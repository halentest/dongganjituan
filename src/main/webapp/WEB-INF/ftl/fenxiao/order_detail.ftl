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
                <fieldset>
                  <legend>订单详情</legend>    
                        <input type="hidden" value="${order.id}" id="order-id"/>
                        <span class="distance">订单编号</span>
                        <span class="input-medium uneditable-input">${order.order_id?c}</span>
                        <span class="distance">收&nbsp;&nbsp;货&nbsp;&nbsp;人</span>
                        <span class="input-small uneditable-input">${order.name}</span>
                        <span class="distance">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</span>
                        <span class="input-medium uneditable-input">${order.phone}</span>
                        <br>
                        <span class="distance">详细地址</span>
                        <span class="span6 uneditable-input">${order.address}</span>
                        <br>
                        <span class="distance">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;编</span>
                        <span class="input-small uneditable-input">${order.postcode}</span>
                        <span class="distance">快&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;递</span>
                        <span class="input-small uneditable-input">${order.delivery}</span>
                        <span class="distance">总&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;重</span>
                        <span class="input-small uneditable-input">${order.total_weight/1000}</span>
                        <span class="distance">快&nbsp;&nbsp;递&nbsp;&nbsp;费</span>
                        <span class="input-small uneditable-input">${order.deliveryMoney/100}</span>
                        <br>
                        <span class="distance">货&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;款</span>
                        <span class="input-small uneditable-input">${order.huokuan/100}</span>
                        <br>
                        <span class="distance">订单状态</span>
                        <span class="input-small uneditable-input">${order.status}</span>
                        <br>
                        <button id="modify-order" class="btn btn-primary">修改</button>
                        <button id="add-goods" class="btn btn-primary">添加商品</button>
                </fieldset> 

                <fieldset>
                    <legend>商品列表</legend>
                        <span class="distance">订单编号</span>
                        <span class="input-medium uneditable-input">${order.order_id?c}</span>
                        <span class="distance">收&nbsp;&nbsp;货&nbsp;&nbsp;人</span>
                        <span class="input-small uneditable-input">${order.name}</span>
                        <span class="distance">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话</span>
                        <span class="input-medium uneditable-input">${order.phone}</span>
                        <br>
                        <span class="distance">详细地址</span>
                        <span class="span6 uneditable-input">${order.address}</span>
                        <br>
                        <span class="distance">邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;编</span>
                        <span class="input-small uneditable-input">${order.postcode}</span>
                        <br>
                        <span class="distance">快&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;递</span>
                        <span class="input-small uneditable-input">${order.delivery}</span>
                        <br>
                        <button id="submit1" class="btn btn-primary">修改</button>
                        <button id="submit1" class="btn btn-primary">添加商品</button>
                </fieldset> 
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
