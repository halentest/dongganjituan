package cn.halen.controller.formbean;

import org.apache.commons.lang.StringUtils;

import cn.halen.service.ResultInfo;

public abstract class BaseValidator {
	private boolean success = true;
	private StringBuilder builder = new StringBuilder();
	
	public void validNotEmpty(String s, String errorMsg) {
		if(StringUtils.isEmpty(s)) {
			success = false;
			addErrorMsg(errorMsg);
		}
	}
	
	public void validBigger0(int i, String errorMsg) {
		if(i<=0) {
			success = false;
			addErrorMsg(errorMsg);
		}
	}
	
	public void validNotNegative(int i, String errorMsg) {
		if(i<0) {
			success = false;
			addErrorMsg(errorMsg);
		}
	}
	
	public ResultInfo validate() {
		doValidate();
		ResultInfo info = new ResultInfo();
		if(!success) {
			info.setSuccess(false);
			info.setErrorInfo(getErrorMsg());
		}
		return info;
	}
	
	private void addErrorMsg(String errorMsg) {
		builder.append(errorMsg).append(" and ");
	}
	
	public boolean success() {
		return success;
	}
	
	protected abstract void doValidate();
	
	public String getErrorMsg() {
		if(success)
			return null;
		
		String errorMsg = builder.toString();
		int index = errorMsg.lastIndexOf("and");
		return errorMsg.substring(0, index);
	}
}
