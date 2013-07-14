
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML><HEAD>
    <STYLE type=text/css>BODY {
        BACKGROUND: #d6dff7; MARGIN: 0px; FONT: 9pt 宋体
        }
        TABLE {
        margin: 5px;
        BORDER-RIGHT: 0px; BORDER-TOP: 0px; BORDER-LEFT: 0px; BORDER-BOTTOM: 0px
        }
        TD {
        padding-left: 15px;
        height: 25px;
        FONT: 12px 宋体;
        }
        IMG {
        BORDER-RIGHT: 0px; BORDER-TOP: 0px; VERTICAL-ALIGN: bottom; BORDER-LEFT: 0px; BORDER-BOTTOM: 0px
        }
        A {
        FONT: 12px 宋体; COLOR: #000000; TEXT-DECORATION: none;
        cursor: pointer;
        }
        A:hover {
        COLOR: #428eff; TEXT-DECORATION: none
        }
        .sec_menu {
        BORDER-RIGHT: white 1px solid; BACKGROUND: #d6dff7; OVERFLOW: hidden; BORDER-LEFT: white 1px solid; BORDER-BOTTOM: white 1px solid
        }
        .menu_title {

        }
        .menu_title SPAN {
        FONT-WEIGHT: bold; LEFT: 7px; COLOR: #215dc6; POSITION: relative; TOP: 2px
        }
        .menu_title2 {

        }
        .menu_title2 SPAN {
        FONT-WEIGHT: bold; LEFT: 8px; COLOR: #428eff; POSITION: relative; TOP: 2px
        }
        td.active {
            background-color: white;
        }
    </STYLE>

    <META http-equiv=Content-Type content="text/html; charset=utf-8">
    <META content="MSHTML 6.00.2900.2180" name=GENERATOR></HEAD>
<BODY leftMargin=0 topMargin=0 marginwidth="0" marginheight="0">

        <TABLE cellSpacing=0 cellPadding=0 width=158 align=center style="margin-top: 15px;">
            <TBODY>
            <TR>
                <TD class=menu_title id=menuTitle1
                    onmouseover="this.className='menu_title2';" onclick=showsubmenu(2)
                    onmouseout="this.className='menu_title';"
                    background=${rc.contextPath}/img/frame/admin_left_2.gif height=25><SPAN>退货管理</SPAN> </TD>
            </TR>
            <TR>
                <TD id=submenu2><DIV class=sec_menu style="WIDTH: 158px">
                    <TABLE cellSpacing=0 cellPadding=0 width=135 align=center>
                        <TBODY>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin" || CURRENT_USER.type=="DistributorManager">
                            <tr>
                                <td height="20" class="active"><a id='ApplyRefund'>申请退货中</a></td>
                            </tr>
                        </#if>
                        <tr>
                            <td height="20" class=""><a id="Refunding">等待买家退货</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="ReceiveRefund">已收到退货</a></td>
                        </tr>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin">
                            <tr>
                                <td height="20" class=""><a id="CancelRefund">已取消退货</a></td>
                            </tr>
                        </#if>
                        <#if CURRENT_USER.type!="WareHouse">
                            <tr>
                                <td height="20" class=""><a id="RejectRefund">拒绝退货</a></td>
                            </tr>
                        </#if>
                        <tr>
                            <td height="20" class=""><a id="RefundSuccess">退货退款成功</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="Refund">退货成功但未退款</a></td>
                        </tr>
                        </TBODY>
                    </TABLE>
                </DIV>
                    <DIV style="WIDTH: 158px">
                        <TABLE cellSpacing=0 cellPadding=0 width=135 align=center>
                            <TBODY>
                            <TR>
                                <TD
                                        height=20></TD>
                            </TR>
                            </TBODY>
                        </TABLE>
                    </DIV></TD>
            </TR>
            </TBODY>
        </TABLE>
</BODY></HTML>
<script src="${rc.contextPath}/js/jquery-min.js"></script>
<script>
    $(document).ready(function() {
        $('a').click(function() {
            $('td').css("background-color", "#d6dff7");
            $(this).parent().css("background-color", "white");
        })

        $('#ApplyRefund').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=ApplyRefund";
        });

        $('#Refunding').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=Refunding";
        });

        $('#ReceiveRefund').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=ReceiveRefund";
        });

        $('#CancelRefund').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=CancelRefund";
        });

        $('#RejectRefund').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=RejectRefund";
        });

        $('#RefundSuccess').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=RefundSuccess";
        });

        $('#Refund').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/refund_list?status=Refund";
        });
        $('a').first().click();
    })
</script>