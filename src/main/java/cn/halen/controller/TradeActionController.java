package cn.halen.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import cn.halen.data.mapper.*;
import cn.halen.data.pojo.*;
import cn.halen.service.SkuService;
import cn.halen.service.excel.ExcelReader;
import cn.halen.service.excel.Row;
import cn.halen.service.excel.TradeExcelReader;
import cn.halen.service.excel.TradeRow;
import cn.halen.util.Constants;
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
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;
import cn.halen.service.UtilService;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.domain.Status;
import cn.halen.service.top.util.MoneyUtils;

import com.taobao.api.ApiException;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class TradeActionController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private MySkuMapper skuMapper;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private AreaMapper areaMapper;
	
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
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	private ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<String, String>();
	
	private static final String REDIS_LOGISTICS_CODE = "redis:code";
	
	private static final String REDIS_AREA = "redis:area";

    @RequestMapping(value="trade/action/upload")
    public String upload(Model model) {
        return "trade/upload";
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

        String sellerNick = UserHolder.get().getShop().getSellerNick();

        List<MyTrade> tList = tradeService.toMyTrade(rows, sellerNick);
        List<Integer> lost = skuService.checkExist(tList);
        List<String> repeated = new ArrayList<String>();
        List<String> successed = new ArrayList<String>();
        for(MyTrade t : tList) {
            t.setMy_status(MyStatus.WaitReceive.getStatus());
            int result = tradeService.insertMyTrade(t, true, Constants.QUANTITY);
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
			@RequestParam(value="fromcart", required=false) String fromCart) {
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
		model.addAttribute("orderList", orderList);
		model.addAttribute("logistics", myLogisticsCompanyMapper.list());
		
		return "trade/buy_goods_form";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/buy_goods")
	public String buyGoods(Model model, HttpServletRequest req) {
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
		String errorInfo = validateAddress(model, province, city, district, address, receiver, mobile);
		if(null != errorInfo) {
			model.addAttribute("errorInfo", errorInfo);
			return "error_page";
		}
		String postcode = req.getParameter("postcode");
		String sellerMemo = req.getParameter("seller_memo");
	
		MyTrade trade = new MyTrade();
		
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
		
		String districtName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + district);
		if(null == districtName) {
			districtName = areaMapper.selectById(Long.parseLong(district)).getName();
			redisTemplate.opsForValue().set(REDIS_AREA + ":" + district, districtName);
		}
		trade.setDistrict(districtName);
		trade.setAddress(address);
		trade.setPostcode(postcode);
		trade.setSeller_memo(sellerMemo);
		trade.setName(receiver);
		trade.setPhone(phone);
		trade.setMobile(mobile);
		trade.setMy_status(MyStatus.New.getStatus());
		trade.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
		trade.setCome_from(Constants.MANAUAL);
        trade.setPay_type(Constants.PAY_TYPE_ONLINE);
		
		boolean hasNext = true;
		
		List<MyOrder> orderList = new ArrayList<MyOrder>();
		int payment = 0;
		
		User currentUser = UserHolder.get();
		float discount = currentUser.getShop().getD().getDiscount();
		trade.setSeller_nick(currentUser.getShop().getSellerNick());
		String tradeId = this.generateTradeId();
		trade.setTid(tradeId);
		
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
			
			Goods goods = goodsMapper.getByHid(goodsId);
			String color = req.getParameter("color" + count);
			String size = req.getParameter("size" + count);
			
			MyOrder myOrder = new MyOrder();
			myOrder.setGoods_id(goodsId);
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setQuantity(quantity);
			int singlePayment = MoneyUtils.cal(goods.getPrice(), discount, quantity);
			payment += singlePayment;
			myOrder.setPayment(singlePayment);
			myOrder.setTitle(goods.getTitle());
			myOrder.setPic_path(goods.getUrl());
			myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
			myOrder.setTid(tradeId);
			myOrder.setOid(this.generateTradeId());
			orderList.add(myOrder);
			count++;
		}
		trade.setMyOrderList(orderList);
		trade.setPayment(payment);
		
		int totalGoods = orderList.size();
		trade.setGoods_count(totalGoods);
		
		trade.setDelivery_money(utilService.calDeliveryMoney(goodsId, totalGoods, logistics, province));
		
		try{
			tradeService.insertMyTrade(trade, false, Constants.LOCK_QUANTITY);
		} catch(Exception e) {
			log.error("", e);
			model.addAttribute("errorInfo", "系统异常，请重试！");
			return "error_page";
		}
		tokens.remove(token);
		return "trade/buy_goods_result";
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
	
	@RequestMapping(value="trade/action/batch_change_status")
	public @ResponseBody ResultInfo batchChangeStatus(Model model, @RequestParam("tids") String tids, @RequestParam("action") String action) {
		ResultInfo result = new ResultInfo();
		if(StringUtils.isNotEmpty(tids)) {
			String[] tidArr = tids.split(";");
			try {
				if(action.equals("approve1")) {
					for(String tid : tidArr) {
						if(StringUtils.isNotEmpty(tid)) {
							tradeService.approve1(tid);
						}
					}
				} else if(action.equals("submit")) {
					for(String tid : tidArr) {
						if(StringUtils.isNotEmpty(tid)) {
							tradeService.submit(tid);
						}
					}
				} else if(action.equals("find-goods")) {
					for(String tid : tidArr) {
						if(StringUtils.isNotEmpty(tid)) {
							tradeService.findGoods(tid);
						}
					}
				} 
			} catch (InvalidStatusChangeException isce) {
				result.setSuccess(false);
				result.setErrorInfo("这个订单" + isce.getTid() + "不能进行此操作!");
			} catch(InsufficientBalanceException ibe) {
				log.error("", ibe);
				result.setSuccess(false);
				result.setErrorInfo("余额不足，请打款！");
			} catch (Exception e) {
				log.error("", e);
				result.setSuccess(false);
				result.setErrorInfo("系统异常，请重试!");
			}
		}
		return result;
	}
	
	@RequestMapping(value="trade/action/change_status")
	public @ResponseBody ResultInfo changeStatus(Model model, @RequestParam("tid") String tid,
                                                 @RequestParam("oid") String oid, @RequestParam("action") String action) {
		ResultInfo result = new ResultInfo();
		try {
			if(action.equals("cancel")) {
				tradeService.cancel(tid);
			} else if(action.equals("approve1")) {
				tradeService.approve1(tid);
			} else if(action.equals("submit")) {
				tradeService.submit(tid);
			} else if(action.equals("find-goods")) {
				tradeService.findGoods(tid);
			} else if(action.equals("no-goods")) {
				tradeService.noGoods(tid, oid);
			} else if(action.equals("refund-success")) {
				tradeService.refundSuccess(tid);
			}
		} catch (InvalidStatusChangeException isce) {
			result.setSuccess(false);
			result.setErrorInfo("这个订单不能进行此操作!");
		} catch(InsufficientStockException ise) {
			result.setSuccess(false);
			result.setErrorInfo("库存不足，不能购买！");
		} catch(InsufficientBalanceException ibe) {
			log.error("", ibe);
			result.setSuccess(false);
			result.setErrorInfo("余额不足，请打款！");
		} catch (Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/action/change_delivery")
	public @ResponseBody ResultInfo changeDelivery(Model model, @RequestParam("tid") String tid, @RequestParam("delivery") String delivery,
			@RequestParam("quantity") int quantity, @RequestParam("province") String province, @RequestParam("goods") String goods) {
		ResultInfo result = new ResultInfo();
		try {
			String logisticsCompany = (String) redisTemplate.opsForValue().get(REDIS_LOGISTICS_CODE + ":" + delivery);
			if(null == logisticsCompany) {
				logisticsCompany = myLogisticsCompanyMapper.selectByCode(delivery).getName();
				redisTemplate.opsForValue().set(REDIS_LOGISTICS_CODE + ":" + delivery, logisticsCompany);
			}
			result.setErrorInfo(logisticsCompany);
			int deliveryMoney = 0;
            if(StringUtils.isNotBlank(province)) {
                deliveryMoney = utilService.calDeliveryMoney(goods, quantity, delivery, province);
            }

			tradeService.changeDelivery(tid, logisticsCompany, deliveryMoney);
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

    /**
     * 扫描单号
     * @param model
     * @param tid
     * @param delivery
     * @param trackingNumber 快递单号
     * @return
     */
	@RequestMapping(value="trade/add_tracking_number")
	public @ResponseBody ResultInfo addTrackingNumber(Model model, @RequestParam("tid") String tid, @RequestParam("delivery") String delivery,
			@RequestParam("trackingNumber") String trackingNumber) {
		ResultInfo result = new ResultInfo();
		try {
			tradeService.addTrackingNumber(tid, delivery, trackingNumber);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
			return result;
		}
		return result;
	}

    /**
     * 发放单号
     * @param model
     * @param tids
     * @return
     */
    @RequestMapping(value="trade/action/delivery_tracking_number")
    public @ResponseBody ResultInfo send(Model model, @RequestParam("tids") String tids) {

        ResultInfo result = new ResultInfo();
        if(StringUtils.isNotEmpty(tids)) {
            String[] tidArr = tids.split(";");
            try {
                for(String tid : tidArr) {
                    if(StringUtils.isNotEmpty(tid)) {
                        String errorInfo = tradeService.send(tid);
                        if(StringUtils.isNotBlank(errorInfo)) {
                            result.setSuccess(false);
                            result.setErrorInfo(errorInfo);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("", e);
                result.setSuccess(false);
                result.setErrorInfo("系统异常，请重试!");
            }
        }
        return result;
    }
	
	private String validateAddress(Model model, String province, String city, String district, String address
			, String receiver, String mobile) {
		String errorInfo = null;
		if(province.equals("-1")) {
			errorInfo = "请选择省!";
		} else if(city.equals("-1")) {
			errorInfo = "请选择市!";
		} else if(district.equals("-1")) {
			errorInfo = "请选择地区!";
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
        List<Shop> allSyncShop = adminMapper.selectShop(1, null, null);
        List<String> allSyncSellerNick = new ArrayList<String>();
        for(Shop shop : allSyncShop) {
            allSyncSellerNick.add(shop.getSellerNick());
        }
        List<Shop> validShopList = new ArrayList<Shop>();
        User user = UserHolder.get();
        if(user.getUserType()==UserType.Distributor) {
            List<Shop> currShopList = adminMapper.selectDistributorMapById(user.getShop().getD().getId()).getShopList();
            for(Shop shop : currShopList) {
                if(allSyncSellerNick.contains(shop.getSellerNick())) {
                    validShopList.add(shop);
                }
            }
        } else if(user.getUserType()==UserType.ServiceStaff) {
            Shop shop = user.getShop();
            if(allSyncSellerNick.contains(shop.getSellerNick())) {
                validShopList.add(shop);
            }
        }
        model.addAttribute("shopList", validShopList);
        return "trade/manual_sync_trade_form";
    }

	@RequestMapping(value="trade/action/manual_sync_trade")
	public @ResponseBody ResultInfo syncTrade(@RequestParam("sellerNick") String sellerNick,
			@RequestParam("start") String start, @RequestParam("end") String end) throws IOException, ServletException, JSONException, ParseException, InsufficientStockException, InsufficientBalanceException {
		ResultInfo result = new ResultInfo();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
		
		int count = 0;
		try {
			count = tradeService.initTrades(Arrays.asList(topConfig.getToken(sellerNick)), startDate, endDate);
		} catch (ApiException e) {
			log.error("Error while sync trade", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，同步失败");
			return result;
		}
		log.info("Success sync trade {}", count);
		result.setErrorInfo("成功导入" + count + "条交易信息");
		return result;
	}

    @RequestMapping(value="trade/action/modify_receiver_info_form")
    public String modifyReceiverInfoForm(Model model, @RequestParam("tid") String tid) {

        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("tid", tid);
        return "trade/modify_receiver_info_form";
    }

    @RequestMapping(value="trade/action/modify_receiver_info")
    public String modifyReceiverInfo(Model model, HttpServletRequest req) {

        String province = req.getParameter("province");
        String city = req.getParameter("city");
        String district = req.getParameter("district");
        String address = req.getParameter("address");
        String receiver = req.getParameter("receiver");
        String phone = req.getParameter("phone");
        String mobile = req.getParameter("mobile");
        String tid = req.getParameter("tid");
        String errorInfo = validateAddress(model, province, city, district, address, receiver, mobile);
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

        String districtName = (String) redisTemplate.opsForValue().get(REDIS_AREA + ":" + district);
        if(null == districtName) {
            districtName = areaMapper.selectById(Long.parseLong(district)).getName();
            redisTemplate.opsForValue().set(REDIS_AREA + ":" + district, districtName);
        }
        int count = tradeMapper.updateLogisticsAddress(provinceName, cityName, districtName, address, mobile, phone,
                postcode, receiver, new Date(), tid);
        if(count > 0) {
            model.addAttribute("info", "修改收货地址成功!");
        } else {
            model.addAttribute("info", "修改收货地址失败!");
        }
        return "success_page";
    }

}
