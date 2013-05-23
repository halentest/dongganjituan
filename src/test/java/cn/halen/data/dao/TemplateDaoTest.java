package cn.halen.data.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.pojo.Template;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class})
public class TemplateDaoTest {

	@Autowired
	private TemplateDao templateDao;
	
	@Test
	public void testGetTemplateByCityDelivery() {
		Template template = templateDao.getTemplateByCityDelivery(1, 1);
		System.out.println(template);
	}
	
}
