package cn.halen.data.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.MyTrade;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class MyTradeMapperTest {
	
	@Autowired
	private MyTradeMapper myTradeMapper;
	
	@Test
	public void test_selectTradeDetail() {
		long tid = 221610652790229L;
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		System.out.println(myTrade);
	}
}
