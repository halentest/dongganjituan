package cn.halen.data.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.Distributor;
import cn.halen.service.ServiceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class FenXiaoShangDaoTest {

	@Autowired
	private DistributorDao fenXiaoShangDao;
	
	@Test
	public void testListFenXiaoShang() {
		List<Distributor> list = fenXiaoShangDao.list();
		System.out.println(list.size());
	}
	
	@Test
	public void testGetFenXiaoShangById() {
		Distributor fenXiaoShang = fenXiaoShangDao.get(1L);
		System.out.println(fenXiaoShang);
	}
}
