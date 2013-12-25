package cn.halen.controller;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.*;
import cn.halen.service.top.TopConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.filter.UserHolder;
import cn.halen.service.TradeService;
import cn.halen.util.Paging;

@Controller
public class TradeController {
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MyLogisticsCompanyMapper myLogisticsCompanyMapper;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;

    @Autowired
    private MyTradeMapper tradeMapper;

    @Autowired
    private TopConfig topConfig;
	
	@Autowired
	private AdminMapper adminMapper;

    private Logger log = LoggerFactory.getLogger(TradeController.class);
	
	//private static final String REDIS_DISTRIBUTOR_LIST = "redis:distributor:list";

    @RequestMapping(value="trade/report")
    public void sendReport(Model model, HttpServletResponse response, @RequestParam(required = false) String date) throws IOException {
        response.setCharacterEncoding("gb2312");
        response.setContentType("multipart/form-data");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");
        if(StringUtils.isNotBlank(date)) {
            try {
                cal.setTime(format1.parse(date));
            } catch (ParseException e) {
            }
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date start = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        List<MyTrade> list = tradeMapper.listSendTrade(start, end);

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = "send-report-" + format.format(start) + ".csv";
        File f = new File(topConfig.getFileReport() + File.separator + fileName);
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "gb2312"));
        String title = "序号,网店名称,订单号,网店单号,类别,年份,商品名称,货号,颜色,规格,数量,分销价,市场价,网店销售价,实收运费,实收金额,买家ID,收件人,手机,电话,地址,快递单号,快递费,邮编,快递公司,发货时间,审核时间,备注,货到付款订单,货到付款手续费,货到付款代收总额,订单状态";
        w.write(title);
        w.write("\r\n");
        StringBuilder builder = new StringBuilder();
        int count = 1;
        for(MyTrade trade : list) {
            boolean isFirst = true;
            for(MyOrder order : trade.getMyOrderList()) {
                builder.delete(0, builder.length());//清空builder
                if(isFirst) {
                    builder.append(count).append(",")
                            .append(trade.getSeller_nick()).append(",")
                            .append("'" + trade.getId()).append(",")
                            .append("'" + trade.getTid()).append(",")
                            .append(",")
                            .append(",")
                            .append(order.getTitle()).append(",")
                            .append(order.getGoods_id()).append(",")
                            .append(order.getSku().getColor()).append(",")
                            .append(order.getSku().getSize()).append(",")
                            .append(order.getQuantity()).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(trade.getBuyer_nick()).append(",")
                            .append(trade.getName()).append(",")
                            .append(trade.getMobile()).append(",")
                            .append(trade.getPhone()).append(",")
                            .append(trade.getState() + " " + trade.getCity() + " " + trade.getDistrict() + " " + trade.getAddress()).append(",")
                            .append(trade.getDelivery_number()).append(",")
                            .append(trade.getDelivery_money()).append(",")
                            .append(trade.getPostcode()).append(",")
                            .append(trade.getDelivery()).append(",")
                            .append(trade.getSend_time()==null?"":format2.format(trade.getSend_time())).append(",")
                            .append(trade.getSubmit_time()==null?"":format2.format(trade.getSubmit_time())).append(",")
                            .append(StringUtils.isBlank(trade.getBuyer_message())?"":trade.getBuyer_message().replaceAll(",", " ")).append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(trade.getTradeStatus().getDesc());
                } else {
                    builder.append("").append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(order.getTitle()).append(",")
                            .append(order.getGoods_id()).append(",")
                            .append(order.getSku().getColor()).append(",")
                            .append(order.getSku().getSize()).append(",")
                            .append(order.getQuantity()).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(0).append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append(",")
                            .append("");
                }
                w.write(builder.toString());
                w.write("\r\n");
                isFirst = false;
            }
            count++;
        }
        w.flush();
        w.close();

        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.flushBuffer();

        OutputStream os=response.getOutputStream();
        InputStream in = new FileInputStream(f);
        try {
            byte[] b=new byte[1024];
            int length;
            while((length=in.read(b))>0){
                os.write(b,0,length);
            }
        }catch (IOException e) {
            log.error("export store failed.", e);
        }finally {
            os.close();
        }
    }
	
    @RequestMapping(value="trade/export_finding")
    public void exportFinding(Model model, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("gb2312");
        response.setContentType("multipart/form-data");
        List<String> status = Arrays.asList(TradeStatus.WaitFind.getStatus());
        List<MyTrade> list = tradeMapper.listWaitFind();

        Date now = new Date();
        String fileName = "jianhuodan-" + now.getTime() + ".csv";
        File f = new File(topConfig.getJianhuodan() + File.separator + fileName);
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "gb2312"));
        String title = "商品名称,货号,条码,颜色,规格,数量,备注";
        w.write(title);
        w.write("\r\n");
        for(MyTrade t : list) {
            List<MyOrder> orderList = t.getMyOrderList();
            for(MyOrder o : orderList) {
                StringBuilder builder = new StringBuilder();
                builder.append(o.getTitle()).append(",")
                        .append("'").append(o.getGoods_id()).append(",")
                        .append("'").append(o.getGoods_id() + o.getSku().getColor_id() + o.getSku().getSize()).append(",")
                        .append(o.getSku().getColor()).append(",")
                        .append(o.getSku().getSize()).append(",")
                        .append(o.getQuantity()).append(",")
                        .append(t.getBuyer_message());
                w.write(builder.toString());
                w.write("\r\n");
            }
        }
        w.flush();
        w.close();

        response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.flushBuffer();

        OutputStream os=response.getOutputStream();
        InputStream in = new FileInputStream(f);
        try {
            byte[] b=new byte[1024];
            int length;
            while((length=in.read(b))>0){
                os.write(b,0,length);
            }
        }catch (IOException e) {
            log.error("export jianhuodan failed.", e);
        }finally {
            os.close();
        }
    }

    @RequestMapping("trade/trade_detail")
    public String tradeDetail(Model model, @RequestParam String id) {
        MyTrade trade = tradeMapper.selectTradeMap(id);
        model.addAttribute("trade", trade);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        return "trade/trade_detail";
    }

    @RequestMapping(value="/trade/trade_detail_json")
    public @ResponseBody MyTrade tradeDetailJson(Model model, @RequestParam String id) {
        if(null==id) {
            return null;
        }
        MyTrade trade = tradeMapper.selectTradeMap(id);
        return trade;
    }

	@RequestMapping(value="trade/trade_list")
	public String list(Model model, HttpServletResponse resp, @RequestParam(value="seller_nick", required=false) String sellerNick,
			@RequestParam(value="dId", required=false) Integer dId,
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="tid", required=false) String tid,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="delivery", required=false) String delivery,
            @RequestParam(value="start", required = false) String start,
            @RequestParam(value="end", required = false) String end,
			@RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="isSubmit", required=false) Integer isSubmit,
            @RequestParam(value="isRefund", required=false) Integer isRefund,
            @RequestParam(value="isSend", required=false) Integer isSend,
            @RequestParam(value="isCancel", required=false) String isCancel,
            @RequestParam(value="isFinish", required=false) Integer isFinish,
            @RequestParam(required = false, defaultValue = "false") String scan,
            @RequestParam(required = false, defaultValue = "false") String map,
            @RequestParam(required = false, defaultValue = "order by t.created") String orderString,
            @RequestParam(required = false) String deliveryNumber) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}

		User currUser = UserHolder.get();
		String currType = currUser.getType();
		if(!currType.equals(UserType.Admin.getValue()) && !currType.equals(UserType.SuperAdmin.getValue()) &&
				!currType.equals(UserType.Distributor.getValue()) && !currType.equals(UserType.WareHouse.getValue()) &&
				!currType.equals(UserType.DistributorManager.getValue()) && !currType.equals(UserType.ServiceStaff.getValue())
                && !currType.equals(UserType.Accounting.getValue())
                && !currType.equals(UserType.GoodsManager.getValue())) {
			model.addAttribute("errorInfo", "对不起，您没有权限查看此页面！");
			return "error_page";
		}

        Date startTime = null;
        Date endTime = null;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        boolean customTime = false;
        //检查日期
        if(StringUtils.isNotEmpty(start) && StringUtils.isNotEmpty(end)) {
            try {
                startTime = format.parse(start);
                endTime = format.parse(end);
            } catch (Exception e) {
                model.addAttribute("errorInfo", "请输入正确的开始时间和结束时间!");
                return "error_page";
            }
            if(endTime.before(startTime)) {
                model.addAttribute("errorInfo", "结束时间不能早于开始时间!");
                return "error_page";
            }
            customTime = true;
        } else {
            endTime = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endTime);
            cal.add(Calendar.MONTH, -3);
            startTime = cal.getTime();
        }

		List<String> sellerNickList = new ArrayList<String>();
		if(currType.equals(UserType.ServiceStaff.getValue())) {
			sellerNickList.add(currUser.getShop().getSeller_nick());
		} else if(currType.equals(UserType.Distributor.getValue())) {
			Distributor d = adminMapper.selectDistributorMapById(currUser.getShop().getD().getId());
			if(StringUtils.isNotEmpty(sellerNick)) {
				boolean valid = false;
				for(Shop s : d.getShopList()) {
					if(sellerNick.equals(s.getSeller_nick())) {
						valid = true;
						break;
					}
				}
				if(!valid) {
					model.addAttribute("errorInfo", "对不起，您没有权限查看此页面！");
					return "error_page";
				} else {
					sellerNickList.add(sellerNick);
				}
			} else {
				for(Shop s : d.getShopList()) {
					sellerNickList.add(s.getSeller_nick());
				}
			}
		} else if(StringUtils.isNotEmpty(sellerNick)) {
			sellerNickList.add(sellerNick);
		} else if(null != dId && dId != -1) {
			Distributor d = adminMapper.selectDistributorMapById(dId);
			for(Shop s : d.getShopList()) {
				sellerNickList.add(s.getSeller_nick());
			}
		}

        List<String> statusList = null;
        if(StringUtils.isNotBlank(status)) {
            statusList = new ArrayList<String>();
            String[] strs = status.split("\\|");
            for(String str : strs) {
                if(StringUtils.isNotBlank(str)) {
                    statusList.add(str);
                }
            }
        }

        List<Integer> cancelList = null;
        if(StringUtils.isNotBlank(isCancel)) {
            cancelList = new ArrayList<Integer>();
            String[] strs = isCancel.split("\\|");
            for(String str : strs) {
                if(StringUtils.isNotBlank(str)) {
                    cancelList.add(Integer.parseInt(str));
                }
            }
        }
		long totalCount = tradeMapper.countTrade(sellerNickList, name, tid, statusList, isSubmit, isRefund, isSend, cancelList, isFinish, delivery, startTime, endTime, deliveryNumber,
                null, null, null, null);
		model.addAttribute("totalCount", totalCount);
        int pageSize = 100;
        if("true".equals(map)) {
            pageSize = 10;
        }
		Paging paging = new Paging(intPage, pageSize, totalCount);
		List<MyTrade> list = Collections.emptyList();
		if(totalCount > 0) {
            boolean bMap = false;
            if("true".equals(map)) {
                bMap = true;
            }
			list = tradeMapper.listTrade(sellerNickList, name, tid, paging, statusList, isSubmit, isRefund, isSend,
                    cancelList, isFinish, delivery, startTime, endTime, bMap, orderString, deliveryNumber, null, null, null, null);
		}
		model.addAttribute("trade_list", list);
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(MyTrade trade : list) {
            if(!first) {
                builder.append(";");
            }
            builder.append(trade.getId());
            first = false;
        }
        model.addAttribute("idList", builder.toString());
		model.addAttribute("paging", paging);
		model.addAttribute("name", name);
		model.addAttribute("tid", tid);
		model.addAttribute("status", status);
        model.addAttribute("isSubmit", isSubmit);
        model.addAttribute("isRefund", isRefund);
        model.addAttribute("isSend", isSend);
        model.addAttribute("isCancel", isCancel);
        model.addAttribute("isFinish", isFinish);
		model.addAttribute("seller_nick", sellerNick);
		model.addAttribute("delivery", delivery);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
		model.addAttribute("dId", dId);
		model.addAttribute("dList", adminMapper.listDistributor());
        model.addAttribute("scan", scan);
        model.addAttribute("map", map);
        model.addAttribute("orderString", orderString);
        if(customTime) {
            model.addAttribute("start", start);
            model.addAttribute("end", end);
        }
		if(null != dId && -1 != dId) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(dId).getShopList());
		}
		if(currType.equals(UserType.Distributor.getValue()) || currType.equals(UserType.ServiceStaff.getValue())) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(currUser.getShop().getD().getId()).getShopList());
		}

		model.addAttribute("sellerInfo", adminMapper.selectSellerInfo());
        if("true".equals(map)) {
            return "trade/trade_map_list";
        } else {
            return "trade/trade_list2";
        }
	}
	
	@RequestMapping(value="fenxiao/add_trade_form")
	public String addTradeForm() {
		return "fenxiao/add_trade_form"; 
	}
	
	@RequestMapping(value="/trade/list_shop")
	public @ResponseBody List<Shop> listShop(Model model, @RequestParam("dId") Integer dId) {
		if(null==dId) {
			return null;
		}
		Distributor d = adminMapper.selectDistributorMapById(dId);
		return d.getShopList();
	}

    @RequestMapping(value="/trade/trade_search")
    public String tradeSearch(Model model, @RequestParam(required = false) String criteria_type,
                              @RequestParam(required = false) String criteria, @RequestParam(required = false, defaultValue = "1") int page) {

        if(StringUtils.isBlank(criteria)) {
            return "trade/trade_search";
        }
        User currUser = UserHolder.get();
        String currType = currUser.getType();
        if(!currType.equals(UserType.Admin.getValue()) && !currType.equals(UserType.SuperAdmin.getValue()) &&
                !currType.equals(UserType.Distributor.getValue()) && !currType.equals(UserType.WareHouse.getValue()) &&
                !currType.equals(UserType.DistributorManager.getValue()) && !currType.equals(UserType.ServiceStaff.getValue())
                && !currType.equals(UserType.Accounting.getValue())
                && !currType.equals(UserType.GoodsManager.getValue())) {
            model.addAttribute("errorInfo", "对不起，您没有权限查看此页面！");
            return "error_page";
        }

        List<String> sellerNickList = new ArrayList<String>();
        if(currType.equals(UserType.ServiceStaff.getValue())) {
            sellerNickList.add(currUser.getShop().getSeller_nick());
        } else if(currType.equals(UserType.Distributor.getValue())) {
            Distributor d = adminMapper.selectDistributorMapById(currUser.getShop().getD().getId());
            for(Shop s : d.getShopList()) {
                sellerNickList.add(s.getSeller_nick());
            }
        }
        String name = null;
        String tid = null;
        String nick = null;
        String mobile = null;
        String phone = null;
        String id = null;
        String deliveryNumber = null;
        if("name".equals(criteria_type)) {
            name = criteria;
        } else if("tid".equals(criteria_type)) {
            tid = criteria;
        } else if("nick".equals(criteria_type)) {
            nick = criteria;
        } else if("mobile".equals(criteria_type)) {
            mobile = criteria;
        } else if("phone".equals(criteria_type)) {
            phone = criteria;
        } else if("id".equals(criteria_type)) {
            id = criteria;
        } else if("delivery_number".equals(criteria_type)) {
            deliveryNumber = criteria;
        }

        long totalCount = tradeMapper.countTrade(sellerNickList, name, tid, null, null, null, null, null, null, null, null, null, deliveryNumber,
                nick, mobile, phone, id);
        model.addAttribute("totalCount", totalCount);
        int pageSize = 100;
        Paging paging = new Paging(page, pageSize, totalCount);
        List<MyTrade> list = Collections.emptyList();
        if(totalCount > 0) {
            list = tradeMapper.listTrade(sellerNickList, name, tid, paging, null, null, null, null,
                    null, null, null, null, null, false, null, deliveryNumber, nick, mobile, phone, id);
        }
        model.addAttribute("trade_list", list);
        model.addAttribute("paging", paging);
        model.addAttribute("criteria", criteria);
        model.addAttribute("criteria_type", criteria_type);
        return "trade/trade_search";
    }
	
}
