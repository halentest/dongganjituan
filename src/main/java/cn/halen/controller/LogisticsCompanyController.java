package cn.halen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.halen.data.mapper.MyLogisticsCompanyMapper;

@Controller
public class LogisticsCompanyController {
	
	@Autowired
	private MyLogisticsCompanyMapper mapper;
	
	@RequestMapping(value="logistics_list")
	public String list(Model model, @RequestParam(value="seller_nick", required=false) String seller_nick,
			@RequestParam(value="name", required=false) String name, 
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="page", required=false) Integer page) {
		return null;
	}
}
