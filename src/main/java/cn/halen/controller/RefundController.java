package cn.halen.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.halen.data.mapper.*;
import cn.halen.data.pojo.*;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.domain.TaoTradeStatus;
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

import cn.halen.filter.UserHolder;
import cn.halen.service.RefundService;
import cn.halen.service.ResultInfo;
import cn.halen.service.TradeService;
import cn.halen.service.UtilService;
import cn.halen.util.Paging;
import org.springframework.web.multipart.MultipartFile;

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
	private MyTradeMapper tradeMapper;
	
	@Autowired
	private RefundService refundService;
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private RefundMapper refundMapper;

    @Autowired
    private TopConfig topConfig;

    @Autowired
    private ConfigurationMapper configurationMapper;

    private static final String KEY_SPACE = "default";

    @RequestMapping(value="trade/action/receive_refund_form")
    public String receiveRefundForm(Model model, @RequestParam String tid) {
        MyTrade trade = tradeMapper.selectTradeMap(tid);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("trade", trade);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        if(trade.getIs_refund()==1) {
            MyRefund refund = refundMapper.selectRefundMapByTid(tid);
            model.addAttribute("refund", refund);
        }
        return "trade/receive_refund_form";
    }

    @RequestMapping(value="trade/action/receive_refund")
    public String receiveRefund(Model model, @RequestParam String id, @RequestParam String comment, HttpServletRequest req) {
        MyTrade trade = tradeMapper.selectTradeMap(id);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());

        trade.setStatus(TradeStatus.RefundFinish.getStatus());
        trade.setIs_finish(1);
        tradeMapper.updateMyTrade(trade);

        MyRefund refund = refundMapper.selectRefundMapByTid(id);
        refund.setComment(comment);
        refundMapper.updateRefund(refund);

        for(RefundOrder order : refund.getRefundOrderList()) {
            String bad = req.getParameter("bad" + order.getId());
            long badL = 0;
            if(StringUtils.isNotBlank(bad)) {
                badL = Long.parseLong(bad);
            }
            order.setBad_quantity(badL);
            refundMapper.updateRefundOrder(order);
        }
        model.addAttribute("refund", refund);
        model.addAttribute("trade", trade);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        return "trade/refund_info";
    }

    @RequestMapping(value="trade/action/apply_refund_form")
    public String applyRefundForm(Model model, @RequestParam String id) {
        MyTrade trade = tradeMapper.selectTradeMap(id);
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        model.addAttribute("trade", trade);
        model.addAttribute("conf", configurationMapper.listKVByKeySpace(KEY_SPACE));
        if(trade.getIs_refund()==1) {
            MyRefund refund = refundMapper.selectRefundMapByTid(id);
            model.addAttribute("refund", refund);
            return "trade/refund_info";
        }
        return "trade/apply_refund_form";
    }
	
	@RequestMapping(value="trade/apply_refund")
	public String applyRefund(Model model, HttpServletRequest req,
                            @RequestParam String id, //trade id
                            @RequestParam(required = false) String responsible_party,
                            @RequestParam String why_refund,
                            @RequestParam String delivery,
                            @RequestParam String delivery_number,
                            @RequestParam("pic1") MultipartFile pic1,
                            @RequestParam("pic2") MultipartFile pic2,
                            @RequestParam("pic3") MultipartFile pic3) {

        List<RefundOrder> orderList = new ArrayList<RefundOrder>();

        if(StringUtils.isBlank(responsible_party)) {
            model.addAttribute("errorInfo", "请选择责任方！");
            return "error_page";
        }

        MyTrade t = tradeMapper.selectTradeMap(id);
        List<MyOrder> myOrderList = t.getMyOrderList();
        for(MyOrder o : myOrderList) {
            String tui = req.getParameter("tui" + o.getId());
            String huan = req.getParameter("huan" + o.getId());
            if(StringUtils.isBlank(tui) && StringUtils.isBlank(huan)) {
                continue;
            }
            long tuiL = 0;
            long huanL = 0;
            try {
                if(StringUtils.isNotBlank(tui)) {
                    tuiL = Long.parseLong(tui.trim());
                }
                if(StringUtils.isNotBlank(huan)) {
                    huanL = Long.parseLong(huan.trim());
                }
            } catch (Exception e) {
                model.addAttribute("errorInfo", "请输入数字！");
                return "error_page";
            }
            if(tuiL == 0 && huanL == 0) {
                continue;
            }
            if((tuiL + huanL) > o.getQuantity()) {
                model.addAttribute("errorInfo", "退换货的数量不能大于购买的数量！");
                return "error_page";
            }
            RefundOrder refundOrder = new RefundOrder();
            refundOrder.setTid(id);
            refundOrder.setGoods_id(o.getGoods_id());
            refundOrder.setPic_path(o.getPic_path());
            refundOrder.setHuan_quantity(huanL);
            refundOrder.setSku_id(o.getSku_id());
            refundOrder.setTitle(o.getTitle());
            refundOrder.setTui_quantity(tuiL);
            refundOrder.setQuantity(o.getQuantity());
            orderList.add(refundOrder);
        }
        if(orderList.size() == 0) {
            model.addAttribute("errorInfo", "请输入退换货数量！");
            return "error_page";
        }
        MyRefund refund = new MyRefund();
        //上传图片
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssssss");
            if(!pic1.isEmpty()) {
                String d = format.format(new Date());
                String fileName = new String(pic1.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                File dest = new File(topConfig.getFileRefundPic() + "/" + d + "-" + fileName);
                byte[] bytes = pic1.getBytes();
                OutputStream out = new FileOutputStream(dest);
                out.write(bytes);
                out.flush();
                out.close();
                refund.setPic1(d + "-" + fileName);
            }

            if(!pic2.isEmpty()) {
                String d = format.format(new Date());
                String fileName = new String(pic2.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                File dest = new File(topConfig.getFileRefundPic() + "/" + d + "-" + fileName);
                byte[] bytes = pic2.getBytes();
                OutputStream out = new FileOutputStream(dest);
                out.write(bytes);
                out.flush();
                out.close();
                refund.setPic3(d + "-" + fileName);
            }

            if(!pic3.isEmpty()) {
                String d = format.format(new Date());
                String fileName = new String(pic3.getOriginalFilename().getBytes("iso8859-1"), "UTF-8");
                File dest = new File(topConfig.getFileRefundPic() + "/" + d + "-" + fileName);
                byte[] bytes = pic3.getBytes();
                OutputStream out = new FileOutputStream(dest);
                out.write(bytes);
                out.flush();
                out.close();
                refund.setPic3(d + "-" + fileName);
            }

        } catch (Exception e) {
            log.error("upload pic failed,", e);
        }

        if(StringUtils.isNotBlank(delivery)) {
            try {
                delivery = new String(delivery.getBytes("iso8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
        }
        if(StringUtils.isNotBlank(why_refund)) {
            try {
                why_refund = new String(why_refund.getBytes("iso8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }
        }
        refund.setTid(t.getId());
        refund.setDelivery(delivery);
        refund.setDelivery_number(delivery_number);
        refund.setRefundOrderList(orderList);
        refund.setResponsible_party(responsible_party);
        refund.setStatus("");
        refund.setWhy_refund(why_refund);
        refundService.applyRefund(refund, t);

        model.addAttribute("trade", t);
        model.addAttribute("refund", refundMapper.selectRefundMapByTid(t.getId()));
        model.addAttribute("logistics", myLogisticsCompanyMapper.list());
        return "trade/refund_info";
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

		if(StringUtils.isNotEmpty(status)) {
			statusList = Arrays.asList(status);
		} else if(currType.equals(UserType.WareHouse.getValue())) {
//			statusList = Arrays.asList(TaoTradeStatus.ReceiveRefund.getValue(), TaoTradeStatus.Refund.getValue(),
//					TaoTradeStatus.Refunding.getValue(), TaoTradeStatus.RefundSuccess.getValue());
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
		
//		if(currType.equals(UserType.WareHouse.getValue())) {
//			model.addAttribute("statusList", Arrays.asList(TaoTradeStatus.ReceiveRefund, TaoTradeStatus.Refund,
//					TaoTradeStatus.Refunding, TaoTradeStatus.RefundSuccess));
//		} else {
//			model.addAttribute("statusList", Arrays.asList(TaoTradeStatus.ApplyRefund, TaoTradeStatus.ReceiveRefund, TaoTradeStatus.Refund,
//					TaoTradeStatus.Refunding, TaoTradeStatus.RefundSuccess, TaoTradeStatus.RejectRefund, TaoTradeStatus.CancelRefund));
//		}
		
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
