package cn.halen.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.pojo.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class AdminServiceTest {
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private AdminMapper adminMapper;
	
	@Test
	public void test_insertNewTemplate() {
		adminMapper.insertTemplateName("Template1");
		
		List<Template> list = new ArrayList<Template>();
		Template template = new Template();
		template.setName("Template1");
		template.setArea("hb");
		list.add(template);
		
		adminService.insertNewTemplate(list, "Template1");
	}
}
