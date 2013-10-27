<#import "/templates/root.ftl" as root >
<@root.html active=3 css=["easyui.css", "icon.css"] js=["jquery.easyui.min.js"]>
	<br><br>
    <#if shopList?size==0>
        <div class="alert">
            您目前没有店铺可以自动同步
        </div>
    <#else>
	<form class="form-horizontal">
            选择店铺
              	<select id="seller-nick">
					<#list shopList as shop>
					<option value="${shop.seller_nick}">${shop.seller_nick}</option>
					</#list>
				</select>
        <br>
            开始时间
              	<input id="start" class="easyui-datetimebox" type="text">
        <br>
            结束时间
              	<input id="end" type="text" class="easyui-datetimebox">
    </form>
          <div class="form-actions" >
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


