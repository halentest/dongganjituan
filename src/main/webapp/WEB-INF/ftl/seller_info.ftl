<#import "/templates/root.ftl" as root >
<@root.html css=[] js=[] >
    <p>发件人姓名：${sellerInfo.sender!''}</p>
    <p>始发地：${sellerInfo.from_state!''}</p>
    <p>发件公司：${sellerInfo.from_company!''}</p>
    <p>详细地址：${sellerInfo.from_address!''}</p>
    <p>联系电话：${sellerInfo.mobile!''}</p>
    <p><a class="btn btn-primary" href="${rc.contextPath}/admin/modify_seller_info_form">修改</a></p>
</@root.html>
