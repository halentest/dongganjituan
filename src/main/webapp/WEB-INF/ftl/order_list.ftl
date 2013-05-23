<#import "/templates/root.ftl" as root >

    <@root.html active=3 css=["jdpicker.css"] js=["highcharts.js", "exporting.js"]>
    <!-- start 订单列表展示-->
    <table class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>订单编号</th>
            <th>收货人</th>
            <th>电话</th>
            <th>地址</th>
            <th>邮编</th>
            <th>总重(千克)</th>
            <th>快递</th>
            <th>快递费</th>
            <th>货款(元)</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <#list orderList as order>
            <tr>
                <td>${order.order_id?c}</td>
                <td>${order.name}</td>
                <td>${order.phone}</td>
                <td>${order.address}</td>
                <td>${order.postcode}</td>
                <td>${order.total_weight/1000}</td>
                <td>${order.delivery}</td>
                <td>${order.deliveryMoney/100}</td>
                <td>${order.huokuan/100}</td>
                <td>${order.status}</td>
                <td>${order.created?string('yyyy-MM-dd HH:mm:ss')}</td>
                <td>
                    <a href="${rc.contextPath}/fenxiao/order_detail?order_id=${order.order_id?c}">详情</a>
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
    <!-- end 订单列表展示-->


</@root.html>

<script type="text/javascript">

</script>
