<#import "/templates/root.ftl" as root >
<style>
	td.can-click {
		cursor: pointer;
	}
</style>

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
	<i class="icon-list-alt"></i>商品列表
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
	<#if list??>
	<#list list as goods>
	<#assign map2 = map[goods.hid]>
	<table>
        <tbody>
        	  <tr>
        	  		<th rowspan="${map2?size+1}" style="width: 12%;"><img style="width: 80px; height: 80px" src="${goods.url}_80x80.jpg" /></th>
        	  		<th rowspan="${map2?size+1}" style="width: 9%;">${goods.hid}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 15%;">${goods.title}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 5%;">${goods.price/100}元</th>
        	  		<th style="padding: 2px;">颜色</th>
        	  		<#list map2?keys as key2>
        	  			<#if key2_index==0>
        	  				<#list map2[key2]?keys as key3>
        	  					<th style="padding: 2px;">${key3}</th>
        	  				</#list>
        	  			</#if>
        	  		</#list>
        	  		<td rowspan="${map2?size+1}" style="width: 12%;">
        	  		<#if CURRENT_USER.type=="Distributor">
        	  			<p><a class="buy-button" data-goods="${goods.hid}" style="cursor: pointer;">点击购买</a></p>
        	  			<p><a class="add-to-cart" data-goods="${goods.hid}" style="cursor: pointer;">加入购物车</a></p>
        	  			<p><a href="${rc.contextPath}/trade/action/shopcart">查看购物车</a></p>
        	  		</#if>
        	  		<#if CURRENT_USER.type=="GoodsManager">
        	  			<p><a class="sync-store" data-goods="${goods.id}" style="cursor: pointer;">同步库存</a></p>
        	  		</#if>
        	  		</td>
        	  </tr>
        	  <#list map2?keys as key2>
        	  <tr>
        	  		<td>${key2}</td>
        	  		<#list map2[key2]?keys as key3>
        	  		<td data-goods="${goods.hid}" data-url="${goods.url}" data-title="${goods.title}" data-color="${key2}" data-size="${key3}" 
        	  			is-selected="false" class="can-click" style="padding: 2px;">${map2[key2][key3]}</td>
        	  		</#list>
        	  </tr>
        	  </#list>
	          
	        </#list>
        </tbody>
      </table>
      </#if>
      <div class="pagination" style="float: right;">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	 </div>
        
</@root.html>

<script>
	function initpage() {
	      $('#goods-id').val('${goods_id!""}');
	}
	
	$(document).ready(function(){
		  //$.cookie('name', 'value');
		  //alert($.cookie('name'));
		  initpage();
	      $('.pagination').jqPagination({
			    paged: function(page) {
			    	var goodsId = $('#goods-id').val();
			        window.location.href="/goods/goods_list?page=" + page + "&goods_id=" + goodsId;
			    }
		   });
		   
		   $('#search').click(function() {
		   		var goodsId = $('#goods-id').val();
		    	var page = $('#page').attr('data-current-page');
		   		window.location.href="/goods/goods_list?page=" + page + "&goods_id=" + goodsId;
		   });
		   
		   $('td.can-click').click(function() {
		   		var isSelected = $(this).attr("is-selected");
		   		if(isSelected=="false") {
		   			$(this).attr("is-selected", "true");
		   			$(this).css("background-color", "red");
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
		   
		   $('.sync-store').click(function() {
		   		$.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "ids=" + $(this).attr("data-goods"),
	            url: "${rc.contextPath}/goods/action/sync_store",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.success == false) {
	                	alert(result.errorInfo);
	                } else {
	                	alert("同步成功!");
	                }
	            }}); 
		   })
		   
	});
</script>

