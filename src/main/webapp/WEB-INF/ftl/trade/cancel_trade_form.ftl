<#import "/templates/root.ftl" as root >
<#import "/trade/_buyer_info.ftl" as buyer_info >
<#import "/trade/_t_detail_tab.ftl" as detail_tab >
<@root.html active=3 css=["trade_detail.css"] js=["jquery.cookie.js"]>
    <@detail_tab.detail_tab current_tab="cancel" />
    <div style="border:1px solid gray; width: 100%; height: auto;">
        <div class="right">
            请输入取消原因
            <form action="${rc.contextPath}/trade/action/cancel_trade" method="post">
                <textarea name="why-cancel" rows="6" cols="50">

                </textarea>
                <br>
                <input type="hidden" name="id" value="${trade.id}"/>
                <input type="hidden" name="isApply" value="${isApply!''}"/>
                <input type="submit" value="取消订单"/>
            </form>
        </div>
        <@buyer_info.buyer_info trade=trade/>
        <div style="clear: both;"></div>
    </div>

</@root.html>
