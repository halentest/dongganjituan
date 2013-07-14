<#import "/templates/root.ftl" as root >
<style>
	input.my-input {
		height: 50px;
	}
</style>

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
	<#if errorInfo??>
		${errorInfo}
	<#else>
		<p>创建订单成功！</p>
	</#if>
</@root.html>
<script>
	$(document).ready(function() {
		<#if errorInfo??>
		<#else>
			$.cookie("orders", null, {path: '/'});
		</#if>
	})
</script>