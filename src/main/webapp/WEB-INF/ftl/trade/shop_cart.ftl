<#import "/templates/root.ftl" as root >
<@root.html active=3 css=["jdpicker.css", "trade_list.css"] js=["highcharts.js", "exporting.js", "jquery.cookie.js"]>

    <i class="icon-shopping-cart"></i>我的购物车
    <div id="cart-container">
    </div>
    <table>
    </table>
    <div id="button-container" style="text-align: right;"></div>
</@root.html>

<script type="text/javascript">
    //加载快递列表
    $(document).ready(function(){
         var ordersStr = $.cookie('orders');
         if(ordersStr == null) {
         	$('#cart-container').append('<p/><div class="alert fade in">哇哦！您的购物车空空如也哦，赶紧去选购吧！<a href="${rc.contextPath}/goods/goods_list">去选购</a></div>');
         } else {
         	 var orderArr = ordersStr.split(":::");
         	 //$('#cart-container').append("<table>");
	         $(orderArr).each(function(index, item) {
	         	var itemArr = item.split(",");
	         	if(itemArr.length == 6) {
	         		$('table').append("<tr>"
	         						      + "<td><img src='" + itemArr[1] + "_80x80.jpg'/></td>"
	         						      + "<td>" + itemArr[0] + "</td>"
	         						      + "<td>" + itemArr[2] + "</td>"
	         						      + "<td><p>颜色：" + itemArr[3] + "</p><p>尺码：" + itemArr[4] + "</p></td>"
	         						      + "<td><p>数量：" + itemArr[5] + "</p></td>"
	         				       + "</tr>");
	         	}
	         })
	         //$('#cart-container').append("</table>");
	         $('table').after('<p/>');
			 $('#button-container').append('<a style="cursor: pointer;" id="clear-cart">清空购物车</a>&nbsp;&nbsp;');
			 $('#button-container').append('<a href="${rc.contextPath}/goods/goods_list">继续选购</a>&nbsp;&nbsp;');
			 $('#button-container').append('<a style="cursor: pointer;" id="check-out">结算</a>');
         }
         
         $('#clear-cart').click(function() {
         	$.cookie("orders", null, {path: '/'});
         	window.location.reload();
         })
         
         $('#check-out').click(function() {
         	window.location.href = "${rc.contextPath}/trade/action/buy_goods_form?orders=" + $.cookie('orders');
         })
    })
</script>
