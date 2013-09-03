package cn.halen.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import cn.halen.service.top.AreaClient;
import cn.halen.service.top.ItemClient;
import cn.halen.service.top.LogisticsCompanyClient;
import cn.halen.service.top.TopConfig;

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
	private TradeService tradeService;
	
	@Autowired
	private TopConfig topConfig;
	
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
	
	@RequestMapping(value="/admin/sync_trade")
	public @ResponseBody ResultInfo syncTrade() throws IOException, ServletException, JSONException, ParseException, InsufficientStockException, InsufficientBalanceException {
		
		ResultInfo result = new ResultInfo();
		int count = 0;
		try {
			Date endDate = new Date();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -15);
			Date startDate = cal.getTime();
			count = tradeService.initTrades(topConfig.listToken(), startDate, endDate);
		} catch (ApiException e) {
			log.error("Error while sync trade", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，更新失败");
			return result;
		}
		log.info("Success sync trade {}", count);
		result.setErrorInfo("成功导入" + count + "条交易信息");
		return result;
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
