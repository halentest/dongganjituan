package cn.halen.controller;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.halen.data.mapper.MigrationMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.data.pojo.migration.Order1;
import cn.halen.data.pojo.migration.Order2;
import cn.halen.data.pojo.migration.Trade1;
import cn.halen.data.pojo.migration.Trade2;
import cn.halen.service.top.*;
import cn.halen.util.Constants;
import com.taobao.api.domain.Trade;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;

import com.taobao.api.ApiException;

@Controller
public class RedirectController {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private LogisticsCompanyClient logisticsCompanyClient;
	
	@Autowired
	private ItemClient itemClient;
	
	@Autowired
	private AreaClient areaClient;

    @Autowired
    private TradeClient tradeClient;
	
	@Autowired
	private TopConfig topConfig;

    @Autowired
    private MyTradeMapper tradeMapper;

    @Autowired
    private MigrationMapper migrationMapper;

    @Autowired
    private TradeService tradeService;

    @RequestMapping(value = "/test")
    public void test() throws ApiException {
        MyTrade t = new MyTrade();
        String id = "201310171155000035";
        t.setName("name");
        t.setMobile("13344");
        t.setAddress("address");
        t.setId(id);
        MyOrder o = new MyOrder();
        o.setTid(id);
        o.setSku_id(2);
        t.addOrder(o);
        tradeService.insertMyTrade(t, 0, null);
    }

    @RequestMapping(value = "/migration2")
    public void migration2(HttpServletResponse resp) throws IOException {
        List<String> list = migrationMapper.selectAllTid();
        for(String s : list) {
            if(StringUtils.isNotBlank(s)) {
                String[] arr = s.split(",");
                for(String a : arr) {
                    if(StringUtils.isNotBlank(a)) {
                        tradeMapper.insertTid(a);
                    }
                }
            }
        }
        Writer writer = resp.getWriter();
        writer.write("success");
        writer.flush();
        writer.close();
    }

    @RequestMapping(value="/migration")
    public String migration() {
        List<Trade1> t1List = migrationMapper.selectTrade1();
        for(Trade1 t1 : t1List) {
            Trade2 t2 = new Trade2();
            if(t1.getCome_from().equals("批量倒入")) {
                t2.setIs_finish(1);
                t2.setStatus(TradeStatus.WaitReceive.getStatus());
            } else if(t1.getStatus().equals("WAIT_SELLER_SEND_GOODS") && t1.getMy_status()==0) {
                t2.setStatus(TradeStatus.UnSubmit.getStatus());
            } else if(t1.getStatus().equals("WAIT_SELLER_SEND_GOODS") && t1.getMy_status()==2) {
                t2.setIs_submit(1);
                t2.setStatus(TradeStatus.WaitSend.getStatus());
            } else if(t1.getStatus().equals("WAIT_SELLER_SEND_GOODS") && t1.getMy_status()==3) {
                t2.setIs_submit(1);
                t2.setStatus(TradeStatus.WaitFind.getStatus());
            } else if(t1.getStatus().equals("WAIT_SELLER_SEND_GOODS") && t1.getMy_status()==6) {
                t2.setIs_submit(1);
                t2.setStatus(TradeStatus.WaitOut.getStatus());
            } else if(t1.getMy_status()==4) {
                t2.setIs_submit(1);
                t2.setIs_send(1);
                t2.setStatus(TradeStatus.WaitReceive.getStatus());
            } else if(t1.getMy_status()==-5 || t1.getMy_status()==-6) {
                t2.setIs_cancel(1);
            } else if(t1.getMy_status()==-1) {
                t2.setIs_cancel(1);
            }
            t2.setAddress(t1.getAddress());
            t2.setArea_id(t1.getArea_id());
            t2.setBuyer_message(t1.getBuyer_message());
            t2.setCity(t1.getCity());
            t2.setCome_from(t1.getCome_from());
            t2.setCreated(t1.getCreated());
            t2.setDelivery(t1.getDelivery());
            t2.setDelivery_money(t1.getDelivery_money());
            t2.setDelivery_number(t1.getDelivery_number());
            t2.setDistributor_id(t1.getDistributor_id());
            t2.setDistrict(t1.getDistrict());
            t2.setCity(t1.getCity());
            t2.setGoods_count(t1.getGoods_count());
            t2.setId(t1.getTid());
            t2.setMobile(t1.getMobile());
            t2.setModified(t1.getModified());
            t2.setName(t1.getName());
            t2.setPay_type(t1.getPay_type());
            t2.setPayment(t1.getPayment());
            t2.setPhone(t1.getPhone());
            t2.setPostcode(t1.getPostcode());
            t2.setReturn_order(t1.getReturn_order());
            t2.setSeller_memo(t1.getSeller_memo());
            t2.setMobile(t1.getMobile());
            t2.setSeller_nick(t1.getSeller_nick());
            t2.setState(t1.getState());
            t2.setTemplate_id(t1.getTemplate_id());
            t2.setTid(t1.getTid());
            t2.setTotal_weight(t1.getTotal_weight());
            migrationMapper.insertTrade2(t2);
            List<Order1> o1List = migrationMapper.selectOrder1(t1.getTid());
            for(Order1 o1 : o1List) {
                Order2 o2 = new Order2();
                o2.setTid(t2.getId());
                o2.setColor(o1.getColor());
                o2.setGoods_id(o1.getGoods_id());
                o2.setModified(o1.getModified());
                o2.setPayment(o1.getPayment());
                o2.setPic_path(o1.getPic_path());
                o2.setPrice(o1.getPrice());
                o2.setQuantity(o1.getQuantity());
                o2.setTitle(o1.getTitle());
                o2.setSize(o1.getSize());
                o2.setPrice(o1.getPrice());
                o2.setSku_id(o1.getSku_id());
                migrationMapper.insertOrder2(o2);
            }
        }
        return "migration";
    }
	
	@RequestMapping(value="/login")
	public String login(Model model) {
		return "login";
	}

    @RequestMapping(value="/set_print")
    public String setPrint(Model model, @RequestParam("delivery") String delivery) {
        model.addAttribute("delivery", delivery);
        return "set_print";
    }
	
	@RequestMapping(value="/access_denied")
	public String accessDenied() {
		return "access_denied";
	}
	
	@RequestMapping(value="/")
	public String main(Model model, HttpServletRequest req) {
		return "templates/frame";
	}
	
	@RequestMapping(value="/admin/action/system_init")
	public String systemInit(Model model, HttpServletRequest req) {
		return "admin/system_init";
	}
	
	@RequestMapping(value="/index")
	public String index(Model model, HttpServletRequest req) {
		return "templates/frame";
	}

    @RequestMapping(value="/left_trade")
    public String left(Model model, HttpServletRequest req) {
        return "templates/left_trade";
    }

    @RequestMapping(value="/left2")
    public String left2(Model model, HttpServletRequest req) {
        return "templates/left2";
    }

    @RequestMapping(value="/left3")
    public String left3(Model model, HttpServletRequest req) {
        return "templates/left3";
    }

    @RequestMapping(value="/left4")
    public String left4(Model model, HttpServletRequest req) {
        return "templates/left4";
    }

    @RequestMapping(value="/left5")
    public String left5(Model model, HttpServletRequest req) {
        return "templates/left5";
    }

    @RequestMapping(value="/head")
    public String head(Model model, HttpServletRequest req) {

        return "templates/head";
    }

    @RequestMapping(value="/list")
    public String list(Model model, HttpServletRequest req) {
        return "templates/list";
    }
	
	@RequestMapping(value="/login_failed")
	public String loginFailed(Model model, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		model.addAttribute("error", "true");
		return "login";
	}
	
	@RequestMapping(value="/admin/sync_logistics")
	public @ResponseBody ResultInfo syncLogistics() throws IOException, ServletException {
		
		ResultInfo result = new ResultInfo();
		int count = 0;
		try {
			count = logisticsCompanyClient.import2db();
		} catch (ApiException e) {
			log.error("Error while sync logistics", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，更新失败");
			return result;
		}
		log.info("Success sync logistics {}", count);
		result.setErrorInfo("成功导入" + count + "条物流信息");
		return result;
	}
	
	@RequestMapping(value="/admin/sync_item")
	public @ResponseBody ResultInfo syncItem() throws IOException, ServletException, JSONException {
		
		ResultInfo result = new ResultInfo();
		int count = 0;
		try {
			count = itemClient.importGoods2db();
		} catch (Exception e) {
			log.error("Error while sync item", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，更新失败");
			return result;
		}
		log.info("Success sync item {}", count);
		result.setErrorInfo("成功导入" + count + "条商品信息");
		return result;
	}

    @Deprecated
	@RequestMapping(value="/admin/sync_trade")
	public @ResponseBody ResultInfo syncTrade() throws IOException, ServletException, JSONException, ParseException, InsufficientStockException, InsufficientBalanceException {
		
		ResultInfo result = new ResultInfo();
		int count = 0;
		try {
			Date endDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -15);
			Date startDate = cal.getTime();
			count = initTrades(topConfig.listToken(), startDate, endDate);
		} catch (Exception e) {
			log.error("Error while sync trade", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，更新失败");
			return result;
		}
		log.info("Success sync trade {}", count);
		result.setErrorInfo("成功导入" + count + "条交易信息");
		return result;
	}

    @Deprecated
    public int initTrades(List<String> tokenList, Date startDate, Date endDate) throws ApiException, ParseException {
        int totalCount = 0;

        List<Trade> tradeList = tradeClient.queryTradeList(tokenList, startDate, endDate);
        for(Trade trade : tradeList) {
            //check trade if exists
            Trade tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getToken(trade.getSellerNick()));
            if(null == tradeDetail) {
                continue;
            }
            MyTrade myTrade = tradeService.toMyTrade(tradeDetail);
            if(null == myTrade)
                continue;
            myTrade.setStatus(TradeStatus.UnSubmit.getStatus());
            int count = tradeService.insertMyTrade(myTrade, Constants.LOCK_QUANTITY, null);
            totalCount += count;
        }
        return totalCount;
    }
	
	@RequestMapping(value="/admin/sync_area")
	public @ResponseBody ResultInfo syncArea() throws IOException, ServletException, JSONException, ParseException {
		
		ResultInfo result = new ResultInfo();
		int count = 0;
		try {
			count = areaClient.import2db();
		} catch (Exception e) {
			log.error("Error while sync area", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，更新失败");
			return result;
		}
		log.info("Success sync area {}", count);
		result.setErrorInfo("成功导入" + count + "条地区信息");
		return result;
	}
}
