<#import "/templates/root.ftl" as root >
<@root.html css=["all.css", "easyui.css", "icon.css"] js=["jquery.easyui.min.js"] >
	<button id="logistics">同步物流信息(慎用)</button>
	<button id="item">同步商品信息(慎用)</button>
	<button id="trade">同步订单信息(慎用)</button>
	<button id="area">同步地区信息(慎用)</button>
    <div id="loading" class="easyui-window" title="同步中，请稍后!" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false" style="width:600px;height:280px;padding:2px;">
        <img style="margin-left: 200px;" src="${rc.contextPath}/img/loading.gif" />
    </div>

	 <div id="status" style="margin-top: 50px;">
	 </div>
	
</@root.html>
<script>
	$(document).ready(function(){
		$('#logistics').click(function() {
			$.ajax({
		            type: "get",//使用get方法访问后台
		            dataType: "json",//返回json格式的数据
		            beforeSend: function() {
		            	$('#loading').window('open');
		            },
		            url: "${rc.contextPath}/admin/sync_logistics",//要访问的后台地址
		            //data: "id=" + id,//要发送的数据
		            success: function(result){//msg为返回的数据，在这里做数据绑定
		            			$('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
			                	$('#loading').window('close');
			                }
		                });  
		})
		
		$('#item').click(function() {
			$.ajax({
		            type: "get",//使用get方法访问后台
		            dataType: "json",//返回json格式的数据
		            beforeSend: function() {
		            	$('#loading').window('open');
		            },
		            url: "${rc.contextPath}/admin/sync_item",//要访问的后台地址
		            //data: "id=" + id,//要发送的数据
		            success: function(result){//msg为返回的数据，在这里做数据绑定
		            			$('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
			                	$('#loading').window('close');
			                }
		                });  
		})
		
		$('#trade').click(function() {
			$.ajax({
		            type: "get",//使用get方法访问后台
		            dataType: "json",//返回json格式的数据
		            beforeSend: function() {
		            	$('#loading').window('open');
		            },
		            url: "${rc.contextPath}/admin/sync_trade",//要访问的后台地址
		            //data: "id=" + id,//要发送的数据
		            success: function(result){//msg为返回的数据，在这里做数据绑定
		            			$('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
			                	$('#loading').window('close');
			                }
		                });  
		})
		
		$('#area').click(function() {
			$.ajax({
		            type: "get",//使用get方法访问后台
		            dataType: "json",//返回json格式的数据
		            beforeSend: function() {
		            	$('#loading').window('open');
		            },
		            url: "${rc.contextPath}/admin/sync_area",//要访问的后台地址
		            //data: "id=" + id,//要发送的数据
		            success: function(result){//msg为返回的数据，在这里做数据绑定
		            			$('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
			                	$('#loading').window('close');
			                }
		                });  
		})
		
	})
</script>