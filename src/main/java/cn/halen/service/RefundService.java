package cn.halen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.mapper.RefundMapper;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.service.top.domain.Status;
import cn.halen.util.Constants;

@Service
public class RefundService {

	@Autowired
	private RefundMapper refundMapper;
	
	@Autowired
	private MyTradeMapper tradeMapper;
	
	@Autowired
	private SkuService skuService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Transactional(rollbackFor=Exception.class)
	public boolean applyRefund(String tid, String oid, String refundReason) throws InvalidStatusChangeException {
		MyTrade myTrade = tradeMapper.selectByTradeId(tid);
		MyOrder myOrder = tradeMapper.selectOrderByOrderId(oid);
		if(myTrade.getMy_status() != MyStatus.WaitReceive.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		if(null == myTrade || null == myOrder) {
			return false;
		}
		
		MyRefund dbMyRefund = refundMapper.selectByTidOid(tid, oid);
		MyRefund refund = new MyRefund();
		refund.setTid(tid);
		refund.setOid(oid);
		refund.setRefund_reason(refundReason);
		refund.setSeller_nick(myTrade.getSeller_nick());
		refund.setName(myTrade.getName());
		refund.setStatus(Status.ApplyRefund.getValue());
		if(null == dbMyRefund) {
			refundMapper.insert(refund);
		} else {
			refundMapper.updateRefund(refund);
		}
		
		myOrder.setStatus(Status.ApplyRefund.getValue());
		tradeMapper.updateMyOrder(myOrder);
		return true;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void cancel(long id, String tid, String oid) {
		refundMapper.updateStatus(id, Status.CancelRefund.getValue());
		tradeMapper.updateOrderStatus(Status.WAIT_SELLER_SEND_GOODS.getValue(), tid, oid);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void approveRefund(long id, String tid, String oid) {
		
		refundMapper.updateStatus(id, Status.Refunding.getValue());
		tradeMapper.updateOrderStatus(Status.Refunding.getValue(), tid, oid);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void rejectRefund(long id, String tid, String oid, String comment1) {
		MyRefund refund = refundMapper.selectByTidOid(tid, oid);
		refund.setStatus(Status.RejectRefund.getValue());
		refund.setComment1(comment1);
		
		refundMapper.updateRefund(refund);
		tradeMapper.updateOrderStatus(Status.RejectRefund.getValue(), tid, oid);
	}

    /**
     * 收到退货
     * 货物可以二次销售，quantity + 1
     * 不能二次销售，残次品数量 + 1
     * @param id
     * @param tid
     * @param oid
     * @param comment2
     * @param isTwice
     */
	@Transactional(rollbackFor=Exception.class)
	public void receiveRefund(long id, String tid, String oid, String comment2, boolean isTwice) throws InsufficientStockException {
		MyRefund refund = refundMapper.selectByTidOid(tid, oid);
		refund.setStatus(Status.ReceiveRefund.getValue());
		refund.setComment2(comment2);
		
		refundMapper.updateRefund(refund);
		tradeMapper.updateOrderStatus(Status.ReceiveRefund.getValue(), tid, oid);
		
		if(isTwice) {
			MyOrder myOrder = tradeMapper.selectOrderByOrderId(oid);
			skuService.updateSku(myOrder.getSku_id(), myOrder.getQuantity(), 0, 0, true);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void refundMoney(long id, String tid, String oid, String sellerNick) {
		
		refundMapper.updateStatus(id, Status.RefundSuccess.getValue());
		tradeMapper.updateOrderStatus(Status.RefundSuccess.getValue(), tid, oid);
		
		MyOrder myOrder = tradeMapper.selectOrderByOrderId(oid);
		Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			try {
				adminService.updateDeposit(d.getId(), myOrder.getPayment());
			} catch (InsufficientBalanceException e) {
			}
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void notRefundMoney(long id, String tid, String oid, String comment3) {
		MyRefund refund = refundMapper.selectByTidOid(tid, oid);
		refund.setStatus(Status.Refund.getValue());
		refund.setComment3(comment3);
		
		refundMapper.updateRefund(refund);
		tradeMapper.updateOrderStatus(Status.Refund.getValue(), tid, oid);
	}
}
