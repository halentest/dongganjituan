<#import "/templates/root.ftl" as root >
<@root.html active=3 css=["jdpicker.css"] js=["highcharts.js", "exporting.js"]>
    <style type="text/css">
        span.distance {
            display: inline-block;
            height: 25px;
            margin-right: 3px;
            vertical-align: middle;
            padding-bottom: 4px;
        }

        input.my-input {
            margin-right: 25px;
        }
    </style>
    <div style="width: 950px;">
        <div style="width: 750px; margin-left: 215px;">
            <form id="add-order-form" action="/fenxiao/add_order" method="post" Content-Type="application/json; charset=utf-8">
                <fieldset>
                  <legend>新建订单</legend>
                    
                        <span class="distance">订单编号</span><input type="text" class="input-medium my-input" name="orderId" id="orderId">
                        <span class="distance">收&nbsp;&nbsp;货&nbsp;&nbsp;人</span><input type="text" class="input-small my-input" name="name" id="name">
                        <span class="distance">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</span><input type="text" name="phone" class="input-medium my-input" id="phone">
                        <br>    
                        <span class="distance">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址</span>
                        <select id="province" class="span2">
                            <option value="0">选择省</option>
                        </select>
                        <select id="cityId" class="span2" name="cityId">
                            <option value="0">选择市</option>
                        </select>
                        <br>
                        <span class="distance">详细地址</span><input type="text" name="address" class="span6 my-input" id="address"><br>
                        <span class="distance">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;编</span><input type="text" class="input-small my-input" name="postcode" id="postcode">
                        <br>
                        <span class="distance">快&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;递</span>
                        <select id="deliveryId" name="deliveryId" class="span2">
                            <option value="0">选择快递</option>
                        </select>
                </fieldset>
            </form>
            <div class="form-actions">
                <button id="submit1" class="btn btn-primary">确定</button>
                <button class="btn">取消</button>
            </div>
          </div>
      </div>
      
      <!-- start 更新出错提示框 -->
      <div class="modal hide" id="error-info">
          <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>
            <h4>出错啦！</h4>
          </div>
          <div id="error-body" class="modal-body">
            
          </div>
          <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">关闭</a>
          </div>
      </div>
      <!-- end 更新出错提示框 -->
        
</@root.html>

<script type="text/javascript">
    //加载快递列表
    $(document).ready(function(){
          $.ajax({
            type: "get",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            data: null,
            url: "${rc.contextPath}/fenxiao/list_province",//要访问的后台地址
            success: function(province_list){//msg为返回的数据，在这里做数据绑定
                $.each(province_list, function(index, province) {
                    $('#province').append('<option value="' + province.id + '">' + province.name + '</option>');
                });
          }}); 

          $('#province').change(function() {
              $('#cityId').empty();
              $('#cityId').append('<option vlaue="0">选择市</option>');
              $('#deliveryId').empty();
              $('#deliveryId').append('<option vlaue="0">选择快递</option>');
              $.ajax({
                type: "get",//使用get方法访问后台
                dataType: "json",//返回json格式的数据
                data: "pid=" + $(this).val(),
                url: "${rc.contextPath}/fenxiao/list_city",//要访问的后台地址
                success: function(city_list){//msg为返回的数据，在这里做数据绑定
                    $.each(city_list, function(index, city) {
                        $('#cityId').append('<option value="' + city.id + '">' + city.name + '</option>');
                    });
                }}); 
          }) 

          $('#cityId').change(function() {
              $('#deliveryId').empty();
              $('#deliveryId').append('<option vlaue="0">选择快递</option>');
              $.ajax({
                type: "get",//使用get方法访问后台
                dataType: "json",//返回json格式的数据
                data: "cityId=" + $(this).val(),
                url: "${rc.contextPath}/fenxiao/list_delivery",//要访问的后台地址
                success: function(delivery_list){//msg为返回的数据，在这里做数据绑定
                    $.each(delivery_list, function(index, delivery) {
                        $('#deliveryId').append('<option value="' + delivery.id + '">' + delivery.name + '</option>');
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
                        window.location.href = "${rc.contextPath}/fenxiao/order_detail?order_id=" + $('#orderId').val().trim();
                    } else {
                        $('#error-body').html("<p>" + result.errorInfo + "</p>");
                        $('#error-info').modal({
                            keyboard: false
                        })
                    } 
              }}); 
          })
    })
</script>
