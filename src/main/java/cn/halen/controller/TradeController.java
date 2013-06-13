package cn.halen.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="trade/trade_list")
	public String list(Model model, @RequestParam(value="seller_nick", required=false) String seller_nick,
			@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="page", required=false) Integer page) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		
		Integer notstatus = null;
		List<Integer> statusList = null;
		
		//如果是分销商或者客服，那么只显示他自己的订单
		User currUser = UserHolder.get();
		if(currUser.getType().equals(UserType.Distributor.getValue()) || currUser.getType().equals(UserType.ServiceStaff.getValue())) {
			seller_nick = currUser.getSeller_nick();
		}
		if(!currUser.getType().equals(UserType.Distributor.getValue())) {
			notstatus = MyStatus.Cancel.getStatus();
		}
		
		if(null != status) {
			statusList = new ArrayList<Integer>();
			statusList.add(status);
		}
		
		long totalCount = tradeService.countTrade(seller_nick, name, statusList, notstatus);
		Paging paging = new Paging(intPage, 10, totalCount);
		List<MyTrade> list = tradeService.listTrade(seller_nick, name, paging, statusList, notstatus);
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
