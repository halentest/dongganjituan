<#import "/templates/root.ftl" as root >
<@root.html css=["trade_list.css"] js=[] >
    <table>
        <tr>
            <th>发件人姓名</th>
            <th>始发地</th>
            <th>发件公司</th>
            <th>详细地址</th>
            <th>联系电话</th>
            <th></th>
        </tr>
        <tr>
            <td>${sellerInfo.sender!''}</td>
            <td>${sellerInfo.from_state!''}</td>
            <td>${sellerInfo.from_company!''}</td>
            <td>${sellerInfo.from_address!''}</td>
            <td>${sellerInfo.mobile!''}</td>
            <td><a href="${rc.contextPath}/admin/modify_seller_info_form">修改</a></td>
        </tr>
    </table>
</@root.html>
