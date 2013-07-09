package cn.halen.service.top;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.NotifyTrade;
import com.taobao.api.domain.Task;
import com.taobao.api.domain.Trade;
import com.taobao.api.internal.util.AtsUtils;

@Service
public class TradeClient {
	
	private final static long PAGE_SIZE = 40L;
	
	private Logger log = LoggerFactory.getLogger(TradeClient.class);
	@Autowired
	private TopConfig topConfig;
	
	/**
	 * 获取订单详情
	 * @param tid
	 * @param sessionKey
	 * @return
	 * @throws ApiException
	 */
	public Trade getTradeFullInfo(Long tid, String sessionKey) throws ApiException {
		
		TradeFullinfoGetRequest req = new TradeFullinfoGetRequest();
		req.setFields("total_fee,tid,oid,buyer_nick,payment,outer_iid,title,pic_path,post_fee,receiver_state,receiver_address,num,receiver_city,receiver_district," +
				"receiver_mobile,logistics_company,invoice_no,seller_nick,created,pay_time,modified,order.modified,buyer_message," +
				"receiver_name,receiver_phone,receiver_mobile,receiver_zip,price,seller_memo,parent_id,type,status,orders");
		req.setTid(tid);
		TradeFullinfoGetResponse rsp = topConfig.getRetryClient().execute(req, sessionKey);
		if (rsp.isSuccess()) {
			log.info("查询订单详情成功：" + rsp.getBody());
			return rsp.getTrade();
		}
		return null;
	}
	
	/**
	 * 获取增量消息
	 * @throws ApiException 
	 */
	public List<NotifyTrade> getNofityTrade() throws ApiException {
		TaobaoClient client = topConfig.getRetryClient();
		IncrementTradesGetRequest req = new IncrementTradesGetRequest();
		
		req.setNick("志东张");
//		Date dateTime = SimpleDateFormat.getDateTimeInstance().parse("2013-05-25 00:00:00");
//		req.setStartModified(dateTime);
//		Date dateTime = SimpleDateFormat.getDateTimeInstance().parse("2013-05-25 01:00:00");
//		req.setEndModified(dateTime);
		IncrementTradesGetResponse response = client.execute(req);
		return response.getNotifyTrades();
	}
	
	/**
	 * get wait send trades in two days.
	 * @return
	 * @throws ParseException
	 * @throws ApiException
	 */
	public List<Trade> queryTradeList(List<String> sessionList, Date startDate, Date endDate) throws ParseException, ApiException {
		
		TaobaoClient client = topConfig.getRetryClient();
		TradesSoldGetRequest req = new TradesSoldGetRequest();
		req.setFields("tid, seller_nick");
		//查询已付款的订单
		req.setStatus("WAIT_SELLER_SEND_GOODS");
		//一口价
		req.setType("fixed");
		
		log.info("Start to sync sold trades from {} to {}", startDate, endDate);
		req.setStartCreated(startDate);
		req.setEndCreated(endDate);
		req.setPageSize(PAGE_SIZE);
		req.setUseHasNext(true);
		
		List<Trade> result = new ArrayList<Trade>();
		for(String session : sessionList) {
			boolean hasNext = true;
			int tryTime = 0;
			for(long pageNo=1; hasNext;) {
				req.setPageNo(pageNo);
				TradesSoldGetResponse response = client.execute(req , session);
				if(response.isSuccess()) {
					List<Trade> list = response.getTrades();
					if(null!=list) {
						result.addAll(list);
					}
					hasNext = response.getHasNext();
					pageNo++;
				} else {
					if(response.getErrorCode().equals("isp.remote-service-timeout")) {
						log.info("Time out while query has sold trades in 2 days, try {} again!", ++tryTime);
						hasNext = true; //超时重试
					} else {
						log.info("Error while query has sold trades in 2 days, error code : {}", response.getErrorCode());
						hasNext = false;
					}
				}
			}
		}
		return result;
	}

    public void updateMemo(long tid, String sellerNick, String memo) throws ApiException {
        TradeMemoUpdateRequest req = new TradeMemoUpdateRequest();
        req.setTid(tid);
        req.setMemo(memo);
        TradeMemoUpdateResponse response = topConfig.getRetryClient().execute(req, topConfig.getToken(sellerNick));
        if(!response.isSuccess()) {
            log.error("Update memo failed (tid:{},  sellerNick:{}, memo:{}, errorCode:{})",
                    tid, sellerNick, memo, response.getSubMsg());
            throw new ApiException("Update memo failed");
        }
    }
	
	
	//////////////////////////////////
	public void import2db() throws ApiException, JSONException, IOException {
		TaobaoClient client = topConfig.getRetryClient();
		TopatsTradesSoldGetRequest req = new TopatsTradesSoldGetRequest();
		req.setFields("tid,seller_nick,buyer_nick,title,payment,parent_id,type,status,created,orders");
		req.setStartTime("20130315");
		req.setEndTime("20130518");
		TopatsTradesSoldGetResponse response = client.execute(req , topConfig.getMainToken());
		if(null==response.getErrorCode()) {
			Task task = response.getTask();
			
			//查询结果
			boolean done = false;
			while(!done) {
				TopatsResultGetRequest req2 = new TopatsResultGetRequest();
				req2.setTaskId(task.getTaskId());
				TopatsResultGetResponse rsp = client.execute(req2);
				if (rsp.isSuccess() && rsp.getTask().getStatus().equals("done")) {
			         task = rsp.getTask();
			         String url = task.getDownloadUrl();
			         File taskFile = AtsUtils.download(url, new File("f:/topats/result")); // 下载文件到本地
			         File resultFile = new File("f:/topats/unzip", task.getTaskId() + ""); // 解压后的结果文件夹
			         List<File> files = AtsUtils.unzip(taskFile, resultFile); // 解压缩并写入到指定的文件夹
			         // 遍历解压到的文件列表并读取结果文件进行解释 …
			         done = true;
				} else {
				     try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				}
			}
		} else {
			log.error(response.getErrorCode());
		}
	}
}
