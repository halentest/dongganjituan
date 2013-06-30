<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >

	<form class="form-horizontal" action="${rc.contextPath}/admin/modify_password" method="post">
	  <fieldset>
	    <legend><i class="icon-pencil"></i><font size="3">修改密码</font></legend>
	    <div class="control-group">
	      <label class="control-label" for="input01">用户名</label>
	      <div class="controls">
	        ${username}
	      </div>
	      <input type="hidden" name="username" value="${username}"/>
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
	  <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存更改</button>
        <button class="btn" onClick="javascript: window.location.href='${rc.contextPath}/admin/action/account_list'; return false;">取消</button>
      </div>
	</form>
</@root.html>
