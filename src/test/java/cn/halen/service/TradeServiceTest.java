package cn.halen.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.MySkuMapper;
import cn.halen.data.pojo.MyRefund;
import cn.halen.data.pojo.MySku;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class TradeServiceTest {
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private MySkuMapper mySkuMapper;
	
	@Test
	public void test_updateSkuAndInsertRefund() {
		MyRefund myRefund = new MyRefund();
		myRefund.setOid(11111);
		myRefund.setTid(111111);
		MySku mySku = mySkuMapper.select(1);
		mySku.setPrice(1000);
		tradeService.updateSkuAndInsertRefund(myRefund, mySku);
	}
	
	@Test
	public void test_send() {
		long tid = 353821081120229L;
		String outSid = "668458423741";
		String companyCode = "STO";
		String companyName = "申通E物流";
		String errorInfo = tradeService.reSend(tid, outSid, companyName, companyCode);
		System.out.println(errorInfo);
	}
}
