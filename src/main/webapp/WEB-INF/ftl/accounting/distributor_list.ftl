<#import "/templates/root.ftl" as root >
<@root.html css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js"] >
<i class="icon-list-alt"></i>分销商列表<br><br>
	<table>
		<thead>
			<tr>
				<th>姓名</th>
				<th>折扣</th>
				<th>是否自营</th>
				<th>余额(元)</th>
				<th>操作</th>
			</tr>	
		</thead>
		<#if dList??>
		<tbody style="text-align: center;">
			<#list dList as d>
			<tr>
				<td>${d.name}</td>
				<td>${d.discount}</td>
				<td><#if d.self==1>是<#else>否</#if></td>
				<td>${d.deposit/100}</td>
				<td>
					<a style="cursor: pointer;" data-name="${d.name}" data-id="${d.id}" class="recharge">打款</a>
				</td>
			</tr>
			</#list>
		</tbody>
		</#if>
	</table>
	<!-- start 提示框 -->
  <div class="modal hide" id="pop-up">
      <div class="modal-header">
        <a class="close" data-dismiss="modal">×</a>
        <h4 id="title"></h4>
      </div>
      <div class="modal-body">
	    <p>
	    	请输入金额 <input type="text" id="how-much"/>
	    	<input type="hidden" id="curr-id"/>
	    </p>
	  </div>
      <div class="modal-footer">
         <a href="#" class="btn" data-dismiss="modal">关闭</a>
    	 <a href="#" class="btn btn-primary" id="save">保存更新</a>
      </div>
  </div>
  <!-- end 提示框 -->
</@root.html>
<script type="text/javascript">
    //加载快递列表
    $(document).ready(function(){
    	$('.recharge').click(function() {
    		$('#title').val("为分销商 " + $(this).attr("data-name") + " 打款");
    		$('#curr-id').val($(this).attr("data-id"));
    		$('#pop-up').modal({
                keyboard: false
            })
    	})
    	
    	$('#save').click(function() {
	          $.ajax({
	            type: "post",//使用get方法访问后台
	            dataType: "json",//返回json格式的数据
	            data: "how_much=" + $('#how-much').val() + "&dId=" + $('#curr-id').val(),
	            url: "${rc.contextPath}/accounting/recharge",//要访问的后台地址
	            success: function(result){//msg为返回的数据，在这里做数据绑定
	                if(result.errorInfo != "success") {
	                	alert(result.errorInfo);
	                }
	                window.location.reload();
	            }}); 
    	})
    });
</script>
