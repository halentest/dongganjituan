package cn.halen.service;

import cn.halen.data.pojo.*;
import cn.halen.service.jd.JdTradeClient;
import cn.halen.util.Constants;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.taobao.api.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.MySkuMapper;

import java.text.ParseException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class TradeServiceTest {
	@Autowired
	private TradeService tradeService;

    @Autowired
    private JdTradeClient jd;
	
	@Autowired
	private MySkuMapper mySkuMapper;

    @Test
    public void test_insertTrade() throws ApiException {
        MyTrade t = new MyTrade();
        String id = "12345";
        t.setName("name");
        t.setMobile("13344");
        t.setAddress("address");
        t.setId(id);
        MyOrder o = new MyOrder();
        o.setTid(id);
        o.setSku_id(2);
        t.addOrder(o);
        tradeService.insertMyTrade(t, 0, null);
    }

	@Test
	public void test_updateSkuAndInsertRefund() {
		MyRefund myRefund = new MyRefund();
		myRefund.setTid("111111");
		MySku mySku = mySkuMapper.select(1);
		mySku.setPrice(1000);
		tradeService.updateSkuAndInsertRefund(myRefund, mySku);
	}

    /**
     * 测试导入京东订单
     */
    @Test
    public void importJDOrder() throws ParseException, JdException, ApiException {
        Shop shop = new Shop();
        shop.setToken("b94f4d02-0760-4f80-aa3e-8c6d6d883904");
        List<OrderSearchInfo> orderList = jd.queryOrder(null, null, shop);
        List<MyTrade> tradeList = tradeService.toMyTrade(orderList);
        int count = 0;
        for(MyTrade t : tradeList) {
            count += tradeService.insertMyTrade(t, Constants.LOCK_QUANTITY, null);
        }
        System.out.println(count);
    }
	
	@Test
	public void test_send() {
		String tid = "353821081120229";
		String outSid = "668458423741";
		String companyCode = "STO";
		String companyName = "申通E物流";
		String errorInfo = tradeService.reSend(tid, outSid, companyName, companyCode);
		System.out.println(errorInfo);
	}
}
