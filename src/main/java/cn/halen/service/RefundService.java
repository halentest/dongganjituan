package cn.halen.service;

import cn.halen.data.pojo.*;
import cn.halen.service.top.domain.TaoTradeStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.mapper.RefundMapper;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.util.Constants;

import java.util.List;

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
	public boolean applyRefund(MyRefund refund, MyTrade trade) {
		List<RefundOrder> list = refund.getRefundOrderList();
        for(RefundOrder order : list) {
            refundMapper.insertRefundOrder(order);
        }
        refundMapper.insert(refund);
        trade.setStatus(TradeStatus.WaitWareHouseReceive.getStatus());
        trade.setIs_refund(1);
        tradeMapper.updateMyTrade(trade);
        return true;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void cancel(long id, String tid, String oid) {
//		refundMapper.updateStatus(id, TaoTradeStatus.CancelRefund.getValue());
		tradeMapper.updateOrderStatus(TaoTradeStatus.WAIT_SELLER_SEND_GOODS.getValue(), tid, oid);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void approveRefund(long id, String tid, String oid) {
		
//		refundMapper.updateStatus(id, TaoTradeStatus.Refunding.getValue());
//		tradeMapper.updateOrderStatus(TaoTradeStatus.Refunding.getValue(), tid, oid);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void rejectRefund(long id, String tid, String oid, String comment1) {
		MyRefund refund = refundMapper.selectByTidOid(tid, oid);
//		refund.setStatus(TaoTradeStatus.RejectRefund.getValue());
//		refund.setComment1(comment1);
//
//		refundMapper.updateRefund(refund);
//		tradeMapper.updateOrderStatus(TaoTradeStatus.RejectRefund.getValue(), tid, oid);
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
//		refund.setStatus(TaoTradeStatus.ReceiveRefund.getValue());
//		refund.setComment2(comment2);
//
//		refundMapper.updateRefund(refund);
//		tradeMapper.updateOrderStatus(TaoTradeStatus.ReceiveRefund.getValue(), tid, oid);
		
		if(isTwice) {
			MyOrder myOrder = tradeMapper.selectOrderByOrderId(oid);
			skuService.updateSku(myOrder.getSku_id(), myOrder.getQuantity(), 0, 0, true);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void refundMoney(long id, String tid, String oid, String sellerNick) {
		
//		refundMapper.updateStatus(id, TaoTradeStatus.RefundSuccess.getValue());
//		tradeMapper.updateOrderStatus(TaoTradeStatus.RefundSuccess.getValue(), tid, oid);
		
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
//		refund.setStatus(TaoTradeStatus.Refund.getValue());
//		refund.setComment3(comment3);
//
//		refundMapper.updateRefund(refund);
//		tradeMapper.updateOrderStatus(TaoTradeStatus.Refund.getValue(), tid, oid);
	}
}
