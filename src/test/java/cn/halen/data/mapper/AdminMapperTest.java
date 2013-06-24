package cn.halen.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.Distributor;
import cn.halen.data.pojo.Shop;
import cn.halen.data.pojo.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class AdminMapperTest {
	
	@Autowired
	private AdminMapper mapper;
	
	@Test
	public void test_listUser() {
		List<User> list = mapper.listUser();
		System.out.println(list.size());
	}
	
	@Test
	public void test_listDistributor() {
		List<Distributor> list = mapper.listDistributorMap();
		System.out.println(list.size());
	}
	
	@Test
	public void test_selectUser() {
		User user = mapper.selectUser("zhangzhidong");
		System.out.println(user.getPassword());
	}
	
	@Test
	public void test_selectShopMap() {
		Shop s = mapper.selectShopMapBySellerNick("志东张");
		System.out.println(s.getSellerNick());
	}
	
	@Test
	public void test_updateDistributorToken() {
		String sellerNick = "骆驼动感旗舰店";
		String token = "6101508899dbb0721a6e3669a3912bc4b8a8fd9ff1f15ad822479138";
		String refreshToken = "6102308580d800f610c3f6d564002f362587dc4b9d41fc8822479138";
		mapper.updateDistributorToken(token, refreshToken, sellerNick);
		
		sellerNick = "志东张";
		token = "6100118adcacd086aef5cbd9d616b5c269e8bb5e8a40292719237974";
		refreshToken = "610001867f19ee3591057d31631fda7decc7f67b12ae35a719237974";
		mapper.updateDistributorToken(token, refreshToken, sellerNick);
	}
	
}
