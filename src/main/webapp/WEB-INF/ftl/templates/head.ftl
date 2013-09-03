
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
    <link rel="stylesheet" href="${rc.contextPath}/css/frame/css.css?t=1" type="text/css">
    <META http-equiv=Content-Type content="text/html; charset=utf-8">
    <META content="MSHTML 6.00.2900.2180" name=GENERATOR>
</HEAD>
<BODY bgColor=#d6dff7 leftMargin=0 topMargin=0 marginwidth="0" marginheight="0">
<TABLE cellSpacing=0 cellPadding=0 width="100%" align=center border=0>
    <TBODY>
        <TR>
            <TD class=txlHeaderBackgroundAlternate id=TableTitleLink vAlign=center
                width="43%" height=23>&nbsp;${CURRENT_USER.userType.getName()!''} ${CURRENT_USER.username!'无名氏'} 您好!欢迎来到骆驼动感ERP管理系统</TD>
            <TD class=txlHeaderBackgroundAlternate id=TableTitleLink vAlign=center
                width="21%"></TD>
            <TD class=txlHeaderBackgroundAlternate id=TableTitleLink vAlign=center
                align=right width="36%">
                <a href="/j_spring_security_logout" target="_parent">退出</a>&nbsp;&nbsp;
            </TD>
        </TR>
    </TBODY>
</TABLE>
<div class="my-nav">
    <div class="my-nav">
        <ul>
            <li>
                <button id="b1" class="active">订单管理</button>
            </li>
            <li>
                <button id="b5">退货管理</button>
            </li>
            <li>
                <button id="b2">库存管理</button>
            </li>
            <li>
                <button id="b3">财务管理</button>
            </li>
            <li>
                <button id="b4">系统管理</button>
            </li>
        </ul>
    </div>
</div>

</BODY>
</HTML>
<script src="${rc.contextPath}/js/jquery-min.js"></script>
<script type="text/javascript">
    $('button').click(function() {
        $('button').each(function(index, item) {
                $(item).css("background-color", "#d6dff7");
            }
        )
        $(this).css("background-color", "white");
    });

    $('#b1').click(function() {
        parent.frames[1].location.href="${rc.contextPath}/left_trade";
        //parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=-6";
    });

    $('#b2').click(function() {
        parent.frames[1].location.href="${rc.contextPath}/left2";
        parent.frames[2].location.href="${rc.contextPath}/goods/goods_list";
    });

    $('#b3').click(function() {
        parent.frames[1].location.href="${rc.contextPath}/left3";
        //parent.frames[2].location.href="${rc.contextPath}/accounting/distributor_list";
    });

    $('#b4').click(function() {
        parent.frames[1].location.href="${rc.contextPath}/left4";
        parent.frames[2].location.href="${rc.contextPath}/admin/template_list";
    });

    $('#b5').click(function() {
        parent.frames[1].location.href="${rc.contextPath}/left5";
        //parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=ApplyRefund";
    });

</script>