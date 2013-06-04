<#import "/templates/root.ftl" as root >
<style>
</style>

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
<table>
	<#list orderList as order>
	<tr>
		<td><img src="${order.url}_80x80.jpg"/></td>
		<td>${order.goodsId}</td>
		<td>${order.title}</td>
		<td>
			<p>颜色: ${order.color}</p>
			<p>尺码: ${order.size}</p>
		</td>
		<td>
			<p>数量：<input style="margin-top: 5px; width: 30px;" type="text" value="${order.count}" /></p>
		</td>
	</tr>
	</#list>
</table>
<p>选择快递：
	<select id="logistics" style="">
		<#list logistics as lo>
			<option value="${lo.code}" <#if lo.status==1>selected</#if>>${lo.name}</option>
		</#list>
	</select>
	
	<select id="province">
		<option value="-1">选择省</option>
	</select>
	<select id="city">
		<option value="-1">选择市</option>
	</select>
	<select id="district">
		<option value="-1">选择区</option>
	</select>
</p>
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
              window.location.href = "${rc.contextPath}/huopin/goods_list?order_id=" + $('#order-id').val();
          })
    })
</script>

