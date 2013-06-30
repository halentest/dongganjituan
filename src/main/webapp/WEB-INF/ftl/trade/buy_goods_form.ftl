<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
<i class="icon-pencil"></i>创建订单
<form action="${rc.contextPath}/trade/action/buy_goods" method="">
<#if fromcart??>
	111
<#else>
<table>
	<#list orderList as order>
	<input type="hidden" name="goods${order_index}" value="${order.goodsId}"/>
	<input type="hidden" name="color${order_index}" value="${order.color}"/>
	<input type="hidden" name="size${order_index}" value="${order.size}"/>
	<input type="hidden" name="token" value="${token}"/>
	<tr>
		<td><img src="${order.url}_80x80.jpg"/></td>
		<td>${order.goodsId}</td>
		<td>${order.title}</td>
		<td>
			<p>颜色: ${order.color}</p>
			<p>尺码: ${order.size}</p>
		</td>
		<td>
			<p>数量：<input name="count${order_index}" style="margin-top: 5px; width: 30px;" type="text" value="${order.count}" /></p>
		</td>
	</tr>
	</#list>
</table>
</#if>
<br>
<p>选择快递：
	<select id="logistics" name="logistics" style="">
		<#list logistics as lo>
			<option value="${lo.code}" <#if lo.status==1>selected</#if>>${lo.name}</option>
		</#list>
	</select>
</p>
<p>选择地址：
	<select id="province" name="province">
		<option value="-1">选择省</option>
	</select>
	<select id="city" name="city">
		<option value="-1">选择市</option>
	</select>
	<select id="district" name="district">
		<option value="-1">选择区</option>
	</select>
	邮编：
	<input name="postcode" class="my-input" type="text"/>
</p>
<p>
详细地址：
	<input name="address" class="my-input" style="width: 50%;" type="text"/>
</p>
<p>
给仓库留言：
	<input name="seller_memo" class="my-input" style="width: 50%;" type="text"/>
</p>
<p>
收&nbsp;&nbsp;货&nbsp;&nbsp;人：
	<input name="receiver" class="my-input" type="text"/>
手机：
	<input name="mobile" class="my-input" type="text"/>
电话:
	<input name="phone" class="my-input" type="text"/>
</p>
<div class="form-actions" >
    <input type="submit" class="btn btn-primary" value="确定购买" style="margin-left: 60%;">
</div>
</form>
</@root.html>

<script type="text/javascript">
    //加载快递列表
    $(document).ready(function(){
          $.ajax({
            type: "get",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            data: null,
            url: "${rc.contextPath}/list_province",//要访问的后台地址
            success: function(province_list){//msg为返回的数据，在这里做数据绑定
                $.each(province_list, function(index, province) {
                    $('#province').append('<option value="' + province.id + '">' + province.name + '</option>');
                });
          }}); 

          $('#province').change(function() {
              $('#city').empty();
              $('#city').append('<option vlaue="0">选择市</option>');
              $('#district').empty();
              $('#district').append('<option vlaue="-1">选择区</option>');
              $.ajax({
                type: "get",//使用get方法访问后台
                dataType: "json",//返回json格式的数据
                data: "province_id=" + $(this).val(),
                url: "${rc.contextPath}/list_city",//要访问的后台地址
                success: function(city_list){//msg为返回的数据，在这里做数据绑定
                    $.each(city_list, function(index, city) {
                        $('#city').append('<option value="' + city.id + '">' + city.name + '</option>');
                    });
                }}); 
          }) 

          $('#city').change(function() {
              $('#district').empty();
              $('#district').append('<option vlaue="-1">选择区</option>');
              $.ajax({
                type: "get",//使用get方法访问后台
                dataType: "json",//返回json格式的数据
                data: "city_id=" + $(this).val(),
                url: "${rc.contextPath}/list_district",//要访问的后台地址
                success: function(district_list){//msg为返回的数据，在这里做数据绑定
                    $.each(district_list, function(index, district) {
                        $('#district').append('<option value="' + district.id + '">' + district.name + '</option>');
                    });
                }}); 
          }) 

          $('#submit1').click(function() {
              data = '{"orderId": "' + $('#orderId').val() + '", "name": "' + $('#name').val() + '", "cityId" : "' + $('#cityId').val() +'", "deliveryId": "' + $('#deliveryId').val() +
                          '", "phone": "' + $('#phone').val() + '", "address": "' + $('#address').val() + '", "postcode": "' + $('#postcode').val() + '"}';
              $.ajax({
                type: "post",
                contentType:"application/json; charset=utf-8",
                dataType: "json",//返回json格式的数据
                processData: false,
                data: data,
                url: "${rc.contextPath}/fenxiao/add_order",//要访问的后台地址
                success: function(result){//msg为返回的数据，在这里做数据绑定
                    if(result.success==true) {
                        window.location.href = "${rc.contextPath}/fenxiao/order_detail"
                    } else {
                        $('#error-body').html("<p>" + result.errorInfo + "</p>");
                        $('#error-info').modal({
                            keyboard: false
                        })
                    } 
              }}); 
          })
          
          // add goods to order
          $('#add-goods').click(function() {
              window.location.href = "${rc.contextPath}/goods/goods_list?order_id=" + $('#order-id').val();
          })
    })
</script>

