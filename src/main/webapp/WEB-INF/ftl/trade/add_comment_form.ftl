<#import "/templates/root.ftl" as root >
<#import "/trade/_buyer_info.ftl" as buyer_info >
<#import "/trade/_t_detail_tab.ftl" as detail_tab >
<@root.html active=3 css=["trade_detail.css"] js=["jquery.cookie.js"]>
    <@detail_tab.detail_tab current_tab="detail" />
    <div style="border:1px solid gray; width: 100%; height: auto;">
        <div class="right">
            请输入
            <#if type=="kefu_memo" || type=="cangku_memo">
                备注信息
            <#elseif type=="kefu_msg">
                给仓库的留言
            <#elseif type=="cangku_msg">
                给分销商的留言
            </#if>
            <form action="${rc.contextPath}/trade/action/add_comment" method="post">
                <textarea name="comment" rows="6" cols="50">
                    <#if type=="kefu_memo">
                        ${trade.kefu_memo!''}
                    <#elseif type=="cangku_memo">
                        ${trade.cangku_memo!''}
                    <#elseif type=="kefu_msg">
                        ${trade.kefu_msg!''}
                    <#elseif type=="cangku_msg">
                        ${trade.cangku_msg!''}
                    </#if>
                </textarea>
                <br>
                <input type="hidden" name="id" value="${trade.id}"/>
                <input type="hidden" name="type" value="${type}"/>
                <input type="hidden" name="from" value="${from!''}"/>
                <input type="submit" value="确定"/>
            </form>
        </div>
        <@buyer_info.buyer_info trade=trade conf=conf from=from!''/>
        <div style="clear: both;"></div>
    </div>

</@root.html>
