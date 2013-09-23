<#import "/templates/root.ftl" as root >
<@root.html active=3 css=["trade_detail.css"] js=["jquery.cookie.js"]>
    <#include "/trade/_t_detail_tab.ftl">
    <div style="border:1px solid gray; width: 100%; height: auto;">
        <div class="right">
            请输入取消原因
            <form action="${rc.contextPath}/trade/action/cancel_trade" method="post">
                <textarea name="why-cancel" rows="6" cols="50">

                </textarea>
                <br>
                <input type="hidden" name="id" value="${trade.id}"/>
                <input type="submit" value="取消订单"/>
            </form>
        </div>
        <#include "/trade/_buyer_info.ftl">
        <div style="clear: both;"></div>
    </div>

</@root.html>
