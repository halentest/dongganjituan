package cn.halen.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.halen.data.pojo.*;
import cn.halen.exception.MyException;
import cn.halen.service.excel.TradeRow;
import cn.halen.service.top.domain.TaoTradeStatus;
import cn.halen.service.top.util.DateUtils;
import cn.halen.util.ErrorInfoHolder;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.order.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.mapper.GoodsMapper;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.mapper.MyTradeMapper;
import cn.halen.data.mapper.RefundMapper;
import cn.halen.data.pojo.TradeStatus;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.service.top.LogisticsCompanyClient;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.util.MoneyUtils;
import cn.halen.util.Constants;
import cn.halen.util.Paging;

import com.taobao.api.ApiException;
import com.taobao.api.domain.Order;
import com.taobao.api.domain.Trade;

@Service
public class TradeService {

	private Logger log = LoggerFactory.getLogger(TradeService.class);
	
	@Autowired
	private MyTradeMapper myTradeMapper;
	
	@Autowired
	private TradeClient tradeClient;
	
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private SkuService skuService;
	
	@Autowired
	private LogisticsCompanyClient logisticsClient;
	
	@Autowired
	private UtilService utilService;
	
	@Autowired
	private MyLogisticsCompanyMapper logisticsMapper;
	
	@Autowired
	private RefundMapper refundMapper;

    @Deprecated
	@Transactional(rollbackFor=Exception.class)
	public void updateSkuAndInsertRefund(MyRefund myRefund, MySku mySku) {
		try {
			myTradeMapper.insertRefund(myRefund);
			mySkuMapper.update(mySku);
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

    /**
     * 填写快递单号
     * @param tid 订单号
     * @param outSid 快递单号
     */
    public void addTrackingNumber(String tid, String outSid) {
        MyTrade myTrade = myTradeMapper.selectById(tid);
        myTrade.setDelivery_number(outSid);
        myTrade.setStatus(TradeStatus.WaitOut.getStatus());
        myTrade.setScan_time(new Date());
        myTradeMapper.updateMyTrade(myTrade);
    }
	
	/**
	 * 发货
     * 1，如果是淘宝自动同步的订单，需要调用淘宝的接口完成店铺发货
	 */
	@Transactional(rollbackFor=Exception.class)
	public void send(String id, ErrorInfoHolder eh) throws InsufficientStockException, ApiException, MyException {
            MyTrade t = myTradeMapper.selectTradeMap(id);
			String errorInfo = null;
            if(t.getIs_pause()==1) {
                errorInfo = "已暂停，不能出库";
                eh.setErrorInfo(errorInfo);
                return;
            }
            int result = doSend(t, t.getDelivery(), t.getDelivery_number(), true);
            if(0==result) {
                errorInfo = "订单不存在或者已经发货";
                eh.setErrorInfo(errorInfo);
                return;
            }
			if("淘宝自动同步".equals(t.getCome_from())) {
                String companyCode = logisticsMapper.selectByName(t.getDelivery()).getCode();
				errorInfo = logisticsClient.send(t.getTid(), t.getDelivery_number(), companyCode, t.getSeller_nick());
                if(null != errorInfo) {
                    throw new MyException(errorInfo);
                }
			}
	}

    /**
     * 重新发货（可能由于单号错误，需要重新发货），仅需要更新淘宝店铺和系统内的物流信息，无需修改sku
     * @param tid
     * @param outSid
     * @param companyName
     * @param companyCode
     * @return
     */
	@Transactional(rollbackFor=Exception.class)
	public String reSend(String tid, String outSid, String companyName, String companyCode) {
		try {
			String errorInfo = logisticsClient.reSend(tid, outSid, companyCode);
			if(null == errorInfo) {
				//doSend(tid, companyName, outSid, false);
			}
			return errorInfo;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

    /**
     * 取消订单，如果是已经提交的订单需要unlock库存
     * @return
     * @throws InsufficientStockException
     */
	@Transactional(rollbackFor=Exception.class)
	public void cancel(MyTrade trade) throws InsufficientStockException {
		if(trade.getIs_cancel()==1 && trade.getIs_submit()==1) {
            for(MyOrder myOrder : trade.getMyOrderList()) {
                skuService.updateSku(myOrder.getSku_id(), 0, -myOrder.getQuantity(), 0, true);
                log.info("update sku for cancel {}, {}, {}, {}, {}", myOrder.getId(), myOrder.getSku_id(), -myOrder.getQuantity(), -myOrder.getQuantity(), 0);
            }
        }
        myTradeMapper.updateMyTrade(trade);
	}

    //TODO
	@Transactional(rollbackFor=Exception.class)
	public boolean refundSuccess(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeMap(tid);
//		if(myTrade.getMy_status() != TradeStatus.Refunding.getStatus()) {
//			throw new InvalidStatusChangeException(tid);
//		}
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			String goodsId = myOrder.getGoods_id();
			long quantity = myOrder.getQuantity();
			//skuService.updateSku(goodsId, myOrder.getSku_id(), quantity);
		}
		Shop shop = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick());
		Distributor d = shop.getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), 
					myTrade.getPayment() + myTrade.getDelivery_money());
		}
//		return myTradeMapper.updateTradeStatus(TradeStatus.Refund.getStatus(), tid) > 0;
        return false ;
	}

    /**
     * 仓库发货时发现没货，或者货品有问题
     * 1，修改trade状态为无货
     * 2，unlock锁定库存
     * 可能是因为残次品，那么需要管理员手动做退仓表格，减去残次品库存
     * @param tid
     * @return
     * @throws InsufficientStockException
     * @throws InsufficientBalanceException
     * @throws InvalidStatusChangeException
     */
	@Transactional(rollbackFor=Exception.class)
	public boolean noGoods(String tid, String oid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeMap(tid);
		if(myTrade.getIs_send()==1) {
			throw new InvalidStatusChangeException(tid);
		}
		List<MyOrder> list = myTrade.getMyOrderList();
//		skuService.unlockSku(list, false);

		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), myTrade.getPayment() + myTrade.getDelivery_money());
		}
//        if(StringUtils.isNotBlank(oid)) {
//            //不为空说明只有这个商品没货，否则表示所有商品都没有货
//            myTradeMapper.updateOrderStatus(TaoTradeStatus.NoGoods.getValue(), tid, oid);
//        } else {
//            for(MyOrder order : list) {
//                myTradeMapper.updateOrderStatus(TaoTradeStatus.NoGoods.getValue(), tid, order.getOid());
//            }
//        }
		return myTradeMapper.updateTradeStatus(TradeStatus.NoGoods.getStatus(), tid) > 0;
	}

    /**
     * 同意订单留到仓库
     * @param tid
     * @return
     * @throws InvalidStatusChangeException
     */
	@Transactional(rollbackFor=Exception.class)
	public boolean approve1(String tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeMap(tid);
//		if(myTrade.getMy_status() != TradeStatus.WaitCheck.getStatus()) {
//			throw new InvalidStatusChangeException(tid);
//		}
		return myTradeMapper.updateTradeStatus(TradeStatus.WaitSend.getStatus(), tid) > 0;
	}

    /**
     * 修改快递并修改快递金额
     * 如果是已提交订单并且分销商非自营，那么需要修改存款
     * @param delivery
     * @param deliveryMoney
     * @return                                log.debug("Lock sku({},{},{},{}) failed for salable quantity {} not enough", order.getGoods_id(), order.getColor(), order.getSize(),
    order.getQuantity(), salableQuantity);
     * @throws InvalidStatusChangeException
     * @throws InsufficientBalanceException
     */
	@Transactional(rollbackFor=Exception.class)  
	public boolean changeDelivery(String id, String delivery, int deliveryMoney) throws InvalidStatusChangeException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeMap(id);
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery(delivery);
        if(deliveryMoney > 0) {
            myTrade.setDelivery_money(deliveryMoney);
            Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
            if(myTrade.getIs_submit()==1 && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
                adminService.updateDeposit(d.getId(), change);
            }
        }
		return myTradeMapper.updateMyTrade(myTrade) > 0;
	}

    /**
     * 提交trade，即改变trade的状态为待审核或者待发货（如果此分销商的trade无需审核）
     * 对于非自营的分销商，完成扣款操作
     * @param id
     * @return
     * @throws InvalidStatusChangeException
     * @throws InsufficientBalanceException
     */
	@Transactional(rollbackFor=Exception.class)
	public void submit(String id, ErrorInfoHolder holder, boolean needCheck) throws InsufficientBalanceException, InsufficientStockException {
        MyTrade myTrade = myTradeMapper.selectTradeMap(id);
        if(myTrade.getIs_pause()==1) {
            holder.setErrorInfo("已暂停，不能提交");
            return;
        }

        if(!myTrade.getStatus().equals(TradeStatus.UnSubmit.getStatus())) {
            holder.setErrorInfo("当前状态不能提交");
            return;
        }

        if(needCheck && StringUtils.isNotBlank(myTrade.getBuyer_message())) {
            holder.setErrorInfo("买家有留言, 不能批量提交");
            return;
        }

        myTrade.setStatus(TradeStatus.WaitSend.getStatus());
        myTrade.setIs_submit(1);
        myTrade.setSubmit_time(new Date());
        int count = myTradeMapper.updateMyTrade(myTrade);
        if(0==count) {
            holder.setErrorInfo("订单不存在或者已提交");
            return;
        }

        for(MyOrder order : myTrade.getMyOrderList()) {
            skuService.updateSku(order.getSku_id(), 0, order.getQuantity(), 0, true);
            log.info("update sku for submit {}, {}, {}, {}, {}", order.getId(), order.getSku_id(), 0, order.getQuantity(), 0);
        }

		//update deposit
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), -myTrade.getPayment()-myTrade.getDelivery_money());
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean findGoods(String id, ErrorInfoHolder holder) {
        MyTrade t = myTradeMapper.selectById(id);
        if(!t.getStatus().equals(TradeStatus.WaitSend.getStatus())) {
            holder.setErrorInfo("当前状态不能拣货");
            return false;
        }
        t.setStatus(TradeStatus.WaitFind.getStatus());
		return myTradeMapper.updateMyTrade(t) > 0;
	}

    /**
     * 发货
     * 1，修改trade和order的状态为待买家收货
     * 2，减实际库存
     * 3，减锁定库存
     * @param companyName
     * @param outSid
     */
	private int doSend(MyTrade myTrade, String companyName, String outSid, boolean updateSku) throws InsufficientStockException {
        if(!myTrade.getStatus().equals(TradeStatus.WaitOut.getStatus())) {
            return 0;
        }

		myTrade.setStatus(TradeStatus.WaitReceive.getStatus());
        myTrade.setIs_send(1);
        myTrade.setIs_cancel(0);
        myTrade.setSend_time(new Date());
		int result = myTradeMapper.updateMyTrade(myTrade);
        if(0 == result) {
            return 0;
        }

        if(updateSku) {
            List<MyOrder> list = myTrade.getMyOrderList();
            unlockSku(list);
        }
        return result;
	}

    private void unlockSku(List<MyOrder> list) throws InsufficientStockException {
        for(MyOrder myOrder : list) {
            skuService.updateSku(myOrder.getSku_id(), -myOrder.getQuantity(), -myOrder.getQuantity(), 0, true);
            log.info("update sku for send {}, {}, {}, {}, {}", myOrder.getId(), myOrder.getSku_id(), -myOrder.getQuantity(), -myOrder.getQuantity(), 0);
        }
    }
	
	public void updateOrder(MyOrder myOrder) {
		myTradeMapper.updateMyOrder(myOrder);
	}
	
	public void updateTrade(MyTrade myTrade) {
		myTradeMapper.updateMyTrade(myTrade);
	}

    /**
     *
     * @param myTrade
     * @param type 1:quantity（实际库存）  2:lock_quantity（锁定库存）  3:manaual_lock_quantity（手动锁定库存）
     *             可用库存 = 实际库存 - 锁定库存 - 手动锁定库存
     * @param idHolder 用于返回真正的trade表的id
     * @return
     * @throws ApiException
     */
	@Transactional(rollbackFor=Exception.class)
	public int insertMyTrade(MyTrade myTrade, int type, Map<String, String> idHolder) throws ApiException {
        if(StringUtils.isNotBlank(myTrade.getTid()) && myTradeMapper.checkTidExist(myTrade.getTid())) {
            return 0;
        }
        List<MyOrder> orderList = myTrade.getMyOrderList();
        //合并相同地址相同收货人并且未发货的订单
        MyTrade commonTrade = myTradeMapper.selectTradeByAddress(myTrade.getName(), myTrade.getMobile(), myTrade.getState(),
                myTrade.getCity(), myTrade.getDistrict(), myTrade.getAddress());

		for(MyOrder order : orderList) {
            if(null != commonTrade) {
                order.setTid(commonTrade.getId());
            }
			myTradeMapper.insertMyOrder(order);
            if(Constants.QUANTITY == type) {
                //批量导入的订单，需要减去库存
                try {
                    skuService.updateSku(order.getSku_id(), -order.getQuantity(), 0, 0, true);
                } catch (InsufficientStockException e) {
                    log.error("batch insert trade error", e);
                }
            }
		}
        int count = 0;
        if(null == commonTrade) {
            count = myTradeMapper.insert(myTrade);
            if(null != idHolder) {
                idHolder.put("id", myTrade.getId());
            }
        } else {
            //更新tid
            if(StringUtils.isNotBlank(myTrade.getTid())) {
                String newTid = myTrade.getTid();
                String commonTid = commonTrade.getTid();
                if(StringUtils.isNotBlank(commonTid)) {
                    newTid = commonTid + "," + newTid;
                }
                commonTrade.setTid(newTid);
                myTradeMapper.updateMyTrade(commonTrade);
            }
            count = 1;
            if(null != idHolder) {
                idHolder.put("id", commonTrade.getId());
            }
        }
        //插入tid到tid表中
        if(StringUtils.isNotBlank(myTrade.getTid())) {
            myTradeMapper.insertTid(myTrade.getTid());
        }
        if("淘宝自动同步".equals(myTrade.getCome_from())) {
            //update memo
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String memo = (StringUtils.isEmpty(myTrade.getSeller_memo())?"":myTrade.getSeller_memo()) + "[已同步:" + format.format(now) + "]";
            tradeClient.updateMemo(Long.parseLong(myTrade.getTid()), myTrade.getSeller_nick(), memo);
        }
        return count;
    }
	
	public MyOrder selectOrderByOrderId(String id) {
		return myTradeMapper.selectOrderByOrderId(id);
	}
	
	public MyTrade selectByTradeId(String id) {
		return myTradeMapper.selectById(id);
	}
	
	public int updateTradeMemo(String memo, String tradeId, Date modified) {
		int count = myTradeMapper.updateTradeMemo(memo, tradeId, modified);
		return count;
	}

    /**
     * 处理淘宝发来的物流变更消息
     * @param state
     * @param city
     * @param district
     * @param address
     * @param mobile
     * @param phone
     * @param zip
     * @param name
     * @param modified
     * @param tradeId
     * @return
     * @throws InsufficientBalanceException
     */
	public int updateLogisticsAddress(String state, String city, String district, String address, String mobile, String phone,
			String zip, String name, Date modified, String tradeId) throws InsufficientBalanceException {
		
		MyTrade myTrade = myTradeMapper.selectTradeMap(tradeId);

        //如果已经发货，则不能再修改地址了
		if(myTrade.getIs_send()==1) {
			return 0;
		}
		int deliveryMoney = utilService.calDeliveryMoney(myTrade.getMyOrderList().get(0).getGoods_id(), Integer.valueOf(String.valueOf(myTrade.getGoods_count())),
				myTrade.getDelivery(), myTrade.getState());
		
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery_money(deliveryMoney);
		myTrade.setState(state);
		myTrade.setCity(city);
		myTrade.setDistrict(district);
		myTrade.setAddress(address);
		myTrade.setMobile(mobile);
		myTrade.setPhone(phone);
		myTrade.setPostcode(zip);
		myTrade.setName(name);
		myTrade.setModified(modified);
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
        //如果已经提交了，那么要修改存款，因为修改地址快递费会改变
		if(myTrade.getIs_submit()==1 && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), change);
		}
		int count = myTradeMapper.updateMyTrade(myTrade);
		return count;
	}

    /**
     * 转换京东的订单
     * @param list
     * @return
     */
    public List<MyTrade> toMyTrade(List<OrderSearchInfo> list) throws ParseException {
        List<MyTrade> result = new ArrayList<MyTrade>();
        for(OrderSearchInfo orderInfo : list) {
            MyTrade t = new MyTrade();
            String id = myTradeMapper.generateId();
            t.setId(id);
            t.setTid(orderInfo.getOrderId());
            t.setSeller_nick(orderInfo.getVenderId());
            String payType = orderInfo.getPayType();
            String[] split = payType.split("-");
            t.setPay_type(Integer.parseInt(split[0]));
            t.setPayment(MoneyUtils.convert(orderInfo.getOrderSellerPrice()));
            t.setDelivery_money(MoneyUtils.convert(orderInfo.getFreightPrice()));
            t.setBuyer_message(orderInfo.getOrderRemark());
            t.setCreated(DateUtils.parseDateTime(orderInfo.getOrderStartTime()));

            int iReturnOrder = 0;
            String returnOrder = orderInfo.getReturnOrder();
            if(StringUtils.isNotBlank(returnOrder)) {
                iReturnOrder = Integer.parseInt(returnOrder);
            }
            t.setReturn_order(iReturnOrder);
            t.setSeller_memo(orderInfo.getVenderRemark());

            UserInfo userInfo = orderInfo.getConsigneeInfo();
            t.setName(userInfo.getFullname());
            t.setAddress(userInfo.getFullAddress());
            t.setPhone(userInfo.getTelephone());
            t.setMobile(userInfo.getMobile());
            t.setState(userInfo.getProvince());
            t.setCity(userInfo.getCity());
            t.setDistrict(userInfo.getCounty());

            List<ItemInfo> itemList = orderInfo.getItemInfoList();
            int n = 1; //用来给子订单编号。子订单编号为主订单编号 + n
            for(ItemInfo item : itemList) {
                String skuId = item.getOuterSkuId(); //商家自定义id  = 商品自定义id + 颜色id + 尺寸
                //检查sku是否存在
                MySku sku = mySkuMapper.selectByHid(skuId);
                if(null == sku) {
                    log.info("This sku {} does not exist!", skuId);
                    continue;
                }
                int num = Integer.parseInt(item.getItemTotal()); //商品数量
                MyOrder order = new MyOrder();
                order.setSku_id(sku.getId());
                order.setTid(id);
                order.setQuantity(num);
                n++;
                t.addOrder(order);
            }
            if(t.getMyOrderList() != null && t.getMyOrderList().size() > 0) {
                result.add(t);
            }
        }
        return result;
    }

    public List<MyTrade> toMyTrade(List<TradeRow> list, String sellerNick) {
        List<MyTrade> result = new ArrayList<MyTrade>();
        MyTrade lastTrade = null;
        for(TradeRow row : list) {
            if(StringUtils.isNotBlank(row.getShopName())) {
                MyTrade myTrade = new MyTrade();
                String id = myTradeMapper.generateId();
                myTrade.setId(id);
                myTrade.setTid(row.getTradeId());
                myTrade.setName(row.getName());
                myTrade.setPhone(row.getPhone());
                myTrade.setMobile(row.getMobile());
                myTrade.setDistributor_id(1);
                myTrade.setSeller_memo(row.getComment());
                myTrade.setSeller_nick(sellerNick);
                myTrade.setCome_from("批量导入");
                myTrade.setModified(new Date());
                myTrade.setCreated(new Date());
                myTrade.setAddress(row.getAddress());
                myTrade.setStatus(TradeStatus.WaitReceive.getStatus());
                myTrade.setIs_send(1);
                myTrade.setIs_submit(1);
                myTrade.setIs_finish(1);
                myTrade.setSend_time(new Date());
                MyLogisticsCompany mc = logisticsMapper.select(1);
                myTrade.setDelivery(mc.getName());

                MyOrder myOrder = new MyOrder();
                myOrder.setTid(id);
                myOrder.setColor(row.getColor());
                myOrder.setSize(row.getSize());
                myOrder.setPayment(row.getPrice());
                myOrder.setGoods_id(row.getGoodsId());
                myOrder.setTitle(row.getTitle());
                myOrder.setQuantity(row.getNum());
                myTrade.addOrder(myOrder);
                result.add(myTrade);

                lastTrade = myTrade;
            } else {
                MyOrder myOrder = new MyOrder();
                myOrder.setTid(lastTrade.getId());
                myOrder.setColor(row.getColor());
                myOrder.setSize(row.getSize());
                myOrder.setGoods_id(row.getGoodsId());
                myOrder.setTitle(row.getTitle());
                myOrder.setQuantity(row.getNum());
                myOrder.setPayment(row.getPrice());
                lastTrade.addOrder(myOrder);
            }
        }
        return result;
    }

    /**
     * 把淘宝自动同步的订单对象转为MyTrade对象， 同时会检查商品和sku是否存在，以及根据收货人的省份和运费模板计算快递费用
     * @param trade
     * @return
     * @throws ApiException
     */
	public MyTrade toMyTrade(Trade trade) throws ApiException {

        MyTrade myTrade = new MyTrade();
        String id = myTradeMapper.generateId();
        myTrade.setId(id);
		List<Order> orderList = trade.getOrders();
		int goodsCount = 0;
		List<MyOrder> myOrderList = new ArrayList<MyOrder>();
		String sellerNick = trade.getSellerNick();
		Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
		boolean isSelf = d.getSelf() == Constants.DISTRIBUTOR_SELF_YES;
		float discount = d.getDiscount();
		String goodsHid = null; //用来查询模板
		int totalPayment = 0;
		String lCompany = null;
		for(Order order : orderList) {
			Goods goods = goodsMapper.getByHid(order.getOuterIid());
			if(null == goods) { //检查商品是否存在
				log.info("This goods {} not exist!", order.getOuterIid());
                    //update memo
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date now = new Date();
//                String memo = (StringUtils.isEmpty(trade.getSellerMemo())?"":trade.getSellerMemo()) + "同步失败1";
//                tradeClient.updateMemo(trade.getTid(), trade.getSellerNick(), memo);
                myTrade.setSuccess(false);
				continue;
			}

            //先同outer id查询sku，如果查不到， 再通过颜色和尺码
            String color = null;
            String size = null;
            //MySku sku = mySkuMapper.selectByHid("11112240");
            MySku sku = mySkuMapper.selectByHid(order.getOuterSkuId());
            if(null != sku) {
                color = sku.getColor();
                size = sku.getSize();
            } else {
                String skuStr = order.getSkuPropertiesName(); //颜色分类:玫红色;尺码:35
                String[] properties = skuStr.split(";");
                color = properties[0].split(":")[1];
                size = properties[1].split(":")[1];
                sku = mySkuMapper.select(order.getOuterIid(), color, size);
            }

			if(null == sku) {  //检查sku是否存在
				log.info("This sku {} {} {} not exist!", order.getOuterIid(), color, size);
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date now = new Date();
//                String memo = (StringUtils.isEmpty(trade.getSellerMemo())?"":trade.getSellerMemo()) + "同步失败2";
//                tradeClient.updateMemo(trade.getTid(), trade.getSellerNick(), memo);
                myTrade.setSuccess(false);
				continue;
			}
			goodsHid = goods.getHid();
			lCompany = order.getLogisticsCompany();
			
			goodsCount += order.getNum();
			MyOrder myOrder = new MyOrder();
			myOrder.setTid(id);
			myOrder.setColor(color);
			myOrder.setSize(size);
            myOrder.setSku_id(sku.getId());
			myOrder.setGoods_id(order.getOuterIid());
			myOrder.setTitle(order.getTitle());
			myOrder.setPic_path(order.getPicPath());
			myOrder.setQuantity(order.getNum());
			if(!isSelf && trade.getStatus().equals(TaoTradeStatus.WAIT_SELLER_SEND_GOODS.getValue())) {
				myOrder.setPrice(goods.getPrice());
				int payment = MoneyUtils.cal(goods.getPrice(), discount, order.getNum());
				myOrder.setPayment(payment);
				totalPayment += payment;
			} else {
				myOrder.setPrice(MoneyUtils.convert(order.getPrice()));
				myOrder.setPayment(MoneyUtils.convert(order.getPayment()));
			}
			myOrder.setDelivery(order.getLogisticsCompany());
			myOrder.setDelivery_number(order.getInvoiceNo());
			myOrder.setStatus(order.getStatus());
			myOrderList.add(myOrder);
		}
		if(myOrderList.size() == 0) {
			return null;
		}
		
		myTrade.setTid(String.valueOf(trade.getTid()));
		myTrade.setName(trade.getReceiverName());
		myTrade.setPhone(trade.getReceiverPhone());
		myTrade.setMobile(trade.getReceiverMobile());
		myTrade.setState(trade.getReceiverState());
		myTrade.setCity(trade.getReceiverCity());
		myTrade.setDistrict(trade.getReceiverDistrict());
		myTrade.setAddress(trade.getReceiverAddress());
		myTrade.setPostcode(trade.getReceiverZip());
		myTrade.setDistributor_id(1);
		myTrade.setSeller_memo(trade.getSellerMemo());
		myTrade.setBuyer_message(trade.getBuyerMessage());
		myTrade.setSeller_nick(trade.getSellerNick());
        myTrade.setBuyer_nick(trade.getBuyerNick());
		myTrade.setCome_from(Constants.TOP);
		myTrade.setModified(trade.getModified());
		myTrade.setCreated(trade.getPayTime());
		myTrade.setMyOrderList(myOrderList);
		myTrade.setGoods_count(goodsCount);
        myTrade.setPay_type(Constants.PAY_TYPE_ONLINE); //目前只支持淘宝的在线支付订单
		MyLogisticsCompany mc = logisticsMapper.select(1);
		myTrade.setDelivery(mc.getName());
		if(!isSelf) {
			myTrade.setDelivery_money(utilService.calDeliveryMoney(goodsHid, goodsCount, mc.getCode(), trade.getReceiverState()));
			myTrade.setPayment(totalPayment);
		} else {
			myTrade.setDelivery_money(MoneyUtils.convert(trade.getPostFee()));
			myTrade.setPayment(MoneyUtils.convert(trade.getPayment()));
		}
		
		return myTrade;
	}
	
}
