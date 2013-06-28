package cn.halen.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Shop;
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
	
	//private static final String REDIS_DISTRIBUTOR_LIST = "redis:distributor:list";
	
	private static final List<MyStatus> SuperAdmin = Arrays.asList(MyStatus.New, MyStatus.Cancel, MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.NoGoods);
	private static final List<MyStatus> Admin = Arrays.asList(MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.NoGoods);
	private static final List<MyStatus> Distributor = Arrays.asList(MyStatus.New, MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.Cancel, MyStatus.NoGoods);
	private static final List<MyStatus> WareHouse = Arrays.asList(MyStatus.WaitSend, MyStatus.Finding, MyStatus.WaitReceive);
	private static final List<MyStatus> DistributorManager = Arrays.asList(MyStatus.WaitCheck, MyStatus.WaitSend, MyStatus.Finding,
			MyStatus.WaitReceive, MyStatus.NoGoods);
	
	@RequestMapping(value="trade/trade_list")
	public String list(Model model, HttpServletResponse resp, @RequestParam(value="seller_nick", required=false) String sellerNick,
			@RequestParam(value="dId", required=false) Integer dId,
			@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="tid", required=false) String tid, 
			@RequestParam(value="status", required=false) Integer status,
			@RequestParam(value="page", required=false) Integer page) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		
		List<Integer> notstatusList = null;
		List<Integer> statusList = null;
		
		User currUser = UserHolder.get();
		String currType = currUser.getType();
		if(!currType.equals(UserType.Admin.getValue()) && !currType.equals(UserType.SuperAdmin.getValue()) &&
				!currType.equals(UserType.Distributor.getValue()) && !currType.equals(UserType.WareHouse.getValue()) &&
				!currType.equals(UserType.DistributorManager.getValue()) && !currType.equals(UserType.ServiceStaff.getValue())) {
			model.addAttribute("errorInfo", "对不起，您没有权限查看此页面！");
			return "error_page";
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

		if(null != status) {
			statusList = Arrays.asList(status);
		} else {
			if(currType.equals(UserType.Admin.getValue())) {
				notstatusList = Arrays.asList(MyStatus.New.getStatus(), MyStatus.Cancel.getStatus());
			} else if(currType.equals(UserType.WareHouse.getValue())) {
				statusList = Arrays.asList(MyStatus.WaitSend.getStatus(), MyStatus.WaitReceive.getStatus(), MyStatus.Finding.getStatus(), MyStatus.Refunding.getStatus());
			} else if(currType.equals(UserType.DistributorManager.getValue())) {
				notstatusList = Arrays.asList(MyStatus.New.getStatus(), MyStatus.Cancel.getStatus());
			}
		}
		
		if(currType.equals(UserType.Admin.getValue())) {
			model.addAttribute("statusList", Admin);
		} else if(currType.equals(UserType.SuperAdmin.getValue())) {
			model.addAttribute("statusList", SuperAdmin);
		} else if(currType.equals(UserType.Distributor.getValue()) || currType.equals(UserType.ServiceStaff.getValue())) {
			model.addAttribute("statusList", Distributor);
		} else if(currType.equals(UserType.DistributorManager.getValue())) {
			model.addAttribute("statusList", DistributorManager);
		} else if(currType.equals(UserType.WareHouse.getValue())) {
			model.addAttribute("statusList", WareHouse);
		}
		
		long totalCount = tradeService.countTrade(sellerNickList, name, tid, statusList, notstatusList);
		model.addAttribute("totalCount", totalCount);
		Paging paging = new Paging(intPage, 10, totalCount);
		List<MyTrade> list = Collections.emptyList();
		if(totalCount > 0) {
			list = tradeService.listTrade(sellerNickList, name, tid, paging, statusList, notstatusList);
		}
		model.addAttribute("trade_list", list);
		model.addAttribute("paging", paging);
		model.addAttribute("name", name);
		model.addAttribute("tid", tid);
		model.addAttribute("status", status);
		model.addAttribute("seller_nick", sellerNick);
		model.addAttribute("dId", dId);
		model.addAttribute("logistics", myLogisticsCompanyMapper.list());
		model.addAttribute("dList", adminMapper.listDistributor());
		if(null != dId && -1 != dId) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(dId).getShopList());
		}
		if(currType.equals(UserType.Distributor.getValue()) || currType.equals(UserType.ServiceStaff.getValue())) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(currUser.getShop().getD().getId()).getShopList());
		}
		
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
	
	@RequestMapping(value="/trade/list_shop")
	public @ResponseBody List<Shop> listShop(Model model, @RequestParam("dId") Integer dId) {
		if(null==dId) {
			return null;
		}
		Distributor d = adminMapper.selectDistributorMapById(dId);
		return d.getShopList();
	}
}
