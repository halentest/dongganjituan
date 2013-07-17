<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
<i class="icon-pencil"></i>修改收货地址
<br>  <br><br>
<form action="${rc.contextPath}/trade/action/modify_receiver_info" method="">
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
    <input type="hidden" name="tid" value="${tid}"/>
<p>
收&nbsp;&nbsp;货&nbsp;&nbsp;人：
	<input name="receiver" class="my-input" type="text"/>
手机：
	<input name="mobile" class="my-input" type="text"/>
电话:
	<input name="phone" class="my-input" type="text"/>
</p>
<div class="form-actions" >
    <input type="submit" class="btn btn-primary" value="保存更改" style="margin-left: 60%;">
    <input onclick="history.go(-1)" style="margin-left: 10px;" type="reset" class="btn btn-inverse" value="取消"/>
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

    })
</script>

