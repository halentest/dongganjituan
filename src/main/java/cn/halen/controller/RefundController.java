package cn.halen.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import cn.halen.data.mapper.AreaMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.mapper.RefundMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.Shop;
import cn.halen.data.pojo.User;
import cn.halen.data.pojo.UserType;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.filter.UserHolder;
import cn.halen.service.RefundService;
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;
import cn.halen.service.UtilService;
import cn.halen.service.top.domain.Status;
import cn.halen.util.Paging;

@Controller
public class RefundController {
	
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
	private RefundService refundService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private RefundMapper refundMapper;
	
	@RequestMapping(value="trade/apply_refund")
	public @ResponseBody ResultInfo applyRefund(Model model, @RequestParam("tid") String tid, @RequestParam("oid") String oid, 
			@RequestParam("refundReason") String refundReason) {
		ResultInfo result = new ResultInfo();
		try {
			refundService.applyRefund(tid, oid, refundReason);
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
			return result;
		}
		return result;
	}
	
	@RequestMapping(value="trade/refund_list")
	public String list(Model model, HttpServletResponse resp, @RequestParam(value="seller_nick", required=false) String sellerNick,
			@RequestParam(value="dId", required=false) Integer dId,
			@RequestParam(value="tid", required=false) String tid, 
			@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page", required=false) Integer page) {
		int intPage = 1;
		if(null!=page && page>0) {
			intPage = page;
		}
		
		List<String> statusList = null;
		
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

		if(StringUtils.isNotEmpty(status)) {
			statusList = Arrays.asList(status);
		} else if(currType.equals(UserType.WareHouse.getValue())) {
			statusList = Arrays.asList(Status.ReceiveRefund.getValue(), Status.Refund.getValue(),
					Status.Refunding.getValue(), Status.RefundSuccess.getValue());
		}
		
		long totalCount = refundMapper.countRefund(sellerNickList, tid, name, statusList);
		model.addAttribute("totalCount", totalCount);
		Paging paging = new Paging(intPage, 20, totalCount);
		List<MyRefund> list = Collections.emptyList();
		if(totalCount > 0) {
			list = refundMapper.listRefund(sellerNickList, tid, name, paging, statusList);
		}
		model.addAttribute("refund_list", list);
		model.addAttribute("paging", paging);
		model.addAttribute("tid", tid);
		model.addAttribute("status", status);
		model.addAttribute("seller_nick", sellerNick);
		model.addAttribute("name", name);
		model.addAttribute("dId", dId);
		model.addAttribute("dList", adminMapper.listDistributor());
		
		if(currType.equals(UserType.WareHouse.getValue())) {
			model.addAttribute("statusList", Arrays.asList(Status.ReceiveRefund, Status.Refund,
					Status.Refunding, Status.RefundSuccess));
		} else {
			model.addAttribute("statusList", Arrays.asList(Status.ApplyRefund, Status.ReceiveRefund, Status.Refund,
					Status.Refunding, Status.RefundSuccess, Status.RejectRefund, Status.CancelRefund));
		}
		
		if(null != dId && -1 != dId) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(dId).getShopList());
		}
		if(currType.equals(UserType.Distributor.getValue()) || currType.equals(UserType.ServiceStaff.getValue())) {
			model.addAttribute("shopList", adminMapper.selectDistributorMapById(currUser.getShop().getD().getId()).getShopList());
		}
		return "trade/refund_list";
	}
	
	@RequestMapping(value="trade/action/change_refund_status")
	public @ResponseBody ResultInfo changeStatus(Model model, @RequestParam("rid") long rid, @RequestParam("action") String action,
			@RequestParam("tid") String tid, @RequestParam("oid") String oid,
			@RequestParam(value="comment", required=false) String comment,
			@RequestParam(value="isTwice", required=false) boolean isTwice,
			@RequestParam(value="sellerNick", required=false) String sellerNick) {
		ResultInfo result = new ResultInfo();
		try {
			if(action.equals("cancel-refund")) {
				refundService.cancel(rid, tid, oid);
			} else if(action.equals("approve-refund")) {
				refundService.approveRefund(rid, tid, oid);
			} else if(action.equals("reject-refund")) {
				refundService.rejectRefund(rid, tid, oid, comment);
			} else if(action.equals("receive-refund")) {
				refundService.receiveRefund(rid, tid, oid, comment, isTwice);
			} else if(action.equals("refund-money")) {
				refundService.refundMoney(rid, tid, oid, sellerNick);
			} else if(action.equals("not-refund-money")) {
				refundService.notRefundMoney(rid, tid, oid, comment);
			}
		} catch (Exception e) {
			log.error("", e);
			result.setSuccess(false);
			result.setErrorInfo("系统异常，请重试!");
		}
		return result;
	}
}
