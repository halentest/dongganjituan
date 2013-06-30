<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >

	<form class="form-horizontal" action="${rc.contextPath}/admin/add_shop" method="post">
	  <fieldset>
	    <legend><i class="icon-pencil"></i><font size="3">新建店铺</font></legend>
	    <div class="control-group">
	      <label class="control-label" for="input01">店铺名称</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="sellerNick" id="input01">
	      </div>
	    </div>
	    <div class="control-group">
	      <label class="control-label" for="input01">店铺类型</label>
	      <div class="controls">
	        <select name="type" style="width: 15%;">
				<option value="淘宝c店">淘宝c店</option>
				<option value="天猫商城">天猫商城</option>
			</select>
	      </div>
	    </div>
	  </fieldset>
	  
	  <input type="hidden" name="dId" value="${dId}"/>
	  <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存更改</button>
        <button class="btn" onClick="javascript: window.location.href='${rc.contextPath}/admin/action/account_list'; return false;">取消</button>
      </div>
	</form>
</@root.html>
