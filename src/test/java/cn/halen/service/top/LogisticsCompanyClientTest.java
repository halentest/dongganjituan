package cn.halen.service.top;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.halen.data.DataConfig;
import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.service.ServiceConfig;

import com.taobao.api.ApiException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataConfig.class, ServiceConfig.class})
public class LogisticsCompanyClientTest {
	
	private Logger log = LoggerFactory.getLogger(getClass());
			
	@Autowired
	private LogisticsCompanyClient logisticsCompanyClient;
	
	@Autowired
	private MyLogisticsCompanyMapper mapper;

	@Test
	public void test_import2db() throws ApiException, JSONException {
		
//		MyLogisticsCompany company = new MyLogisticsCompany();
//		company.setCode("test");
//		List<MyLogisticsCompany> list = new ArrayList<MyLogisticsCompany>();
//		list.add(company);
//		//mapper.insert(company);
		int count =  logisticsCompanyClient.import2db();
		log.info(String.valueOf(count));
	}
	
	@Test
	public void test_send() throws ApiException {
		String tid = "353821081120229";
		String outSid = "668458423741";
		String companyCode = "SF";
		String errorInfo = logisticsCompanyClient.send(tid, outSid, companyCode, "志东张");
		System.out.println(errorInfo);
	}
	
}
