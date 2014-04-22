<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["jqpagination.css", "easyui.css", "icon.css", "table.css", "all.css"] js=["goods_list.js", "jquery.cookie.js", "jquery.jqpagination.min.js", "jquery.easyui.min.js"]>

	<div style="width: 98%; height: 30px; background-color: #d6dff7; padding-top: 5px; padding-left: 20px;">
		<strong>商品编号</strong>
		<input id="goods-id" type="input" value="" style="width: 6%; height: 15px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}个商品 (${lockQuantity!'0'}/${quantity!'0'})
		<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
		&nbsp;&nbsp;<a href="${rc.contextPath}/trade/action/shopcart">查看购物车</a>
		</#if>
        <#if (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor") && 1==status>
            &nbsp;<a id="sync-store-all" title="同步所有商品">同步库存</a>
        </#if>
        &nbsp;<a href="${rc.contextPath}/goods/export">导出库存</a>
    </div>
<#if list?? && list?size gt 0>
	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>

	<br><br><br>
	<div>
		<a id="check-all" style="cursor: pointer;">全选</a>
		<a id="not-check-all" style="cursor: pointer;">全不选</a>
        <#if (CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor") && 1==status>
		    <a id="sync-store" title="同步选择的商品">同步库存</a>
        </#if>
        <#if CURRENT_USER.type=="GoodsManager">
            <#if status==1>
                <a class="btn btn-primary" id="change-template">修改运费模板</a>
                <a class="btn btn-primary" id="sync-pic">同步商品图片</a>
            </#if>
            <#if status==1>
                <a class="btn btn-primary" id="sold-out">下架商品</a>
            <#elseif status==0>
                <a class="btn btn-primary" id="sold-on">上架商品</a>
            </#if>
        </#if>
	</div>
	<#if list??>
	<#list list as goods>
	<#assign map2 = map[goods.hid]>
	<table>
        <tbody>
        	  <tr>
        	  		<th rowspan="${map2?size+1}" style="width: 2%;"><input class="wait-check" data-hid=${goods.hid} type="checkbox"/></th>
        	  		<th rowspan="${map2?size+1}" style="width: 12%;">
                        <#if goods.url??>
                            <#assign picPath = goods.url/>
                        <#else>
                            <#assign picPath = 'http://img01.tbsandbox.com/bao/uploaded/i1/T1R1CzXeRiXXcckdZZ_032046.jpg'/>
                        </#if>
        	  		    <img style="width: 80px; height: 80px" src="${picPath}_80x80.jpg" />
        	  		</th>
        	  		<th rowspan="${map2?size+1}" style="width: 9%;">${goods.hid}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 15%;">${goods.title!''}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 7%;">${goods.template!''}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 5%;">${goods.price/100}元</th>
        	  		<th style="padding: 2px;">颜色</th>
        	  		<#list map2?keys as key2>
        	  			<#if key2_index==0>
        	  				<#list map2[key2]?keys as key3>
        	  					<th class="can-change" data-goods="${goods.hid}" data-type="size" data-value="${key3}" style="padding: 2px;">${key3}</th>
        	  				</#list>
        	  			</#if>
        	  		</#list>
        	  		<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
        	  		<td rowspan="${map2?size+1}" style="width: 12%;">
                        <#if status==1>
                            <a class="buy-button" data-goods="${goods.hid}" style="cursor: pointer;">购买</a>
                            <a class="add-to-cart" data-goods="${goods.hid}" style="cursor: pointer;">加入购物车</a>
                            <#if tid??><a onclick="addGoods('${tid}', '${goods.hid}', '${from!''}')">添加</a></#if>
                        </#if>
        	  		</td>
        	  		</#if>
        	  </tr>
        	  <#list map2?keys as key2>
        	  <tr>
        	  		<td data-goods="${goods.hid}" data-type="color" data-value="${key2}" class="can-change">${key2} (${goodsCount[goods.hid + key2]!'0/0'})</td>
        	  		<#list map2[key2]?keys as key3>
        	  		<td data-goods="${goods.hid}" data-url="${goods.url!''}" data-title="${goods.title}" data-color="${key2?substring(0, key2?index_of('('))}" data-size="${key3}"
        	  			is-selected="false" style="padding: 2px;"
                        <#assign q = map2[key2][key3]?split("/")/>
                        <#assign savedQ = q[0]?number/>
                        <#assign actualQ = q[1]?number/>
        	  			<#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
                            <#if savedQ &gt; 0>
                                class="can-click"
                            </#if>
                        </#if>
                        <#if CURRENT_USER.type=="GoodsManager">
                                class="can-change-store"
                            data-saved="${savedQ?c}" data-origin="${map2[key2][key3]}"
                        </#if>
                        >
                        <#if savedQ != actualQ>
                            <font color='red'>${map2[key2][key3]}</font>
                        <#else>
                            ${map2[key2][key3]}
                        </#if>
        	  		</td>
        	  		</#list>
        	  </tr>
        	  </#list>
        </tbody>
      </table>
      </#list>
      </#if>
      <div class="pagination" style="float: right;">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	 </div>
<#else>
        <div class="alert" style="margin: 5px;">
            <a class="close" data-dismiss="alert">×</a>
            <strong>无内容！</strong>
        </div>
</#if>
</@root.html>

<!-- start 提示框 -->
  <div id="pop-up" class="easyui-window" title="选择模板" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false" style="width:300px;height:150px;padding:2px;">
      <div class="modal-body">
	    <p>
	    	请选择模板 
	    	<select id="template">
	    	    <#if templateList??>
                    <#list templateList as template>
                    <option value=${template}>${template}</option>
                    </#list>
	    		</#if>
	    	</select>
	    	<input type="hidden" id="hids"/>
	    </p>
	  </div>
      <div style="text-align:center;padding:5px">
          <a class="easyui-linkbutton" onclick="saveTemplate()">确定</a>
          <a class="easyui-linkbutton" onclick="cancelTemplate()">取消</a>
      </div>
  </div>
  <!-- end 提示框 -->

<!-- start 提示框 -->
<div id="pop-up-sync-store" class="easyui-window" title="选择店铺" data-options="modal:true,collapsible:false,closed:true,
        resizable:false,shadow:false,minimizable:false, maximizable:false" style="width:300px;height:150px;padding:10px;">
        <#if shopList?? && shopList?size &gt; 0>
            选择店铺
            <select id="seller-nick">
                <#list shopList as shop>
                    <option value="${shop.seller_nick}">${shop.seller_nick}</option>
                </#list>
            </select>
            <div style="text-align:center;padding:5px;margin-top:15px;">
                <a class="easyui-linkbutton" onclick="saveSyncStore()">确定</a>
                <a class="easyui-linkbutton" onclick="cancelSyncStore()">取消</a>
            </div>
        <#else>
            您目前没有店铺可以同步库存
        </#if>
</div>
<!-- end 提示框 -->

<div id="loading" class="easyui-window" title="请稍后..." style="width:148px;height:54px;"
     data-options="modal:true,closed:true,collapsible:false,minimizable:false,maximizable:false,closable:false,
     draggable:false,resizable:false">
    <img src="/img/ajax-loader.gif"/>
</div>

<div id="dlg" class="easyui-window" title="执行结果" style="width:300px;height:300px; padding: 5px;"
     data-options="modal:true,resizable:true,collapsible:false,closed:true,closable:true,minimizable:false,maximizable:false,onClose:function(){window.location.reload();}">
</div>

<script>

	function initpage() {
	      $('#goods-id').val('${goods_id!""}');
	}
	
	$(document).ready(function(){

	    $(document).bind("ajaxSend", function(){
            $('#loading').window('open');
        }).bind("ajaxComplete", function(){
            $('#loading').window('close');
        });
		  //$.cookie('name', 'value');
		  //alert($.cookie('name'));
		  initpage();
	      $('.pagination').jqPagination({
			    paged: function(page) {
			    	var goodsId = $('#goods-id').val();
                    if(!page) {
                        page = 1;
                    }
			        window.location.href="/goods/goods_list?page=" + page + "&goods_id=" + goodsId + "&tid=${tid!''}" + "&status=${status}";
			    }
		   });
		   
		   $('#search').click(function() {
		   		var goodsId = $('#goods-id').val();
		    	var page = $('#page').attr('data-current-page');
                if(!page) {
                    page = 1;
                }
		   		window.location.href="/goods/goods_list?page=" + page + "&goods_id=" + goodsId + "&tid=${tid!''}" + "&status=${status}";
		   });

		   $('td.can-click').click(function() {
		   		var isSelected = $(this).attr("is-selected");
		   		if(isSelected=="false") {
		   			$(this).attr("is-selected", "true");
		   			$(this).css("background-color", "#d6dff7");
		   		} else {
		   			$(this).attr("is-selected", "false");
		   			$(this).css("background-color", "white");
		   		}
		   })
		   
		   $('.buy-button').click(function() {
		   		var goods = $(this).attr("data-goods");
		   		var selected = $("[data-goods='" + goods + "'][is-selected='true']");
		   		if(selected.size()==0) {
		   			alert("请选择要购买的商品！");
		   			return;
		   		}
		   		var orders = ''
		   		selected.each(function(index, element) {
		   			orders += goods + ',' + $(this).attr("data-url") + ',' + $(this).attr("data-title")
		   				 + ',' + $(this).attr("data-color") + ',' +
		   				$(this).attr("data-size") + ',1';
		   			if(index != selected.size()-1) {
		   				orders += ':::';
		   			}
		   		})
		   		window.location.href = "${rc.contextPath}/trade/action/buy_goods_form?orders=" + orders;
		   })
		   
		   $('.add-to-cart').click(function() {
		   		var goods = $(this).attr("data-goods");
		   		var selected = $("[data-goods='" + goods + "'][is-selected='true']");
		   		if(selected.size()==0) {
		   			alert("请选择要购买的商品！");
		   			return;
		   		}
		   		var orders = ''
		   		selected.each(function(index, element) {
		   			orders += goods + ',' + $(this).attr("data-url") + ',' + $(this).attr("data-title")
		   				 + ',' + $(this).attr("data-color") + ',' +
		   				$(this).attr("data-size") + ',1';
		   			if(index != selected.size()-1) {
		   				orders += ':::';
		   			}
		   		})
		   		var originalOrders = $.cookie("orders");
		   		if(originalOrders != null) {
		   			orders = originalOrders + ":::" + orders;
		   		}
		   		$.cookie('orders', orders, { expires: 7, path: '/' });
		   		window.location.href = "${rc.contextPath}/trade/action/shopcart";
		   })
		   
		   $('#check-all').click(function() {
	    		$('.wait-check').attr("checked", true);
	    	})
	    	
	    	$('#not-check-all').click(function() {
	    		$('.wait-check').attr("checked", false);
	    	})
	    	
	    	$('#sync-pic').click(function() {
	    		var checked = $('.wait-check:checked');
	    		if(checked.length==0) {
	    			alert('至少选中一个商品!');
	    			return false;
	    		}
	    		var hids = "";
	    		$(checked).each(function(index, item) {
	    			var hid = $(item).attr("data-hid");
	    			hids += hid;
	    			hids += ";";
	    		})
    			$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "hids=" + hids + "&action=sync-pic",
	            url: "${rc.contextPath}/goods/batch_change",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
		                if(result.errorInfo != "success") {
		                	alert(result.errorInfo);
		                }  else {
		                	alert("同步图片成功!");
		                	window.location.reload();
		                }
	            }}); 
	    	})
	    	
        //修改库存
        $('.can-change-store').dblclick(function() {
            var v = $(this).attr('data-saved');
            $(this).html('<input id="tempInput" style="width: 50px; height: 12px;" type="text" onblur="changeGoods(this)" value=' + v + '>');
            $('#tempInput').focus();
        });

	});

    function changeGoods(param) {
        var isTrue = confirm("确定修改它吗？");
        var value = $(param).parent().attr('data-origin');
        if(!isTrue) {
            $(param).parent().html(value);
            return false;
        }
        var color = $(param).parent().attr('data-color');
        var hid = $(param).parent().attr('data-goods');
        var size = $(param).parent().attr('data-size');
        var newValue = $(param).val();
        var oldValue = $(param).parent().attr('data-saved');
        if(oldValue==newValue) {
            $(param).parent().html(value);
            return;
        }
        $.ajax({
            type: "post",//使用get方法访问后台
            dataType: "json",//返回json格式的数据
            data: "hid=" + hid + "&color=" + color + "&size=" + size + '&newValue=' + newValue + "&oldValue=" + oldValue,
            url: "${rc.contextPath}/goods/action/change_goods",//要访问的后台地址
            success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
            } else {
                window.location.reload();
            }
        }});

    }
</script>

