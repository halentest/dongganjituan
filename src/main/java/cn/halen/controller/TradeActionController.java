package cn.halen.controller;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.halen.data.mapper.*;
import cn.halen.data.pojo.*;
import cn.halen.exception.MyException;
import cn.halen.service.*;
import cn.halen.service.dangdang.DangdangService;
import cn.halen.service.excel.TradeExcelReader;
import cn.halen.service.excel.TradeRow;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.TaoTradeStatus;
import cn.halen.util.Constants;
import cn.halen.util.ErrorInfoHolder;
import com.taobao.api.domain.Area;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.controller.formbean.ClientOrder;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.filter.UserHolder;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.util.MoneyUtils;

import com.taobao.api.ApiException;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TradeActionController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private GoodsMapper goodsMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private AreaMapper areaMapper;

    @Autowired
    private TradeClient tradeClient;
	
	@Autowired
	private MyLogisticsCompanyMapper myLogisticsCompanyMapper;
	
	@Autowired
	private TradeService tradeService;

    @Autowired
    MyTradeMapper tradeMapper;
	
	@Autowired
	private TopConfig topConfig;

    @Autowired
    private SkuService skuService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private DangdangService dangdangService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	private ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<String, String>();
	
	private static final String REDIS_LOGISTICS_CODE = "redis:code";
	
	private static final String REDIS_AREA = "redis:area";

    private static final String KEY_SPACE = "default";

    @RequestMapping(value="trade/action/upload")
    public String upload(Model model) {
        return "trade/upload";
    }

    @RequestMapping(value="trade/action/add_comment_form")
    public String addCommentForm(Model model, @RequestParam String id, @RequestParam String type, @RequestParam(required = false) String from) {
        MyTrade trade = tradeMapper.selectById(id);
        model.addAttribute("trade", trade);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("type", type);
        model.addAttribute("from", from);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        return "trade/add_comment_form";
    }

    @RequestMapping(value="trade/action/add_comment")
    public void addComment(Model model, @RequestParam String id, @RequestParam String comment, @RequestParam String type, @RequestParam(required = false) String from,
                            HttpServletResponse resp) {
        MyTrade trade = tradeMapper.selectById(id);
        if("kefu_memo".equals(type)) {
            trade.setKefu_memo(comment.trim());
        } else if("cangku_memo".equals(type)) {
            trade.setCangku_memo(comment.trim());
        } else if("kefu_msg".equals(type)) {
            trade.setKefu_msg(comment.trim());
        } else if("cangku_msg".equals(type)) {
            trade.setCangku_msg(comment.trim());
        }
        tradeMapper.updateMyTrade(trade);
        try {
            if("list".equals(from)) {
                resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
            } else {
                resp.sendRedirect("/trade/trade_detail?id=" + id);
            }
        } catch (IOException e) {
        }
    }

    @RequestMapping(value="trade/action/cancel_trade_form")
    public String cancelTradeForm(Model model, @RequestParam String id, @RequestParam(value="isApply", required=false) String isApply) {
        MyTrade trade = tradeMapper.selectById(id);
        model.addAttribute("trade", trade);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("isApply", isApply);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        return "trade/cancel_trade_form";
    }

    @RequestMapping(value="trade/action/cancel_trade")
    public void cancelTrade(Model model, @RequestParam String id, @RequestParam(value="why-cancel", required = false) String whyCancel, @RequestParam(value="isApply", required=false) String isApply,
                                HttpServletResponse resp) throws InsufficientStockException {
        MyTrade trade = tradeMapper.selectTradeMap(id);
        if("true".equals(isApply)) {
            if(trade.getIs_cancel()!=0) {
                try {
                    resp.sendRedirect("/trade/trade_detail?id=" + id);
                } catch (IOException e) {
                }
                return;
            }
            trade.setIs_cancel(-1);
        } else {
            if(trade.getIs_cancel()==1) {
                try {
                    resp.sendRedirect("/trade/trade_detail?id=" + id);
                } catch (IOException e) {
                }
                return;
            }
            trade.setIs_cancel(1);
        }
        if(StringUtils.isNotBlank(whyCancel)) {
            trade.setWhy_cancel(whyCancel.trim());
        }
        tradeService.cancel(trade);
        try {
            resp.sendRedirect("/trade/trade_detail?id=" + id);
        } catch (IOException e) {
        }
    }

    @RequestMapping(value="trade/action/do_upload")
    public String doUpload(Model model, @RequestParam("file") MultipartFile file) {
        if(!file.isEmpty()) {
            String type = file.getContentType();
            if(!"application/vnd.ms-excel".equals(type)) {
                model.addAttribute("errorInfo", "选择的文件必须是03版本的excel表格!");
                return "trade/upload";
            }
            File dest = null;
            try {
                String fileName = new String(file.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                dest = new File(topConfig.getFileBatchTrade() + "/" + fileName);
                if(dest.exists()) {
                    model.addAttribute("errorInfo", "这个文件已经存在，不能重复添加!");
                    return "trade/upload";
                }
                byte[] bytes = file.getBytes();
                OutputStream out = new FileOutputStream(dest);
                out.write(bytes);
                out.flush();
                out.close();
                boolean handleResult = handleExcel(model, dest);
                if(!handleResult) {
                    dest.delete();
                    return "trade/upload";
                }
            } catch (Exception e) {
                dest.delete();
                log.error("Upload file failed, ", e);
                model.addAttribute("errorInfo", "上传文件失败，请重试!");
                return "trade/upload";
            }
        } else {
            model.addAttribute("errorInfo", "必须选择一个文件!");
            return "trade/upload";
        }
        model.addAttribute("successInfo", "批量导入订单成功!");
        return "trade/upload";
    }

    private boolean handleExcel(Model model, File file) throws ApiException {
        TradeExcelReader reader = null;
        List<TradeRow> rows = null;
        try {
            reader = new TradeExcelReader(file);
            boolean checkColumn = reader.checkColumn();
            if(!checkColumn) {
                model.addAttribute("errorInfo", "格式不正确，请选择正确的文件!");
                return false;
            }
            rows = reader.getData();
        } catch (Exception e) {
            log.error("Handle excel failed, ", e);
            model.addAttribute("errorInfo", "系统异常，请联系管理员!");
            return false;
        } finally {
            reader.destroy();
        }

        String sellerNick = UserHolder.get().getShop().getSeller_nick();

        List<MyTrade> tList = tradeService.toMyTrade(rows, sellerNick);
        List<Integer> lost = skuService.checkExist(tList);
        List<String> repeated = new ArrayList<String>();
        List<String> successed = new ArrayList<String>();
        for(MyTrade t : tList) {
            t.setStatus(TradeStatus.WaitReceive.getStatus());
            t.setIs_finish(1);
            int result = tradeService.insertMyTrade(t, Constants.QUANTITY, null);
            if(0==result) {
                repeated.add(t.getTid());
            } else {
                successed.add(t.getTid());
            }
        }
        model.addAttribute("lost", lost);
        model.addAttribute("repeated", repeated);
        model.addAttribute("successed", successed);

        return true;
    }

	@RequestMapping(value="trade/action/buy_goods_form")
	public String buyGoodsForm(Model model, @RequestParam(value="orders", required=false) String orders, 
			@RequestParam(value="fromcart", required=false) String fromCart,
            @RequestParam(value="tid", required=false) String tid,
            @RequestParam(value="addGoods", required=false) String addGoods,
            @RequestParam(required = false) String from) {
		User user = UserHolder.get();
		String token = user.getUsername() + System.currentTimeMillis();
		tokens.put(token, "ture");
		model.addAttribute("token", token);
		
		if(null != fromCart) {
			model.addAttribute("fromcart", true);
			model.addAttribute("logistics", myLogisticsCompanyMapper.list());
			return "trade/buy_goods_form";
		}
		if(StringUtils.isEmpty(orders)) {
			model.addAttribute("errorInfo", "请选择要购买的商品！");
			return "error_page";
		}
		List<ClientOrder> orderList = new ArrayList<ClientOrder>();
		
		String[] orderArr = orders.split(":::");
		for(String order : orderArr) {
			String[] items = order.split(",");
			if(items.length != 6) {
				continue;
			}
			ClientOrder clientOrder = new ClientOrder();
			clientOrder.setGoodsId(items[0]);
			clientOrder.setUrl(items[1]);
			clientOrder.setTitle(items[2]);
			clientOrder.setColor(items[3]);
			clientOrder.setSize(items[4]);
			clientOrder.setCount(Integer.parseInt(items[5]));
			orderList.add(clientOrder);
		}
        if(null != addGoods && "true".equals(addGoods)) {
            model.addAttribute("addGoods", true);
        } else {
            model.addAttribute("addGoods", false);
        }
		model.addAttribute("orderList", orderList);
		model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("tid", tid);
        model.addAttribute("from", from);

		return "trade/buy_goods_form";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/buy_goods")
	public String buyGoods(Model model, HttpServletRequest req, HttpServletResponse resp) {
		String token = req.getParameter("token");
		if(tokens.get(token) == null) {
			model.addAttribute("errorInfo", "请不要重复提交表单！");
			return "error_page";
		}
		
		String logistics = req.getParameter("logistics");
		String province = req.getParameter("province");
		String city = req.getParameter("city");
		String district = req.getParameter("district");
		String address = req.getParameter("address");
		String receiver = req.getParameter("receiver");
		String phone = req.getParameter("phone");
		String mobile = req.getParameter("mobile");
		String errorInfo = validateAddress(model, province, city, address, receiver, mobile);
		if(null != errorInfo) {
			model.addAttribute("errorInfo", errorInfo);
			return "error_page";
		}
		String postcode = req.getParameter("postcode");
		String sellerMemo = req.getParameter("seller_memo");
	
		MyTrade trade = new MyTrade();
        String id = tradeMapper.generateId();
        trade.setId(id);
		
		String logisticsCompany = (String) redisTemplate.opsForValue().get(REDIS_LOGISTICS_CODE + ":" + logistics);
		if(null == logisticsCompany) {
			logisticsCompany = myLogisticsCompanyMapper.selectByCode(logistics).getName();
			redisTemplate.opsForValue().set(REDIS_LOGISTICS_CODE + ":" + logistics, logisticsCompany);
		}
		trade.setDelivery(logisticsCompany);

		String provinceName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + province);
		if(null == provinceName) {
			provinceName = areaMapper.selectById(Long.parseLong(province)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + province, provinceName);
		}
		trade.setState(provinceName);
		
		String cityName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + city);
		if(null == cityName) {
			cityName = areaMapper.selectById(Long.parseLong(city)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + city, cityName);
		}
		trade.setCity(cityName);

        if(!"-1".equals(district) && !"选择区".equals(district)) {
            String districtName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + district);
            if(null == districtName) {
                districtName = areaMapper.selectById(Long.parseLong(district)).getName();
                redisTemplate.opsForValue().set(REDIS_AREA + ":" + district, districtName);
            }
            trade.setDistrict(districtName);
        } else {
            trade.setDistrict(" ");
        }
		trade.setAddress(address);
		trade.setPostcode(postcode);
		trade.setSeller_memo(sellerMemo);
		trade.setName(receiver);
		trade.setPhone(phone);
		trade.setMobile(mobile);
		trade.setStatus(TradeStatus.UnSubmit.getStatus());
		trade.setCome_from(Constants.MANAUAL);
        trade.setPay_type(Constants.PAY_TYPE_ONLINE);
		
		boolean hasNext = true;
		
		List<MyOrder> orderList = new ArrayList<MyOrder>();
		int payment = 0;
		
		User currentUser = UserHolder.get();
		float discount = currentUser.getShop().getD().getDiscount();
		trade.setSeller_nick(currentUser.getShop().getSeller_nick());
        trade.setDistrib(currentUser.getShop().getD().getName());

		int count = 0;
		String goodsId = null;
        boolean first = true; //the first goods will be bought
		while(hasNext) {
			String currGoodsId = req.getParameter("goods" + count);
			if(null == currGoodsId) {
				hasNext = false;
				break;
			}
			goodsId = currGoodsId;
			int quantity = 0;
			try {
				quantity = Integer.parseInt(req.getParameter("count" + count));
			} catch(Exception e) {
				model.addAttribute("errorInfo", "商品数量必须填写数字！");
				return "error_page";
			}
			if(quantity <= 0) {
				model.addAttribute("errorInfo", "商品数量不能小于0！");
				return "error_page";
			}
			
			Goods goods = goodsMapper.getByHid(goodsId, true);
			String color = req.getParameter("color" + count);
			String size = req.getParameter("size" + count);
			
			MyOrder myOrder = new MyOrder();
			myOrder.setGoods_id(goodsId);
			myOrder.setColor(color);
			myOrder.setSize(size);
            MySku sku = skuMapper.select(goodsId, color, size);
            myOrder.setSku_id(sku.getId());
			myOrder.setQuantity(quantity);
			int singlePayment = MoneyUtils.cal(goods.getPrice(), discount, quantity);
			payment += singlePayment;
			myOrder.setPayment(singlePayment);
			myOrder.setTitle(goods.getTitle());
			myOrder.setPic_path(goods.getUrl());
			myOrder.setStatus(TaoTradeStatus.WAIT_SELLER_SEND_GOODS.getValue());
			myOrder.setTid(id);
			orderList.add(myOrder);
			count++;

            if(first) {
                trade.setGoods_id(goods.getId());
                trade.setSku_id(sku.getId());
                first = false;
            }
		}
		trade.setMyOrderList(orderList);
		trade.setPayment(payment);
		
		int totalGoods = orderList.size();
		trade.setGoods_count(totalGoods);
		
		trade.setDelivery_money(utilService.calDeliveryMoney(goodsId, totalGoods, logistics, province));

        Map<String, String> idHolder  = new HashMap<String, String>();
		try{
			tradeService.insertMyTrade(trade, Constants.LOCK_QUANTITY, idHolder);
		} catch(Exception e) {
			log.error("", e);
			model.addAttribute("errorInfo", "系统异常，请重试！");
			return "error_page";
		}
		tokens.remove(token);
        try {
            resp.sendRedirect("/trade/trade_detail?id=" + idHolder.get("id"));
            return null;
        } catch (IOException e) {
        }
        return null;
	}

    @SuppressWarnings("unchecked")
    @RequestMapping(value="trade/action/add_goods")
    public String addGoods(Model model, HttpServletRequest req, HttpServletResponse resp, @RequestParam(required = false) String from) {
        String token = req.getParameter("token");
        if(tokens.get(token) == null) {
            model.addAttribute("errorInfo", "请不要重复提交表单！");
            return "error_page";
        }

        String tid = req.getParameter("tid");
        //判断一下订单的状态必须为未提交
        MyTrade t = tradeMapper.selectById(tid);
        if(!t.getStatus().equals(TradeStatus.UnSubmit.getStatus()))  {
            try {
                if("list".equals(from)) {
                    resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
                } else {
                    resp.sendRedirect("/trade/trade_detail?id=" + tid);
                }
                return null;
            } catch (IOException e) {
            }
        }
        boolean hasNext = true;

        List<MyOrder> orderList = new ArrayList<MyOrder>();
        int count = 0;
        String goodsId = null;
        while(hasNext) {
            String currGoodsId = req.getParameter("goods" + count);
            if(null == currGoodsId) {
                hasNext = false;
                break;
            }
            goodsId = currGoodsId;
            int quantity = 0;
            try {
                quantity = Integer.parseInt(req.getParameter("count" + count));
            } catch(Exception e) {
                model.addAttribute("errorInfo", "商品数量必须填写数字！");
                return "error_page";
            }
            if(quantity <= 0) {
                model.addAttribute("errorInfo", "商品数量不能小于0！");
                return "error_page";
            }

            Goods goods = goodsMapper.getByHid(goodsId, true);
            String color = req.getParameter("color" + count);
            String size = req.getParameter("size" + count);

            MyOrder myOrder = new MyOrder();
            myOrder.setGoods_id(goodsId);
            myOrder.setColor(color);
            myOrder.setSize(size);
            MySku sku = skuMapper.select(goodsId, color, size);
            myOrder.setSku_id(sku.getId());
            myOrder.setQuantity(quantity);
            myOrder.setTitle(goods.getTitle());
            myOrder.setPic_path(goods.getUrl());
            myOrder.setTid(tid);
            orderList.add(myOrder);
            count++;
        }
        try{
            for(MyOrder order : orderList) {
                tradeMapper.insertMyOrder(order);
            }
        } catch(Exception e) {
            log.error("", e);
            model.addAttribute("errorInfo", "系统异常，请重试！");
            return "error_page";
        }
        tokens.remove(token);
        try {
            if("list".equals(from)) {
                resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
            } else {
                resp.sendRedirect("/trade/trade_detail?id=" + tid);
            }
            return null;
        } catch (IOException e) {
        }
        return null;
    }

    @RequestMapping(value="trade/action/del_goods")
    public void delGoods(Model model, HttpServletResponse resp, @RequestParam long oid, @RequestParam String tid, @RequestParam(required = false) String from) {
        MyTrade t = tradeMapper.selectById(tid);
        if(t.getIs_submit()==0) {
            tradeMapper.delOrder(oid);
        }
        try {
            if("list".equals(from)) {
                resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
            } else {
                resp.sendRedirect("/trade/trade_detail?id=" + tid);
            }
        } catch (IOException e) {
        }
    }
	
	@RequestMapping(value="trade/action/shopcart")
	public String shopCart(Model model, HttpServletRequest req) {
		return "trade/shop_cart";
	}
	
	public String generateTradeId() {
		UUID uuid = UUID.randomUUID();  
        String str = uuid.toString();  
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);  
        return temp; 
	}

    /**
     * 发放单号
     * @param model
     * @param check 用来判断是否是批量提交，有买家留言的订单不允许批量提交
     * @return
     *
     */
    @RequestMapping(value="trade/action/batch_change_status")
    public @ResponseBody ResultInfo batchChangeStatus(Model model, @RequestParam String ids, @RequestParam("action") String action,
                                                      @RequestParam(required = false) String check) {

        ResultInfo result = new ResultInfo();
        StringBuilder errorInfoBuilder = new StringBuilder();
        if(StringUtils.isNotEmpty(ids)) {
            String[] idArr = ids.split(";");
            for(String id : idArr) {
                if(StringUtils.isNotEmpty(id)) {
                    log.info("try to batch change status({} for id: {})", action, id);
                    try {
                        ErrorInfoHolder eh = new ErrorInfoHolder();
                        if("send".equals(action)) {
                            tradeService.send(id, eh);
                        } else if("submit".equals(action)) {
                            boolean needCheck = false;
                            if(StringUtils.isNotBlank(check) && "true".equals(check)) {
                                needCheck = true;
                            }
                            tradeService.submit(id, eh, needCheck);
                        } else if("find-goods".equals(action)) {
                            tradeService.findGoods(id, eh);
                        }
                        if(StringUtils.isNotBlank(eh.getErrorInfo())) {
                            errorInfoBuilder.append(id).append(":").append(eh.getErrorInfo()).append("\r\n");
                        }
                    } catch (InsufficientBalanceException e) {
                        log.error("batch change status({}) fail for id : {}, {}", action, id, e.getMessage());
                        errorInfoBuilder.append(id).append(":").append("余额不足").append("\r\n");
                    } catch (ApiException e) {
                        log.error("batch change status({}) fail for id : {}, {}", action, id, e.getMessage());
                        errorInfoBuilder.append(id).append(":").append("Api异常").append("\r\n");
                    } catch (InsufficientStockException e) {
                        log.warn("batch change status({}) fail for trade2 id : {}, {}", action, id, e.getMessage());
                        errorInfoBuilder.append(id).append(":").append("库存不足").append("\r\n");
                    } catch(MyException e) {
                        log.error("batch change status({}) fail for id : {}, {}", action, id, e.getMessage());
                        errorInfoBuilder.append(id).append(":").append(e.getMessage()).append("\r\n");
                    } catch(Exception e) {
                        log.error("batch change status error", e);
                    }
                }
            }
            if(StringUtils.isNotBlank(errorInfoBuilder.toString())) {
                result.setSuccess(false);
                result.setErrorInfo(errorInfoBuilder.toString());
            }
        }
        return result;
    }

	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/change_delivery")
	public @ResponseBody ResultInfo changeDelivery(Model model, @RequestParam("id") String id, @RequestParam("delivery") String delivery) {
		ResultInfo result = new ResultInfo();
		try {
			String logisticsCompany = (String) redisTemplate.opsForValue().get(REDIS_LOGISTICS_CODE + ":" + delivery);
			if(null == logisticsCompany) {
				logisticsCompany = myLogisticsCompanyMapper.selectByCode(delivery).getName();
				redisTemplate.opsForValue().set(REDIS_LOGISTICS_CODE + ":" + delivery, logisticsCompany);
			}
			result.setErrorInfo(logisticsCompany);
            MyTrade trade = tradeMapper.selectTradeMap(id);
			int deliveryMoney = 0;
            int quantity = 0;
            for(MyOrder order : trade.getMyOrderList()) {
                quantity += order.getQuantity();
            }
            String goodsId = trade.getMyOrderList().get(0).getGoods_id();
            if(StringUtils.isNotBlank(trade.getState())) {
                deliveryMoney = utilService.calDeliveryMoney(goodsId, quantity, delivery, trade.getState());
            }

			tradeService.changeDelivery(id, logisticsCompany, deliveryMoney);
		} catch (InvalidStatusChangeException isce) {
			result.setSuccess(false);
			result.setErrorInfo("这个订单不能进行此操作!");
		} catch (InsufficientBalanceException ibe) {
			result.setSuccess(false);
			result.setErrorInfo("余额不足，不能进行此操作!");
		} catch (Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}

    @RequestMapping(value="trade/action/change_delivery_number")
    public @ResponseBody ResultInfo changeDeliveryNumber(Model model, @RequestParam("id") String id, @RequestParam() String deliveryNumber) {
        ResultInfo result = new ResultInfo();
        MyTrade trade = tradeMapper.selectById(id);
        trade.setDelivery_number(deliveryNumber);
        tradeMapper.updateMyTrade(trade);
        result.setErrorInfo(deliveryNumber);
        return result;
    }

    /**
     * 扫描单号
     * @param model
     * @return
     */
	@RequestMapping(value="trade/batch_add_tracking_number")
	public @ResponseBody ResultInfo batchAddTrackingNumber(Model model, @RequestParam() String param) {
		ResultInfo result = new ResultInfo();
		try {
            if(StringUtils.isBlank(param)) {
                return result;
            }
            String[] trades = param.split(",");
            for(String trade : trades) {
                if(StringUtils.isBlank(trade)) {
                    continue;
                }
                String[] items = trade.split(":");
                if(items.length != 2) {
                    continue;
                }
                if(StringUtils.isBlank(items[0]) || StringUtils.isBlank(items[1])) {
                    continue;
                }

                tradeService.addTrackingNumber(items[0], items[1]);
            }
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
			return result;
		}
		return result;
	}

    @RequestMapping(value="trade/add_tracking_number")
    public @ResponseBody ResultInfo addTrackingNumber(Model model, @RequestParam("tid") String tid,
                                                      @RequestParam("trackingNumber") String trackingNumber) {
        ResultInfo result = new ResultInfo();
        try {
            tradeService.addTrackingNumber(tid, trackingNumber);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorInfo("系统异常，请重试!");
            return result;
        }
        return result;
    }

	private String validateAddress(Model model, String province, String city, String address
			, String receiver, String mobile) {
		String errorInfo = null;
		if(province.equals("-1")) {
			errorInfo = "请选择省!";
		} else if(city.equals("-1")) {
			errorInfo = "请选择市!";
		} else if(StringUtils.isEmpty(address)) {
			errorInfo = "请填写详细地址!";
		} else if(null==receiver || StringUtils.isEmpty(receiver.trim())) {
			errorInfo = "请填写收货人!";
		} else if(null==mobile || StringUtils.isEmpty(mobile)) {
			errorInfo = "请填写手机号码!";
		}
		return errorInfo;
	}

    @RequestMapping(value="trade/manual_sync_trade_form")
    public String manaualSyncTradeForm(Model model) {

        model.addAttribute("shopList", adminService.getSyncShopList());
        return "trade/manual_sync_trade_form";
    }

	@RequestMapping(value="trade/action/manual_sync_trade")
	public @ResponseBody ResultInfo syncTrade(@RequestParam("sellerNick") String sellerNick,
			@RequestParam("start") String start, @RequestParam("end") String end) {
		ResultInfo result = new ResultInfo();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = format.parse(start);
			endDate = format.parse(end);
			if(startDate.after(endDate)) {
				result.setSuccess(false);
				result.setErrorInfo("结束时间不能早于开始时间!");
				return result;
			}
		} catch(Exception e) {
			log.error("Error while sync trade", e);
			result.setSuccess(false);
			result.setErrorInfo("请输入正确的时间格式!");
			return result;
		}
		Shop shop = adminMapper.selectShopBySellerNick(sellerNick);

		Map<String, Object> counter = null;
        try {
            if(Constants.SHOP_TYPE_TAOBAO.equals(shop.getType()) || Constants.SHOP_TYPE_TIANMAO.equals(shop.getType())) {
                counter = syncTrade(Arrays.asList(topConfig.getToken(sellerNick)), startDate, endDate);
            } else if(Constants.SHOP_TYPE_DANGDANG.equals(shop.getType())) {
                counter = dangdangService.syncTrade(shop, startDate, endDate);
            }
        } catch(Exception e) {
            log.error("sync trade error", e);
            result.setErrorInfo("系统异常，请重试");
            result.setSuccess(false);
            return result;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("代发货订单数：").append(counter.get("Paid")).append("<br>")
                .append("已存在订单数：").append(counter.get("Exist")).append("<br>")
                .append("导入失败订单列表：");
        for(String tid : (ArrayList<String>)counter.get("Fail")) {
            builder.append(tid).append(",");
        }
        builder.append("<br><br>");
        builder.append("导入成功的订单数量：").append(counter.get("Success"));
		result.setErrorInfo(builder.toString());
		return result;
	}

    public Map<String, Object> syncTrade(List<String> tokenList, Date startDate, Date endDate) {
        Map<String, Object> counter = new HashMap<String, Object>();
        List<Trade> tradeList = Collections.EMPTY_LIST;
        try {
            tradeList = tradeClient.queryTradeList(tokenList, startDate, endDate);
        } catch (ParseException e) {
            log.error("", e);
        } catch (ApiException e) {
            log.error("", e);
        }
        counter.put("Paid", tradeList.size()); //已付款订单数量

        int success = 0;
        int existCount = 0;
        List<String> fail = new ArrayList<String>();
        for(Trade trade : tradeList) {
            //check trade if exists
            boolean exist = tradeMapper.checkTidExist(String.valueOf(trade.getTid()));
            if(exist) {
                existCount++;
                continue;
            }
            Trade tradeDetail = null;
            try {
                tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getToken(trade.getSellerNick()));
            } catch (ApiException e) {
                log.error("", e);
            }

            //检查订单是否退款
            Iterator<Order> it = tradeDetail.getOrders().iterator();
            while(it.hasNext()) {
                Order o = it.next();
                if(!"NO_REFUND".equals(o.getRefundStatus())) {
                    it.remove();
                }
            }
            if(tradeDetail.getOrders().size() == 0) {
                tradeDetail = null;
            }

            if(null == tradeDetail) {
                fail.add(String.valueOf(trade.getTid()));
                continue;
            }
            MyTrade myTrade = null;
            try {
                myTrade = tradeService.toMyTrade(tradeDetail);
            } catch (ApiException e) {
                log.error("", e);
            }
            if(null == myTrade || !myTrade.isSuccess()) {
                fail.add(String.valueOf(trade.getTid()));
            }
            if(null == myTrade) {
                continue;
            }
            myTrade.setStatus(TradeStatus.UnSubmit.getStatus());
            int count = 0;
            try {
                count = tradeService.insertMyTrade(myTrade, Constants.LOCK_QUANTITY, null);
            } catch (ApiException e) {
                log.error("", e);
            }
            success += count;
        }
        counter.put("Success", success); //导入成功的订单
        counter.put("Exist", existCount);
        counter.put("Fail", fail);
        log.info("fail is {}", fail.toString());

        return counter;
    }

    @RequestMapping(value="trade/action/modify_receiver_info_form")
    public String modifyReceiverInfoForm(Model model, @RequestParam("id") String id, @RequestParam(required = false) String from) {

        MyTrade trade = tradeMapper.selectTradeMap(id);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("trade", trade);
        model.addAttribute("from", from);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        return "trade/modify_receiver_info_form";
    }

    @RequestMapping(value="trade/action/modify_receiver_info")
    public String modifyReceiverInfo(Model model, HttpServletRequest req, HttpServletResponse resp) {

        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String address = req.getParameter("address");
        String receiver = req.getParameter("receiver");
        String phone = req.getParameter("phone");
        String mobile = req.getParameter("mobile");
        String id = req.getParameter("id");
        String from = req.getParameter("from");
        String errorInfo = validateAddress(model, province, city, address, receiver, mobile);
        if(null != errorInfo) {
            model.addAttribute("errorInfo", errorInfo);
            return "error_page";
        }
        String postcode = req.getParameter("postcode");

        String provinceName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + province);
        if(null == provinceName) {
            provinceName = areaMapper.selectById(Long.parseLong(province)).getName();
            redisTemplate.opsForValue().set(REDIS_AREA + ":" + province, provinceName);
        }

        String cityName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + city);
        if(null == cityName) {
            cityName = areaMapper.selectById(Long.parseLong(city)).getName();
            redisTemplate.opsForValue().set(REDIS_AREA + ":" + city, cityName);
        }

        String districtName = " ";
        if(!"-1".equals(district) && !"选择区".equals(district)) {
            districtName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + district);
            if(null == districtName) {
                districtName = areaMapper.selectById(Long.parseLong(district)).getName();
                redisTemplate.opsForValue().set(REDIS_AREA + ":" + district, districtName);
            }
        }
        int count = tradeMapper.updateLogisticsAddress(provinceName, cityName, districtName, address, mobile, phone,
                postcode, receiver, new Date(), id);
        //如果是顺丰快递，需要清空之前的下单信息
        MyTrade trade = tradeMapper.selectById(id);
        if("顺丰速运".equals(trade.getDelivery())) {
            trade.setDelivery_number("");
            trade.setOrigincode("");
            trade.setDestcode("");
            trade.setSf_status(0);
            tradeMapper.updateMyTrade(trade);

            String path = req.getServletContext().getRealPath("img/sf/" + id + ".png");
            File f = new File(path);
            if(f.exists()) {
                f.delete();
            }
        }
        if(count > 0) {
            try {
                if("list".equals(from)) {
                    resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
                } else {
                    resp.sendRedirect("/trade/trade_detail?id=" + id);
                }
                return null;
            } catch (IOException e) {
            }
        } else {
            model.addAttribute("info", "修改收货地址失败!");
        }
        return "success_page";
    }

    @RequestMapping(value="trade/action/pause")
    public @ResponseBody ResultInfo pause(@RequestParam("id") String id, @RequestParam String action) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        MyTrade trade = tradeMapper.selectById(id);
        if(!trade.getStatus().equals(TradeStatus.UnSubmit.getStatus()) && !trade.getStatus().equals(TradeStatus.WaitOut.getStatus())) {
            resultInfo.setErrorInfo("此订单不能暂停或者取消暂停");
            resultInfo.setSuccess(false);
            return resultInfo;
        }
        if("pause".equals(action)) {
            trade.setIs_pause(1);
            tradeMapper.updateMyTrade(trade);
        } else {
            trade.setIs_pause(0);
            tradeMapper.updateMyTrade(trade);
        }
        return resultInfo;
    }

    @RequestMapping(value="test/trade/mock_trade")
    public void mockTrade(Model model, @RequestParam int num, HttpServletResponse resp) throws ApiException, IOException {
        int totalCount = 0;
        List<MySku> skuList = skuMapper.selectAll();
        int skuListSize = skuList.size();
        List<Shop> shopList = adminMapper.selectAllShop();
        int shopListSize = shopList.size();
        Random random = new Random();
        Date start = new Date();
        List<Area> states = areaMapper.listByType(2);
        for(int i=0; i<num; i++) {

            //随机选一个sku
            MySku sku = skuList.get(random.nextInt(skuListSize));
            //随机选一个shop
            Shop shop = shopList.get(random.nextInt(shopListSize));
            //随机选择一个省
            Area state = states.get(random.nextInt(states.size()));
            List<Area> cities = areaMapper.listByParent(state.getId());
            Area city = cities.get(random.nextInt(cities.size()));
            List<Area> districts = areaMapper.listByParent(city.getId());
            Area district = null;
            if(districts.size()>0) {
                district = districts.get(random.nextInt(districts.size()));
            } else {
                district = new Area();
                district.setName("其他区");
            }

            MyTrade myTrade = new MyTrade();
            String id = tradeMapper.generateId();
            myTrade.setId(id);

            List<MyOrder> myOrderList = new ArrayList<MyOrder>();

            MyOrder myOrder = new MyOrder();
            myOrder.setTid(id);
            myOrder.setSku_id(sku.getId());
            myOrder.setGoods_id(sku.getGoods_id());
            myOrder.setTitle("");
            myOrder.setPic_path("");
            myOrder.setQuantity(1);
            myOrder.setPrice(0);
            myOrder.setPayment(0);
            myOrderList.add(myOrder);

            //set order info to trade so as to be able to order by it.
            myTrade.setSku_id(sku.getId());
            Goods goods = goodsMapper.getByHid(sku.getGoods_id(), true);
            myTrade.setGoods_id(goods.getId());

            myTrade.setTid(id);
            myTrade.setName("name" + random.nextInt(1000));
            myTrade.setPhone("phone" + random.nextInt(1000));
            myTrade.setMobile("mobile" + random.nextInt(1000));
            myTrade.setState(state.getName());
            myTrade.setCity(city.getName());
            myTrade.setDistrict(district.getName());
            myTrade.setAddress("address" + random.nextInt(1000));
            myTrade.setPostcode("");
            myTrade.setSeller_memo("");
            myTrade.setBuyer_message("");
            myTrade.setSeller_nick(shop.getSeller_nick());
            myTrade.setBuyer_nick("");
            myTrade.setCome_from(Constants.MANAUAL);
            myTrade.setModified(new Date());
            myTrade.setCreated(new Date());
            myTrade.setMyOrderList(myOrderList);
            myTrade.setPay_type(Constants.PAY_TYPE_ONLINE); //目前只支持淘宝的在线支付订单
            MyLogisticsCompany mc = myLogisticsCompanyMapper.select(1);
            myTrade.setDelivery(mc.getName());
            myTrade.setStatus(TradeStatus.UnSubmit.getStatus());
            myTrade.setStatus(TradeStatus.UnSubmit.getStatus());
            int count = tradeService.insertMyTrade(myTrade, Constants.LOCK_QUANTITY, null);
            totalCount += count;
        }
        Date end = new Date();
        long clapsed = end.getTime() - start.getTime();
        Writer writer = resp.getWriter();
        writer.write("total " + totalCount + ", time " + clapsed);
        writer.flush();
        writer.close();

    }

    @RequestMapping(value="trade/action/modify_delivery_info_form")
    public String modifyDeliveryInfoForm(Model model, @RequestParam("id") String id, @RequestParam(required = false) String from) {

        MyTrade trade = tradeMapper.selectTradeMap(id);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("trade", trade);
        model.addAttribute("from", from);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        return "trade/modify_delivery_info_form";
    }

    @RequestMapping(value="trade/action/modify_delivery_info")
    public String modifyDeliveryInfo(Model model, HttpServletRequest req, HttpServletResponse resp) {

        String cargo = req.getParameter("cargo");
        String parcelQuantity = req.getParameter("parcel_quantity");
        boolean isInsure = StringUtils.isNotBlank(req.getParameter("is_insure")) && req.getParameter("is_insure").equals("on");
        String insureValue = req.getParameter("insure_value");
        boolean receiverPay = StringUtils.isNotBlank(req.getParameter("receiver_pay")) && req.getParameter("receiver_pay").equals("on");
        String payMethod = req.getParameter("pay_method");
        String id = req.getParameter("id");
        String from = req.getParameter("from");
        int pQuantity = 0;
        int iValue = 0;
        try {
            pQuantity = Integer.parseInt(parcelQuantity);
            iValue = Integer.parseInt(insureValue);
            if(pQuantity < 1) {
                model.addAttribute("errorInfo", "包裹数量填写不正确");
                return "error_page";
            }
        } catch (Exception e) {
            model.addAttribute("errorInfo", "包裹数量或保价金额填写不正确");
            return "error_page";
        }
        MyTrade trade = tradeMapper.selectById(id);
        trade.setCargo(cargo);
        trade.setParcel_quantity(pQuantity);
        trade.setIs_insure(isInsure?1 : 0);
        trade.setInsure_value(iValue * 100);
        trade.setPay_type(receiverPay?1 : 4);
        trade.setPay_method(Integer.valueOf(payMethod));
        int count = tradeMapper.updateMyTrade(trade);
        if(count > 0) {
            try {
                if("list".equals(from)) {
                    resp.sendRedirect("/trade/trade_list?isCancel=0&isSubmit=0&isFinish=0&map=true");
                } else {
                    resp.sendRedirect("/trade/trade_detail?id=" + id);
                }
                return null;
            } catch (IOException e) {
            }
        } else {
            model.addAttribute("info", "修改收货地址失败!");
        }
        return "success_page";
    }

}
