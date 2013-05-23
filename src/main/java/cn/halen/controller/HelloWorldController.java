//package cn.halen.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import cn.halen.data.dao.GoodsDao;
//import cn.halen.data.pojo.Goods;
//
//@Controller
//public class HelloWorldController {
//
//	@Autowired
//	private GoodsDao goodsDao;
//	
//	@RequestMapping(value="/admin/hello")
//	public String sayHello(Model model) {
//		
//		List<Goods> list = goodsDao.list();
//		model.addAttribute("user", list.size());
//		return "admin/hello";
//	}
//}
