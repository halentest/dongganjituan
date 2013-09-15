package cn.halen.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.halen.service.excel.TradeRow;
import cn.halen.service.top.util.DateUtils;
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
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.Goods;
import cn.halen.data.pojo.MyLogisticsCompany;
import cn.halen.data.pojo.MyOrder;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;
import cn.halen.data.pojo.MyStatus;
import cn.halen.data.pojo.MyTrade;
import cn.halen.data.pojo.Shop;
import cn.halen.exception.InsufficientBalanceException;
import cn.halen.exception.InsufficientStockException;
import cn.halen.exception.InvalidStatusChangeException;
import cn.halen.service.top.LogisticsCompanyClient;
import cn.halen.service.top.TopConfig;
import cn.halen.service.top.TradeClient;
import cn.halen.service.top.domain.Status;
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
     * @param companyName 快递名称
     * @param outSid 快递单号
     */
    public void addTrackingNumber(String tid, String companyName, String outSid) {
        MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
        myTrade.setDelivery(companyName);
        myTrade.setDelivery_number(outSid);
        myTrade.setMy_status(MyStatus.WaitOut.getStatus());
        myTradeMapper.updateMyTrade(myTrade);
    }
	
	/**
	 * 发货
     * 1，如果是淘宝自动同步的订单，需要调用淘宝的接口完成店铺发货
	 */
	@Transactional(rollbackFor=Exception.class)
	public String send(String tid) {
		try {
            MyTrade t = myTradeMapper.selectByTradeId(tid);
			String companyCode = logisticsMapper.selectByName(t.getDelivery()).getCode();
			String errorInfo = null;
			if("淘宝自动同步".equals(t.getCome_from())) {
				errorInfo = logisticsClient.send(tid, t.getDelivery_number(), companyCode, t.getSeller_nick());
			}
			if(null == errorInfo) {
				doSend(tid, t.getDelivery(), t.getDelivery_number(), true);
			}
			return errorInfo;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
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
				doSend(tid, companyName, outSid, false);
			}
			return errorInfo;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

    /**
     * 分销商在发货拣货之前取消trade
     * 1，非待处理--》unlock sku
     * 2，非新建、非待处理，且非自营分销商--》回款
     * @param tid
     * @return
     * @throws InsufficientStockException
     * @throws InsufficientBalanceException
     * @throws InvalidStatusChangeException
     */
	@Transactional(rollbackFor=Exception.class)
	public boolean cancel(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && 
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus() &&
                myTrade.getMy_status() != MyStatus.WaitHandle.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
        List<MyOrder> orderList = myTrade.getMyOrderList();
        if(myTrade.getMy_status() != MyStatus.WaitHandle.getStatus()) {
            skuService.unlockSku(orderList, true);
        }
		if(myTrade.getMy_status() != MyStatus.New.getStatus() &&
                myTrade.getMy_status() != MyStatus.WaitHandle.getStatus()) {
			String sellerNick = myTrade.getSeller_nick();
			Distributor d = adminMapper.selectShopMapBySellerNick(sellerNick).getD();
			if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
				adminService.updateDeposit(d.getId(), myTrade.getPayment() + myTrade.getDelivery_money());
			}
		}
		return myTradeMapper.updateTradeStatus(MyStatus.Cancel.getStatus(), tid) > 0;
	}

    //TODO
	@Transactional(rollbackFor=Exception.class)
	public boolean refundSuccess(String tid) throws InsufficientStockException, InsufficientBalanceException, InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.Refunding.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
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
		return myTradeMapper.updateTradeStatus(MyStatus.Refund.getStatus(), tid) > 0;
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
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus() && myTrade.getMy_status() != MyStatus.WaitPrint.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		List<MyOrder> list = myTrade.getMyOrderList();
		skuService.unlockSku(list, false);

		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), myTrade.getPayment() + myTrade.getDelivery_money());
		}
        if(StringUtils.isNotBlank(oid)) {
            //不为空说明只有这个商品没货，否则表示所有商品都没有货
            myTradeMapper.updateOrderStatus(Status.NoGoods.getValue(), tid, oid);
        } else {
            for(MyOrder order : list) {
                myTradeMapper.updateOrderStatus(Status.NoGoods.getValue(), tid, order.getOid());
            }
        }
		return myTradeMapper.updateTradeStatus(MyStatus.NoGoods.getStatus(), tid) > 0;
	}

    /**
     * 同意订单留到仓库
     * @param tid
     * @return
     * @throws InvalidStatusChangeException
     */
	@Transactional(rollbackFor=Exception.class)
	public boolean approve1(String tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		return myTradeMapper.updateTradeStatus(MyStatus.WaitSend.getStatus(), tid) > 0;
	}

    /**
     * 修改快递并修改快递金额
     * 如果是已提交订单并且分销商非自营，那么需要修改存款
     * @param tid
     * @param delivery
     * @param deliveryMoney
     * @return                                log.debug("Lock sku({},{},{},{}) failed for salable quantity {} not enough", order.getGoods_id(), order.getColor(), order.getSize(),
    order.getQuantity(), salableQuantity);
     * @throws InvalidStatusChangeException
     * @throws InsufficientBalanceException
     */
	@Transactional(rollbackFor=Exception.class)  
	public boolean changeDelivery(String tid, String delivery, int deliveryMoney) throws InvalidStatusChangeException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && myTrade.getMy_status() != MyStatus.New.getStatus() &&
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		int change = myTrade.getDelivery_money() - deliveryMoney;
		myTrade.setDelivery(delivery);
        if(deliveryMoney > 0) {
            myTrade.setDelivery_money(deliveryMoney);
            Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
            if(myTrade.getMy_status() != MyStatus.New.getStatus() && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
                adminService.updateDeposit(d.getId(), change);
            }
        }
		return myTradeMapper.updateMyTrade(myTrade) > 0;
	}

    /**
     * 提交trade，即改变trade的状态为待审核或者待发货（如果此分销商的trade无需审核）
     * 对于非自营的分销商，完成扣款操作
     * @param tid
     * @return
     * @throws InvalidStatusChangeException
     * @throws InsufficientBalanceException
     */
	@Transactional(rollbackFor=Exception.class)
	public boolean submit(String tid) throws InvalidStatusChangeException, InsufficientBalanceException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.New.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		//update deposit
		Distributor d = adminMapper.selectShopMapBySellerNick(myTrade.getSeller_nick()).getD();
		if(d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
			adminService.updateDeposit(d.getId(), -myTrade.getPayment()-myTrade.getDelivery_money());
		}
		int count = 0;
		if(d.getNoCheck() == 1) {
			count = myTradeMapper.updateTradeStatus(MyStatus.WaitSend.getStatus(), tid);
		} else {
			count = myTradeMapper.updateTradeStatus(MyStatus.WaitCheck.getStatus(), tid);
		}
		return count > 0;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public boolean findGoods(String tid) throws InvalidStatusChangeException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		if(myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
			throw new InvalidStatusChangeException(tid);
		}
		return myTradeMapper.updateTradeStatus(MyStatus.WaitPrint.getStatus(), tid) > 0;
	}

    /**
     * 发货
     * 1，修改trade和order的状态为待买家收货
     * 2，减实际库存
     * 3，unlock锁定库存
     * @param tid
     * @param companyName
     * @param outSid
     */
	private void doSend(String tid, String companyName, String outSid, boolean updateSku) throws InsufficientStockException {
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		myTrade.setStatus(Status.WAIT_BUYER_CONFIRM_GOODS.getValue());
		myTrade.setMy_status(MyStatus.WaitReceive.getStatus());
		myTradeMapper.updateMyTrade(myTrade);
		
		List<MyOrder> list = myTrade.getMyOrderList();
		for(MyOrder myOrder : list) {
			myOrder.setStatus(Status.WAIT_BUYER_CONFIRM_GOODS.getValue());
			myOrder.setDelivery(companyName);
			myOrder.setDelivery_number(outSid);
			myTradeMapper.updateMyOrder(myOrder);
            if(updateSku) {
                //2
                skuService.updateSku(myOrder.getSku_id(), -myOrder.getQuantity(), 0, 0, false);
            }
		}
        if(updateSku) {
            //3
            skuService.unlockSku(list, false);
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
     * @param checkExist
     * @param type 1:quantity（实际库存）  2:lock_quantity（锁定库存）  3:manaual_lock_quantity（手动锁定库存）
     *             可用库存 = 实际库存 - 锁定库存 - 手动锁定库存
     * @return
     * @throws ApiException
     */
	@Transactional(rollbackFor=Exception.class)
	public int insertMyTrade(MyTrade myTrade, boolean checkExist, int type) throws ApiException {
        if(checkExist && myTradeMapper.isTidExist(myTrade.getTid())) {
            return 0;
        }
        List<MyOrder> orderList = myTrade.getMyOrderList();
        boolean enough = skuService.reduceSku(orderList, true, type);
        int payment = 0;
        int quantity = 0;
		for(MyOrder order : orderList) {
			myTradeMapper.insertMyOrder(order);
            quantity += order.getQuantity();
            payment += order.getPayment() * order.getQuantity();
		}
        if(checkExist) {
            myTrade.setPayment(payment);
            myTrade.setGoods_count(quantity);
        }
        if(!enough) {
            myTrade.setMy_status(MyStatus.WaitHandle.getStatus());
        }
        int count = myTradeMapper.insert(myTrade);
//        if("淘宝自动同步".equals(myTrade.getCome_from())) {
//            //update memo
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date now = new Date();
//            String memo = (StringUtils.isEmpty(myTrade.getSeller_memo())?"":myTrade.getSeller_memo()) + "已同步";
//            tradeClient.updateMemo(Long.parseLong(myTrade.getTid()), myTrade.getSeller_nick(), memo);
//        }
        return count;
    }
	
	public MyOrder selectOrderByOrderId(String oid) {
		return myTradeMapper.selectOrderByOrderId(oid);
	}
	
	public MyTrade selectTradeDetail(String tid) {
		return myTradeMapper.selectTradeDetail(tid);
	}
	
	public MyTrade selectByTradeId(String id) {
		return myTradeMapper.selectByTradeId(id);
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
		
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tradeId);
		if(myTrade.getMy_status() != MyStatus.WaitCheck.getStatus() && myTrade.getMy_status() != MyStatus.New.getStatus() &&
				myTrade.getMy_status() != MyStatus.WaitSend.getStatus()) {
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
		if(myTrade.getMy_status() != MyStatus.New.getStatus() && d.getSelf() != Constants.DISTRIBUTOR_SELF_YES) {
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
                order.setTid(orderInfo.getOrderId());
                order.setQuantity(num);
                order.setOid(orderInfo.getOrderId() + n);
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
        int c = 1;
        for(TradeRow row : list) {
            if(StringUtils.isNotBlank(row.getShopName())) {
                c = 1;
                MyTrade myTrade = new MyTrade();
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
                myTrade.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
                MyLogisticsCompany mc = logisticsMapper.select(1);
                myTrade.setDelivery(mc.getName());

                MyOrder myOrder = new MyOrder();
                myOrder.setTid(row.getTradeId());
                myOrder.setOid(row.getTradeId() + c);
                myOrder.setColor(row.getColor());
                myOrder.setSize(row.getSize());
                myOrder.setPayment(row.getPrice());
                myOrder.setGoods_id(row.getGoodsId());
                myOrder.setTitle(row.getTitle());
                myOrder.setQuantity(row.getNum());
                myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
                myTrade.addOrder(myOrder);
                result.add(myTrade);

                lastTrade = myTrade;
            } else {
                c ++;
                MyOrder myOrder = new MyOrder();
                myOrder.setTid(lastTrade.getTid());
                myOrder.setOid(lastTrade.getTid() + c);
                myOrder.setColor(row.getColor());
                myOrder.setSize(row.getSize());
                myOrder.setGoods_id(row.getGoodsId());
                myOrder.setTitle(row.getTitle());
                myOrder.setQuantity(row.getNum());
                myOrder.setPayment(row.getPrice());
                myOrder.setStatus(Status.WAIT_SELLER_SEND_GOODS.getValue());
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
				continue;
			}
			String skuStr = order.getSkuPropertiesName(); //颜色分类:玫红色;尺码:35
			String[] properties = skuStr.split(";");
			String color = properties[0].split(":")[1];
			String size = properties[1].split(":")[1];
			MySku sku = mySkuMapper.select(order.getOuterIid(), color, size);
			if(null == sku) {  //检查sku是否存在
				log.info("This sku {} {} {} not exist!", order.getOuterIid(), color, size);
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date now = new Date();
//                String memo = (StringUtils.isEmpty(trade.getSellerMemo())?"":trade.getSellerMemo()) + "同步失败2";
//                tradeClient.updateMemo(trade.getTid(), trade.getSellerNick(), memo);
				continue;
			}
			goodsHid = goods.getHid();
			lCompany = order.getLogisticsCompany();
			
			goodsCount += order.getNum();
			MyOrder myOrder = new MyOrder();
			myOrder.setTid(String.valueOf(trade.getTid()));
			myOrder.setOid(String.valueOf(order.getOid()));
			myOrder.setColor(color);
			myOrder.setSize(size);
			myOrder.setGoods_id(order.getOuterIid());
			myOrder.setTitle(order.getTitle());
			myOrder.setPic_path(order.getPicPath());
			myOrder.setQuantity(order.getNum());
			if(!isSelf && trade.getStatus().equals(Status.WAIT_SELLER_SEND_GOODS.getValue())) {
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
		
		MyTrade myTrade = new MyTrade();
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
		myTrade.setCome_from(Constants.TOP);
		myTrade.setModified(trade.getModified());
		myTrade.setCreated(trade.getPayTime());
		myTrade.setMyOrderList(myOrderList);
		myTrade.setGoods_count(goodsCount);
		myTrade.setStatus(trade.getStatus());
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
	
	public long countTrade(List<String> sellerNickList, String name, String tid, List<Integer> statusList, List<Integer> notstatusList, String delivery,
                           String startTime, String endTime) {
		return myTradeMapper.countTrade(sellerNickList, name, tid, statusList, notstatusList, delivery, startTime, endTime);
	}
	
	public List<MyTrade> listTrade(List<String> sellerNickList, String name, String tid, Paging paging, List<Integer> statusList, List<Integer> notstatusList, String delivery,
                                   String startTime, String endTime) {
		return myTradeMapper.listTrade(sellerNickList, name, tid, paging, statusList, notstatusList, delivery, startTime, endTime);
	}
	
	public int initTrades(List<String> tokenList, Date startDate, Date endDate) throws ParseException, ApiException, InsufficientStockException, InsufficientBalanceException {
		int totalCount = 0;
		
		List<Trade> tradeList = tradeClient.queryTradeList(tokenList, startDate, endDate);
		for(Trade trade : tradeList) {
			//check trade if exists
			MyTrade dbMyTrade = selectByTradeId(String.valueOf(trade.getTid()));
			Trade tradeDetail = tradeClient.getTradeFullInfo(trade.getTid(), topConfig.getToken(trade.getSellerNick()));
			MyTrade myTrade = toMyTrade(tradeDetail);
			if(null == myTrade)
				continue;
			if(null == dbMyTrade) {
				myTrade.setMy_status(MyStatus.New.getStatus());
				int count = insertMyTrade(myTrade, false, Constants.LOCK_QUANTITY);
				totalCount += count;
			} else {
				handleExisting(myTrade);
			}
		}
		return totalCount;
	}
	
	private void handleExisting(MyTrade myTrade) throws ApiException {
		MyTrade dbMyTrade = selectTradeDetail(myTrade.getTid());
		if(!myTrade.toString().equals(dbMyTrade.toString()) && myTrade.getModified().getTime() > dbMyTrade.getModified().getTime()) {
			dbMyTrade.setName(myTrade.getName());
			dbMyTrade.setPhone(myTrade.getPhone());
			dbMyTrade.setMobile(myTrade.getMobile());
			dbMyTrade.setState(myTrade.getState());
			dbMyTrade.setCity(myTrade.getCity());
			dbMyTrade.setDistrict(myTrade.getDistrict());
			dbMyTrade.setAddress(myTrade.getAddress());
			dbMyTrade.setSeller_memo(myTrade.getSeller_memo());
			dbMyTrade.setModified(myTrade.getModified());
		}
		updateTrade(dbMyTrade);
	}
}
