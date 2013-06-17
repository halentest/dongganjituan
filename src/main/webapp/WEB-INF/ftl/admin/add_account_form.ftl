<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >

	<form class="form-horizontal" action="${rc.contextPath}/admin/add_account" method="post">
	  <fieldset>
	    <legend><i class="icon-pencil"></i><font size="3">新建${userType.getName()}</font></legend>
	    <div class="control-group">
	      <label class="control-label" for="input01">用户名</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="username" id="input01">
	      </div>
	    </div>
	    <div class="control-group">
	      <label class="control-label" for="input01">密码</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="password" id="input01">
	      </div>
	    </div>
	    <div class="control-group">
	      <label class="control-label" for="input01">请再次输入密码</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="password2" id="input01">
	      </div>
	    </div>
	    <div class="control-group">
	      <label class="control-label" for="input01">姓名</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="name" id="input01">
	      </div>
	    </div>
	    <#if userType.getValue()=="ServiceStaff" || userType.getValue()=="Distributor">
	    <div class="control-group">
	      <label class="control-label" for="input01">店铺名称</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="seller_nick" id="input01">
	        <p class="help-block">必须输入正确的店铺名称</p>
	      </div>
	    </div>
	    </#if>
	    <#if userType.getValue()=="Distributor">
	    <div class="control-group">
	      <label class="control-label" for="input01">折扣</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="discount" id="input01">
	         <p class="help-block">必须输入小于1的浮点数，代表分销商的折扣，如3.5折则输入0.35</p>
	      </div>
	    </div>
	    <div class="control-group">
	        <label class="control-label" for="optionsCheckbox">自营</label>
	        <div class="controls">
	          <label class="checkbox">
	            <input type="checkbox" name="is_self" value="true">
				选中则表示是自营店铺，那么就不需要计费
	          </label>
	        </div>
	      </div>
	    </#if>
	  </fieldset>
	  
	  <input type="hidden" name="type" value="${userType.getValue()}"/>
	  <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存更改</button>
        <button class="btn" onClick="javascript: window.location.href='${rc.contextPath}/admin/account_list'; return false;">取消</button>
      </div>
	</form>
</@root.html>
