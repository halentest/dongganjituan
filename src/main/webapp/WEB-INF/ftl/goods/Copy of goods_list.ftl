<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["jdpicker.css"] js=["highcharts.js", "exporting.js"]>
	<div style="width: 100%; height: 30px; background-color: #99CCCC; padding-top: 5px; padding-left: 20px;">
		<strong>商品编号</strong>
		<input id="goods-id" type="input" value="" style="width: 6%; height: 20px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>
	</div>
	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	<table>
      <!-- start 商品列表展示-->
	    <thead>
          <tr>
            <th>编号</th>
            <th>颜色</th>
            <th>重量(千克)</th>
            <th>单价(元)</th>
            <th>38</th>
            <th>39</th>
            <th>40</th>
            <th>41</th>
            <th>42</th>
            <th>43</th>
            <th>44</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
        	<#list goodsList as goods>
	          <tr>
	            <td>${goods.hid}</td>
	            <td>${goods.color}</td>
	            <td>${goods.weight/1000}</td>
	            <td>${goods.price/100}</td>
	            <td>${goods.thity_eight}</td>
	            <td>${goods.thity_nine}</td>
	            <td>${goods.forty}</td>
	            <td>${goods.forty_one}</td>
	            <td>${goods.forty_two}</td>
	            <td>${goods.forty_three}</td>
	            <td>${goods.forty_four}</td>
                <td>
                     <a href="#" onClick="modifyBase(${goods.id})">修改商品属性</a> 
                     <a href="#" onClick="modifyStore(${goods.id}, 1)">进货</a>   
                     <a href="#" onClick="modifyStore(${goods.id}, 0)">出货</a> 
                     <a href="#" onClick="addOrderDetail(${goods.id}, ${orderId})">添加</a>
                </td>
	          </tr>
	        </#list>
        </tbody>
      </table>
      <!-- end 商品列表展示-->

      <!-- start 修改商品属性 -->
      <div class="modal hide" id="modifyBaseModal">
          <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>
            <h4>修改商品属性</h4>
          </div>
          <div class="modal-body">
            <table class="table table-striped table-bordered table-condensed">
                <thead>
                  <tr>
                    <th>编号</th>
                    <th>颜色</th>
                    <th>重量(千克)</th>
                    <th>单价(元)</th>
                  </tr>
                </thead>
                <tbody>
                      <tr>
                        <td><input id="hid" type="text" class="input-small"></td>
                        <td><input id="color" type="text" class="input-small"></td>
                        <td><input id="weight" type="text" class="input-small"></td>
                        <td><input id="price" type="text" class="input-small"></td>
                        <input id="id" type="hidden"/>
                        <input id="modified" type="hidden"/>
                      </tr>
                </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">关闭</a>
            <a href="#" class="btn btn-primary" onClick="saveBase()">保存更新</a>
          </div>
      </div>
      <!-- start 修改商品属性 -->

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

      <!-- start 修改库存 -->
      <div class="modal hide" id="modifyStoreModal">
          <div class="modal-header">
            <a class="close" data-dismiss="modal">×</a>
            <h4 id="modifyStoreModal-title">进货</h4>
          </div>
          <div class="modal-body">
            <table class="table table-striped table-bordered table-condensed">
                <thead>
                  <tr>
                    <th>编号</th>
                    <th>颜色</th>
                    <th>38</th>
                    <th>39</th>
                    <th>40</th>
                    <th>41</th>
                    <th>42</th>
                    <th>43</th>
                    <th>44</th>
                  </tr>
                </thead>
                <tbody>
                      <tr>
                        <td><input id="store-hid" type="text" class="input-mini"></td>
                        <td><input id="store-color" type="text" class="input-mini" style="width: 30px;"></td>
                        <td><input id="store-38" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-39" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-40" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-41" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-42" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-43" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <td><input id="store-44" type="text" value="0" class="input-mini"  style="width: 30px;"></td>
                        <input id="store-id" type="hidden"/>
                        <input id="store-modified" type="hidden"/>
                        <input id="store-type" value="1" type="hidden" />
                        <input id="goods-id" type="hidden" />
                        <input id="order-id" type="hidden" value="0"/>
                      </tr>
                </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">取消</a>
            <a href="#" class="btn btn-primary" onClick="saveStore()">确定</a>
          </div>
      </div>
      <!-- end 进货 -->
        
</@root.html>

<script type="text/javascript">
    //start 修改商品属性
    function modifyBase(id) {
        $.ajax({
            type: "get",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            url: "${rc.contextPath}/huopin/get_goods_by_id",//要访问的后台地址
            data: "id=" + id,//要发送的数据
            success: function(goods){//msg为返回的数据，在这里做数据绑定
                $('#id').val(goods.id);
                $('#hid').val(goods.hid);
                $('#color').val(goods.color);
                $('#weight').val(goods.weight/1000);
                $('#price').val(goods.price/100);
                $('#modified').val(goods.modified);
                $('#modifyBaseModal').modal({
                    keyboard: false
                })
            }});        
    }

    function saveBase() {
        var goodsBase = '{"id": "' + $("#id").val() + '", "hid": "' + $("#hid").val() + '", "color": "' 
                          + $("#color").val() + '", "weight": "' + $("#weight").val()*1000 + '", "price": "' 
                          + $("#price").val()*100 + '", "modified": "' + $("#modified").val() + '"}';
        $.ajax({
            type: "post",
            contentType:"application/json; charset=utf-8",
            dataType: "json",
            processData: false,
            url: "${rc.contextPath}/huopin/update_goods_base",
            data: goodsBase,
            success: function(result){
                $('#modifyBaseModal').modal('hide')
                if(result.success==true) {
                    location.reload();
                } else {
                    $('#error-body').html("<p>" + result.errorInfo + "</p>");
                    $('#error-info').modal({
                        keyboard: false
                    })
                }               
            }}); 
    }
    //end 修改商品属性

    //start 当错误提示框关闭时刷新浏览器
    $(document).ready(function(){
          $('#error-info').on('hidden', function () {
              location.reload();
          })
    })


    //start 进货或者出货
    function modifyStore(id, type) {
        if(type==0) {
            $('#modifyStoreModal-title').html("出货");
            $('#store-type').val(0);
        } else if(type==2) {
            $('#modifyStoreModal-title').html("添加商品");
            $('#store-type').val(2);
        }
        $.ajax({
            type: "get",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            url: "${rc.contextPath}/huopin/get_goods_by_id",//要访问的后台地址
            data: "id=" + id,//要发送的数据
            success: function(goods){//msg为返回的数据，在这里做数据绑定
                $('#store-id').val(goods.id);
                $('#store-hid').val(goods.hid);
                $('#store-color').val(goods.color);
                $('#store-modified').val(goods.modified);
                $('#modifyStoreModal').modal({
                    keyboard: false
                })
            }});        
    }

    function saveStore() {
        var goodsStore = '{"id": "' + $("#store-id").val() + '", "thity_eight": "' + $("#store-38").val() + '", "thity_nine": "' 
                          + $("#store-39").val() + '", "forty": "' + $("#store-40").val() + '", "forty_one": "' 
                          + $("#store-41").val() + '", "forty_two": "' + $("#store-42").val() + '", "forty_three": "' + $("#store-43").val() 
                           + '", "forty_four": "' + $("#store-44").val() + '", "orderId": "' + $("#order-id").val() + '", "type": "' + $("#store-type").val() + '", "modified": "' + $("#store-modified").val() + '"}';
        $.ajax({
            type: "post",
            contentType:"application/json; charset=utf-8",
            dataType: "json",
            processData: false,
            url: "${rc.contextPath}/huopin/update_goods_store",
            data: goodsStore,
            success: function(result){
                $('#modifyStoreModal').modal('hide')
                if(result.success==true) {
                    location.reload();
                } else {
                    $('#error-body').html("<p>" + result.errorInfo + "</p>");
                    $('#error-info').modal({
                        keyboard: false
                    })
                }               
            }}); 
    }
    // end 进货

    // start 添加order detail
    function addOrderDetail(goodsId, orderId) {
        $('#goods-id').val(goodsId);
        $('#order-id').val(orderId);
        modifyStore(goodsId, 2);
    }
    // end 添加order detail
</script>
