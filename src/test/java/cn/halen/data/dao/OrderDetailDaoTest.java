//package cn.halen.data.dao;
//
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import cn.halen.data.DataConfig;
//import cn.halen.data.pojo.Goods;
//import cn.halen.data.pojo.Order;
//import cn.halen.data.pojo.OrderDetail;
//import cn.halen.exception.UpdateZeroException;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {DataConfig.class})
//public class OrderDetailDaoTest {
//	@Autowired
//	private OrderDetailDao orderDetailDao;
//	@Test
//	public void testAddOrderDetail() {
//		
//		OrderDetail detail = new OrderDetail();
//		
//		Order order = new Order();
//		order.setId(1);
//		detail.setOrder(order);
//		
//		Goods goods = new Goods();
//		goods.setId(1);
//		detail.setGoods(goods);
//		
//		detail.setForty(11);
//		
//		orderDetailDao.addOrderDetail(detail);
//	}
//	@Test 
//	public void testUpdateStatus() throws UpdateZeroException {
//		OrderDetail detail = orderDetailDao.get(1);
//		orderDetailDao.updateStatus(1, 0, detail.getModified());
//	}
//	
//	@Test
//	public void testListOrderDetail() {
//		List<OrderDetail> list = orderDetailDao.list(1);
//		System.out.println(list.size());
//	}
//}
