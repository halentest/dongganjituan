<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >

	<form class="form-horizontal" action="${rc.contextPath}/admin/change_rate" method="post">
	  <fieldset>
	    <legend><i class="icon-pencil"></i><font size="3">修改上货比率</font></legend>
	    <div class="control-group">
	      <label class="control-label" for="input01">上货比率</label>
	      <div class="controls">
	        <input type="text" class="input-xlarge" name="v" value="${oldRate}" id="input01">
	         <p class="help-block">必须输入小于1的浮点数，代表店铺的上货比率，如35%则输入0.35</p>
	      </div>
	    </div>
	  </fieldset>
	  <input type="hidden" name="sId" value="${sId}"/>
	  <div class="form-actions">
        <button type="submit" class="btn btn-primary">保存更改</button>
        <button class="btn" onClick="javascript: window.location.href='${rc.contextPath}/admin/action/account_list'; return false;">取消</button>
      </div>
	</form>
</@root.html>
