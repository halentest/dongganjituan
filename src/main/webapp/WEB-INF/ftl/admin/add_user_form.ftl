<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >

	<form class="form-horizontal" action="${rc.contextPath}/admin/add_user" method="post">
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
	  </fieldset>
	  
	  <input type="hidden" name="type" value="${userType.getValue()}"/>
	  <#if shopId??>
	  <input type="hidden" name="shopId" value="${shopId}"/>
	  </#if>
	  <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存更改</button>
        <button class="btn" onClick="javascript: window.location.href='${rc.contextPath}/admin/action/account_list'; return false;">取消</button>
      </div>
	</form>
</@root.html>
