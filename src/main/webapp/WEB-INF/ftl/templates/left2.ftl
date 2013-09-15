
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
        height: 25px;
        FONT: 12px 宋体;
        padding-left: 15px;
        cursor: pointer;
        }
        IMG {
        BORDER-RIGHT: 0px; BORDER-TOP: 0px; VERTICAL-ALIGN: bottom; BORDER-LEFT: 0px; BORDER-BOTTOM: 0px
        }
        A {
        FONT: 12px 宋体; COLOR: #000000; TEXT-DECORATION: none
        cursor: pointer;
        }
        A:hover {
        COLOR: #428eff; TEXT-DECORATION: none;
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
                    background=${rc.contextPath}/img/frame/admin_left_2.gif height=25><SPAN>商品管理</SPAN> </TD>
            </TR>
            <TR>
                <TD id=submenu2><DIV class=sec_menu style="WIDTH: 158px">
                    <TABLE cellSpacing=0 cellPadding=0 width=135 align=center>
                        <TBODY>
                        <TR>
                            <TD class="active" height=20><a id="goods-list" target="mainFrame">商品列表</a></TD>
                        </TR>
                        <#if CURRENT_USER.type=="GoodsManager" || CURRENT_USER.type=="Admin" || CURRENT_USER.type=="SuperAdmin">
                        <TR>
                            <TD height=20><a id="lock-list" target="mainFrame">锁定列表</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="buy" target="mainFrame">进仓(excel批量导入)</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="refund" target="mainFrame">退仓(excel批量导入)</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="lock" target="mainFrame">锁定(excel批量导入)</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="new" target="mainFrame">新建商品(批量导入)</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="buy-list" target="mainFrame">进仓单列表</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="refund-list" target="mainFrame">退仓单列表</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="lock-invoice-list" target="mainFrame">锁定单列表</a></TD>
                        </TR>
                        <TR>
                            <TD height=20><a id="new-list" target="mainFrame">新建商品单列表</a></TD>
                        </TR>
                        </#if>
                        </TBODY>
                    </TABLE>
                </DIV>
                    </TD>
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

    $('#goods-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/goods_list";
    });

    $('#lock-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/action/lock_list";
    });

    $('#buy').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/action/upload?action=buy";
    });

    $('#refund').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/action/upload?action=refund";
    });

    $('#lock').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/action/upload?action=lock";
    });

    $('#new').click(function() {
    parent.frames[2].location.href="${rc.contextPath}/goods/action/upload?action=new";
    });

    $('#buy-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/upload_list?action=buy";
    });

    $('#refund-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/upload_list?action=refund";
    });

    $('#lock-invoice-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/upload_list?action=lock";
    });

    $('#new-list').click(function() {
        parent.frames[2].location.href="${rc.contextPath}/goods/upload_list?action=new";
    });
    })
</script>