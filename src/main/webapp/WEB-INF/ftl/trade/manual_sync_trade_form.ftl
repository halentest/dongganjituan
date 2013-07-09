<#import "/templates/root.ftl" as root >
<@root.html active=3 css=[] js=[]>
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
              	<select id="seller-nick" style="width: 18%;">
					<#list shopList as shop>
					<option value="${shop.sellerNick}">${shop.sellerNick}</option>
					</#list>
				</select>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="select01">开始时间</label>
            <div class="controls">
              	<input id="start" type="text" placeholder="格式:2013-06-29 12:30:30">
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="select01">结束时间</label>
            <div class="controls">
              	<input id="end" type="text" placeholder="格式:2013-06-29 12:30:30">
            </div>
          </div>
    </form>
          <div style="padding-left: 350px;" class="form-actions" >
            <button id="submit" class="btn btn-primary">确定</button>
          </div>
	
	
	<div class="modal hide" id="loading">
	    <div class="modal-header">
	        <h4>同步中，请稍后！</h4>
	    </div>
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
    var start = $('#start').val();
    if(!strDateTime(start)) {
    alert("请输入正确的开始时间");
    return false;
    }
    var end = $('#end').val();
    if(!strDateTime(end)) {
    alert("请输入正确的结束时间");
    return false;
    }
    $.ajax({
    type: "get",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    beforeSend: function() {
    $('#loading').modal({
    keyboard: false
    })
    },
    url: "${rc.contextPath}/trade/action/manual_sync_trade",//要访问的后台地址
    data: "sellerNick=" + sellerNick + "&start=" + start + "&end=" + end,//要发送的数据
    success: function(result){//msg为返回的数据，在这里做数据绑定
    $('#status').html('<div class="alert alert-success">' + result.errorInfo + '</div>');
    $('#loading').modal('hide')
    }
    });
    })
    })

    function strDateTime(str)
    {
    var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
    var r = str.match(reg);
    if(r==null)return false;
    var d= new Date(r[1], r[3]-1,r[4],r[5],r[6],r[7]);
    return (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]&&d.getHours()==r[5]&&d.getMinutes()==r[6]&&d.getSeconds()==r[7]);
    }
</script>


