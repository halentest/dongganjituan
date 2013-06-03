package cn.halen.data.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
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
	public void test_selectUser() {
		User user = mapper.selectUser("zhangzhidong");
		boolean b = user.hasAnyAuthority("admim", "fenxiaoshang");
		System.out.println(user.getName());
	}
	
}
