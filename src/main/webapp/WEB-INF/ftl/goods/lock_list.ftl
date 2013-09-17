<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["trade_list.css", "jqpagination.css"] js=["jquery.cookie.js", "jquery.jqpagination.min.js"]>
	<style>
		td.can-click {
			cursor: pointer;
		}
	</style>
	<div style="width: 98%; height: 30px; background-color: #d6dff7; padding-top: 5px; padding-left: 20px;">
		<strong>商品编号</strong>
		<input id="goods-id" type="input" value="" style="width: 6%; height: 15px;"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="search">搜索</button>&nbsp;&nbsp;&nbsp;&nbsp;共${totalCount}个商品 (${lockQuantity!'0'}/${quantity!'0'})
	</div>
<#if list?? && list?size gt 0>
	<div class="pagination">
	    <a href="#" class="first" data-action="first">&laquo;</a>
	    <a href="#" class="previous" data-action="previous">&lsaquo;</a>
	    <input id="page" type="text" readonly="readonly" data-current-page="${paging.page}" data-page-string="1 of 3" data-max-page="${paging.pageCount}" />
	    <a href="#" class="next" data-action="next">&rsaquo;</a>
	    <a href="#" class="last" data-action="last">&raquo;</a>
	</div>
	<#if list??>
	<table>
        <thead>
            <th></th>
            <th>商品编号</th>
            <th>颜色</th>
            <th>颜色编号</th>
            <th>尺码</th>
            <th>数量</th>
            <th>操作</th>
        </thead>
        <tbody>
            <#list list as sku>
        	  <tr>
                  <td></td>
                  <td>${sku.goods_id}</td>
                  <td>${sku.color}</td>
                  <td>${sku.color_id}</td>
                  <td>${sku.size}</td>
                  <td>${sku.manaual_lock_quantity}</td>
                  <td>
                      &nbsp;&nbsp;&nbsp;&nbsp;
                      <a href="${rc.contextPath}/goods/action/change_manaual_lock?id=${sku.id}&page=${paging.page}&goods_id=${goods_id!''}&action=unlock" style="cursor: pointer;">解锁</a>&nbsp;&nbsp;&nbsp;&nbsp;
                      <a href="${rc.contextPath}/goods/action/change_manaual_lock?id=${sku.id}&page=${paging.page}&goods_id=${goods_id!''}&action=refund" style="cursor: pointer;">出仓</a>
                  </td>
        	  </tr>
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
<#else>
        <div class="alert" style="margin: 5px;">
            <a class="close" data-dismiss="alert">×</a>
            <strong>无内容！</strong>
        </div>
</#if>
</@root.html>

<script>
	function initpage() {
	      $('#goods-id').val('${goods_id!""}');
	}
	
	$(document).ready(function(){
		  initpage();
	      $('.pagination').jqPagination({
			    paged: function(page) {
			    	var goodsId = $('#goods-id').val();
                    if(!page) {
                        page = 1;
                    }
			        window.location.href="/goods/action/lock_list?page=" + page + "&goods_id=" + goodsId;
			    }
		   });
		   
		   $('#search').click(function() {
		   		var goodsId = $('#goods-id').val();
		    	var page = $('#page').attr('data-current-page');
                if(!page) {
                    page = 1;
                }
		   		window.location.href="/goods/action/lock_list?page=" + page + "&goods_id=" + goodsId;
		   });
	});
</script>

