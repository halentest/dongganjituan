<#import "/templates/root.ftl" as root >
<@root.html active=3 css=["easyui.css", "icon.css"] js=["jquery.easyui.min.js"]>
	<i class="icon-pencil"></i>手工同步订单(正常情况下不需要手工同步订单，除非发现店铺内有订单同步失败!)
	<br><br>
    <#if shopList?size==0>
        <div class="alert">
            您目前没有店铺可以自动同步
        </div>
    <#else>
	<form class="form-horizontal">
          <div class="control-group">
            <label class="control-label" for="select01">店铺</label>
            <div class="controls">
              	<select id="seller-nick">
					<#list shopList as shop>
					<option value="${shop.sellerNick}">${shop.sellerNick}</option>
					</#list>
				</select>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="select01">开始时间</label>
            <div class="controls">
              	<input id="start" class="easyui-datetimebox" type="text">
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="select01">结束时间</label>
            <div class="controls">
              	<input id="end" type="text" class="easyui-datetimebox">
            </div>
          </div>
    </form>
          <div style="padding-left: 350px;" class="form-actions" >
            <button id="submit" class="btn btn-primary">确定</button>
          </div>
	
	
	<div class="easyui-window" title="正在同步订单" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false" style="width:600px;height:280px;padding:2px;" id="loading">
	    <img style="margin-left: 200px;" src="${rc.contextPath}/img/loading.gif" />
	 </div>
	 <div id="status" style="margin-top: 50px;">
	 </div>

    </#if>
</@root.html>
<script>
    $(document).ready(function() {
        $('#submit').click(function() {
            var sellerNick = $('#seller-nick').val();
            var start = $('#start').datetimebox('getValue');
            if(!start || start=="") {
                alert("请输入正确的开始时间");
                return false;
            }
            var end = $('#end').datetimebox('getValue');
            if(!end || end=="") {
                alert("请输入正确的结束时间");
                return false;
            }
            $.ajax({
                type: "get",//使用get方法访问后台
                dataType: "json",//返回json格式的数据
                beforeSend: function() {
                    $('#loading').window('open');
                },
                url: "${rc.contextPath}/trade/action/manual_sync_trade",//要访问的后台地址
                data: "sellerNick=" + sellerNick + "&start=" + start + "&end=" + end,//要发送的数据
                success: function(result){//msg为返回的数据，在这里做数据绑定
                $('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
                $('#loading').window('close');
                }
            });
        })
    })
</script>


