package cn.halen.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.User;
import cn.halen.data.pojo.UserType;
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
	private AdminMapper adminMapper;
	
	private static final String REDIS_DISTRIBUTOR_LIST = "redis:distributor:list";
	
	private static final List<MyStatus> SuperAdmin = Arrays.asList(MyStatus.New, MyStatus.Cancel, MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.Finished, MyStatus.Refunding, MyStatus.Refund, MyStatus.ApplyRefund,
			MyStatus.NoGoods);
	private static final List<MyStatus> Admin = Arrays.asList(MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.Finished, MyStatus.Refunding, MyStatus.Refund, MyStatus.ApplyRefund,
			MyStatus.NoGoods);
	private static final List<MyStatus> Distributor = Arrays.asList(MyStatus.New, MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.Finished, MyStatus.Cancel, MyStatus.Refunding, MyStatus.Refund, MyStatus.ApplyRefund,
			MyStatus.NoGoods);
	private static final List<MyStatus> WareHouse = Arrays.asList(MyStatus.WaitSend, MyStatus.Finding, MyStatus.Refunding);
	private static final List<MyStatus> DistributorManager = Arrays.asList(MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.Finished, MyStatus.Refunding, MyStatus.Refund, MyStatus.ApplyRefund,
			MyStatus.NoGoods);
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/trade_list")
	public String list(Model model, HttpServletResponse resp, @RequestParam(value="seller_nick", required=false) String seller_nick,
			@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="page", required=false) Integer page) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		
		List<Integer> notstatusList = null;
		List<Integer> statusList = null;
		
		//如果是分销商或者客服，那么只显示他自己的订单
		User currUser = UserHolder.get();
		String currType = currUser.getType();
		if(!currType.equals(UserType.Admin.getValue()) && !currType.equals(UserType.SuperAdmin.getValue()) &&
				!currType.equals(UserType.Distributor.getValue()) && !currType.equals(UserType.WareHouse.getValue()) &&
				!currType.equals(UserType.DistributorManager.getValue())) {
			model.addAttribute("errorInfo", "对不起，您没有权限查看此页面！");
			return "error_page";
		}
		
		if(currType.equals(UserType.Distributor.getValue())) {
			seller_nick = currUser.getSeller_nick();
		}
		
		if(null != status) {
			statusList = Arrays.asList(status);
		} else {
			if(currType.equals(UserType.Admin.getValue())) {
				notstatusList = Arrays.asList(MyStatus.New.getStatus(), MyStatus.Cancel.getStatus());
			} else if(currType.equals(UserType.WareHouse.getValue())) {
				statusList = Arrays.asList(MyStatus.WaitSend.getStatus(), MyStatus.Finding.getStatus(), MyStatus.Refunding.getStatus());
			} else if(currType.equals(UserType.DistributorManager.getValue())) {
				notstatusList = Arrays.asList(MyStatus.New.getStatus(), MyStatus.Cancel.getStatus());
			}
		}
		
		if(currType.equals(UserType.Admin.getValue())) {
			model.addAttribute("statusList", Admin);
		} else if(currType.equals(UserType.SuperAdmin.getValue())) {
			model.addAttribute("statusList", SuperAdmin);
		} else if(currType.equals(UserType.Distributor.getValue())) {
			model.addAttribute("statusList", Distributor);
		} else if(currType.equals(UserType.DistributorManager.getValue())) {
			model.addAttribute("statusList", DistributorManager);
		} else if(currType.equals(UserType.WareHouse.getValue())) {
			model.addAttribute("statusList", WareHouse);
		}
		
		long totalCount = tradeService.countTrade(seller_nick, name, statusList, notstatusList);
		Paging paging = new Paging(intPage, 10, totalCount);
		List<MyTrade> list = tradeService.listTrade(seller_nick, name, paging, statusList, notstatusList);
		model.addAttribute("trade_list", list);
		model.addAttribute("paging", paging);
		model.addAttribute("name", name);
		model.addAttribute("status", status);
		model.addAttribute("seller_nick", seller_nick);
		model.addAttribute("logistics", myLogisticsCompanyMapper.list());
		
		List<String> distributorList = (List<String>) redisTemplate.opsForValue().get(REDIS_DISTRIBUTOR_LIST);
		if(null == distributorList) {
			List<User> userList = adminMapper.listUser(UserType.Distributor.getValue(), 1);
			distributorList = new ArrayList<String>();
			for(User user : userList) {
				distributorList.add(user.getSeller_nick());
			}
			redisTemplate.opsForValue().set(REDIS_DISTRIBUTOR_LIST, distributorList, 5, TimeUnit.MINUTES);
		}
		model.addAttribute("distributorList", distributorList);
		
		model.addAttribute("sender", "骆驼动感");
		model.addAttribute("from", "福建");
		model.addAttribute("from_company", "骆驼动感集团");
		model.addAttribute("from_address", "福建省石狮市动感骆驼仓库");
		model.addAttribute("sender_mobile", "15257197713");
		return "trade/trade_list";
	}
	
	@RequestMapping(value="fenxiao/add_trade_form")
	public String addTradeForm() {
		return "fenxiao/add_trade_form"; 
	}
}
