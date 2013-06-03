<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["jdpicker.css", "trade_list.css", "jqpagination.css"] js=["jquery.jqpagination.min.js", "highcharts.js", "exporting.js"]>
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
        	  		<th rowspan="${map2?size+1}" style="width: 12%;"><img style="width: 80px; height: 80px" src="${goods.url}" /></th>
        	  		<th rowspan="${map2?size+1}" style="width: 12%;">${goods.hid}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 20%;">${goods.title}</th>
        	  		<th rowspan="${map2?size+1}" style="width: 8%;">${goods.price/100}元</th>
        	  		<th>颜色</th>
        	  		<#list map2?keys as key2>
        	  			<#if key2_index==0>
        	  				<#list map2[key2]?keys as key3>
        	  					<th>${key3}</th>
        	  				</#list>
        	  			</#if>
        	  		</#list>
        	  </tr>
        	  <#list map2?keys as key2>
        	  <tr>
        	  		<td>${key2}</td>
        	  		<#list map2[key2]?keys as key3>
        	  		<td>${map2[key2][key3]}</td>
        	  		</#list>
        	  </tr>
        	  </#list>
	          
	        </#list>
        </tbody>
      </table>
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
			        window.location.href="/huopin/goods_list?page=" + page + "&goods_id=" + goodsId;
			    }
		   });
		   
		   $('#search').click(function() {
		   		var goodsId = $('#goods-id').val();
		    	var page = $('#page').attr('data-current-page');
		   		window.location.href="/huopin/goods_list?page=" + page + "&goods_id=" + goodsId;
		   });
	});
</script>

