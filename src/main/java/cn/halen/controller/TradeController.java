package cn.halen.controller;

import java.io.*;
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
                        .append(o.getGoods_id()).append(",")
                        .append(o.getGoods_id() + o.getSku().getColor_id() + o.getSku().getSize()).append(",")
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
            @RequestParam(required = false, defaultValue = "false") String scan) {
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
			sellerNickList.add(currUser.getShop().getSellerNick());
		} else if(currType.equals(UserType.Distributor.getValue())) {
			Distributor d = adminMapper.selectDistributorMapById(currUser.getShop().getD().getId());
			if(StringUtils.isNotEmpty(sellerNick)) {
				boolean valid = false;
				for(Shop s : d.getShopList()) {
					if(sellerNick.equals(s.getSellerNick())) {
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
					sellerNickList.add(s.getSellerNick());
				}
			}
		} else if(StringUtils.isNotEmpty(sellerNick)) {
			sellerNickList.add(sellerNick);
		} else if(null != dId && dId != -1) {
			Distributor d = adminMapper.selectDistributorMapById(dId);
			for(Shop s : d.getShopList()) {
				sellerNickList.add(s.getSellerNick());
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
		long totalCount = tradeMapper.countTrade(sellerNickList, name, tid, statusList, isSubmit, isRefund, isSend, cancelList, isFinish, delivery, startTime, endTime);
		model.addAttribute("totalCount", totalCount);
		Paging paging = new Paging(intPage, 100, totalCount);
		List<MyTrade> list = Collections.emptyList();
		if(totalCount > 0) {
			list = tradeMapper.listTrade(sellerNickList, name, tid, paging, statusList, isSubmit, isRefund, isSend, cancelList, isFinish, delivery, startTime, endTime);
		}
		model.addAttribute("trade_list", list);
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
		return "trade/trade_list2";
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
	
}
