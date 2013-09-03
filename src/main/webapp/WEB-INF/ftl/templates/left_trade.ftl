
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
                    background=${rc.contextPath}/img/frame/admin_left_2.gif height=25><SPAN>订单管理</SPAN> </TD>
            </TR>
            <TR>
                <TD id=submenu2><DIV class=sec_menu style="WIDTH: 158px">
                    <TABLE cellSpacing=0 cellPadding=0 width=135 align=center>
                        <TBODY>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin">
                            <tr>
                                <td height="20" class="active"><a id='wait-handle'>库存不足订单</a></td>
                            </tr>
                        </#if>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin">
                            <tr>
                                <td height="20" class=""><a id="new">新建订单</a></td>
                            </tr>
                        </#if>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin" || CURRENT_USER.type=="DistributorManager">
                            <tr>
                                <td height="20" class=""><a id="wait-check">待审核订单</a></td>
                            </tr>
                        </#if>
                        <tr>
                            <td height="20" class=""><a id="wait-send">待发货订单</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="finding">待打印订单</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="have-delivery">已扫描订单</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="wait-receive">已发货订单</a></td>
                        </tr>
                        <tr>
                            <td height="20" class=""><a id="no-goods">无货订单</a></td>
                        </tr>
                        <#if CURRENT_USER.type=="ServiceStaff" || CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="Admin" ||
                                CURRENT_USER.type=="SuperAdmin">
                            <tr>
                                <td height="20" class=""><a id="cancel">已作废订单</a></td>
                            </tr>
                        </#if>
                        <#if CURRENT_USER.type=="Distributor" || CURRENT_USER.type=="ServiceStaff">
                        <TR>
                            <TD height=20><a href="${rc.contextPath}/trade/manual_sync_trade_form" target="mainFrame">手工同步订单</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a href="${rc.contextPath}/trade/action/upload" target="mainFrame">批量导入</a></TD>
                        </TR>
                        </#if>
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

        $('#wait-handle').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=-6";
        });

        $('#new').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=0";
        });

        $('#wait-check').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=1";
        });

        $('#wait-send').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=2";
        });

        $('#finding').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=3";
        });

        $('#have-delivery').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=6";
        });

        $('#wait-receive').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=4";
        });

        $('#no-goods').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=-5";
        });

        $('#cancel').click(function() {
            parent.frames[2].location.href="${rc.contextPath}/trade/trade_list?status=-1";
        });

        $('a').first().click();
    })
</script>