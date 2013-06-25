package cn.halen.service.top;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.halen.data.mapper.MyLogisticsCompanyMapper;
import cn.halen.data.pojo.MyLogisticsCompany;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.LogisticsCompany;
import com.taobao.api.request.LogisticsCompaniesGetRequest;
import com.taobao.api.request.LogisticsConsignResendRequest;
import com.taobao.api.request.LogisticsOfflineSendRequest;
import com.taobao.api.response.LogisticsCompaniesGetResponse;
import com.taobao.api.response.LogisticsConsignResendResponse;
import com.taobao.api.response.LogisticsOfflineSendResponse;

@Service
public class LogisticsCompanyClient {
	
	private Logger log = LoggerFactory.getLogger(LogisticsCompanyClient.class);
	
	@Autowired
	private TopConfig topConfig;
	
	@Autowired
	private MyLogisticsCompanyMapper myLogisticsCompanyMapper;
	
	public int import2db() throws ApiException {
		List<MyLogisticsCompany> dbMyList = myLogisticsCompanyMapper.listAll();
		
		List<MyLogisticsCompany> myList = new ArrayList<MyLogisticsCompany>();
		for(LogisticsCompany logisticsCompany : list()) {
			myList.add(MyLogisticsCompany.toMe(logisticsCompany));
		}
		if(null == dbMyList || dbMyList.size()==0) {  //第一次初始化
			log.info("Init size is {}", myList.size());
			return myLogisticsCompanyMapper.batchInsert(myList);
		} else {
			//初始化db里的数据
			Map<String, MyLogisticsCompany> map = new HashMap<String, MyLogisticsCompany>();
			for(MyLogisticsCompany myLogisticsCompany : dbMyList) {
				map.put(myLogisticsCompany.getCode(), myLogisticsCompany);
			}
			
			List<MyLogisticsCompany> newAdded = new ArrayList<MyLogisticsCompany>();
			for(MyLogisticsCompany myLogisticsCompany : myList) {//遍历从top取得的list， 检查是否已经存在
				MyLogisticsCompany dbMyLogisticsCompany = map.get(myLogisticsCompany.getCode());
				if(null == dbMyLogisticsCompany) {
					newAdded.add(myLogisticsCompany);
				} else if(!myLogisticsCompany.toString().equals(dbMyLogisticsCompany.toString())) {
					myLogisticsCompanyMapper.update(myLogisticsCompany);
					log.info("Update {} with {}", dbMyLogisticsCompany, myLogisticsCompany);
				}
			}
			
			if(newAdded.size() > 0) {
				log.info("New added {}", newAdded.size());
				return myLogisticsCompanyMapper.batchInsert(newAdded);
			}
		}
		return 0;
	}
	
	public List<LogisticsCompany> list() throws ApiException {
		TaobaoClient client = topConfig.getRetryClient();
		LogisticsCompaniesGetRequest req = new LogisticsCompaniesGetRequest();
		req.setFields("id,code,name,reg_mail_no");
		LogisticsCompaniesGetResponse response = client.execute(req);
		if(response.isSuccess()) {
			List<LogisticsCompany> list = response.getLogisticsCompanies();
			log.debug("Got {} LogisticsCompany", list.size());
			return list;
		} else {
			log.error("Error while list LogisticsCompany, errorCode is {}", response.getErrorCode());
			return Collections.emptyList();
		}
	}
	
	/**
	 * 发货
	 * @throws ApiException 
	 */
	public String send(String tid, String outSid, String companyCode, String sellerNick) throws ApiException {
		TaobaoClient client = topConfig.getRetryClient();
		LogisticsOfflineSendRequest req = new LogisticsOfflineSendRequest();
		req.setTid(Long.valueOf(tid));
		req.setOutSid(outSid);
		req.setCompanyCode(companyCode);
		LogisticsOfflineSendResponse response = client.execute(req , topConfig.getToken(sellerNick));
		String errorInfo = null;
		if(!response.isSuccess()) {
			String errorCode = response.getErrorCode();
			log.error("Send failed, errorCode {}", errorCode);
			errorInfo = response.getSubMsg();
		}
		return errorInfo;
	}
	
	/**
	 * 修改物流信息
	 * @param tid
	 * @param outSid
	 * @param companyCode
	 * @return
	 * @throws ApiException 
	 */
	public String reSend(String tid, String outSid, String companyCode) throws ApiException {
		TaobaoClient client = topConfig.getRetryClient();
		LogisticsConsignResendRequest req = new LogisticsConsignResendRequest();
		req.setTid(Long.valueOf(tid));
		req.setOutSid(outSid);
		req.setCompanyCode(companyCode);
		LogisticsConsignResendResponse response = client.execute(req , topConfig.getMainToken());
		String errorInfo = null;
		if(!response.isSuccess()) {
			String errorCode = response.getErrorCode();
			log.error("Resend failed, errorCode {}", errorCode);
			errorInfo = response.getSubMsg();
		}
		return errorInfo;
	}
}
