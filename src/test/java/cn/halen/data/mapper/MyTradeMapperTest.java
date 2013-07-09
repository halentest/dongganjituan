package cn.halen.data.mapper;

import java.util.Arrays;
import java.util.List;

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
		String tid = "353498440620229";
		MyTrade myTrade = myTradeMapper.selectTradeDetail(tid);
		System.out.println(myTrade);
	}
	
	@Test
	public void test_listTrade() {
		List<String> sellerNickList = Arrays.asList("志东张");
		List<MyTrade> list = myTradeMapper.listTrade(sellerNickList, null, null, null, null, null, null, null, null);
		System.out.println(list.size());
	}
}
