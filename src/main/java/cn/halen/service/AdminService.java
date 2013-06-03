package cn.halen.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.halen.data.mapper.AdminMapper;
import cn.halen.data.pojo.Template;

@Service
public class AdminService {

	private Logger log = LoggerFactory.getLogger(AdminService.class);
	
	@Autowired
	private AdminMapper adminMapper;
	
	
	@Transactional(rollbackFor=Exception.class)
	public int insertNewTemplate(List<Template> list, String templateName) {
		try {
			int count = adminMapper.batchInsertTemplate(list);
			adminMapper.insertTemplateName(templateName);
			return count;
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	@Transactional(rollbackFor=Exception.class)
	public int updateTemplate(List<Template> list) {
		int count = 0;
		try {
			for(Template template : list) {
				count += adminMapper.updateTemplate(template);
			}
		} catch(Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		return count;
	}
}