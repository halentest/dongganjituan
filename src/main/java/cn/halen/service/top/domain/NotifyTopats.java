package cn.halen.service.top.domain;

import java.util.Date;

import com.taobao.api.TaobaoObject;
import com.taobao.api.internal.mapping.ApiField;

public class NotifyTopats extends TaobaoObject {

	private static final long serialVersionUID = -6154597964524408620L;

	@ApiField("topic")
	private String topic;
	@ApiField("status")
	private String status;
	@ApiField("api_name")
	private String apiName;
	@ApiField("task_id")
	private Long taskId;
	@ApiField("task_status")
	private String taskStatus;
	@ApiField("timestamp")
	private Date timestamp;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getApiName() {
		return this.apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public Long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTaskStatus() {
		return this.taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
