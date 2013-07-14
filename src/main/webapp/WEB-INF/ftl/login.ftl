<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>用户登录</title>
    <LINK href="${rc.contextPath}/css/login/User_Login.css" type=text/css rel=stylesheet>
    <META http-equiv=Content-Type content="text/html; charset=utf-8">
</HEAD>
<BODY id=userlogin_body>
<form action="j_spring_security_check" method="POST">
    <DIV id=user_login>

        <DL>
            <DD id=user_top>
                <UL>
                    <LI class=user_top_l></LI>
                    <LI class=user_top_c></LI>
                    <LI class=user_top_r></LI>
                </UL>
            <DD id=user_main>
                <UL>

                    <LI class=user_main_l></LI>
                    <LI class=user_main_c>
                        <DIV class=user_main_box>
                            <UL>
                                <LI class=user_main_text>用户名： </LI>
                                <LI class=user_main_input>
                                    <INPUT class=TxtUserNameCssClass maxLength=20 name="j_username" style="width: 140px;">
                                </LI>
                            </UL>
                            <UL>
                                <LI class=user_main_text>密 码： </LI>
                                <LI class=user_main_input>
                                    <input class=TxtPasswordCssClass id="j_password" name="j_password" type="password" style="width: 140px;">
                                </LI>
                            </UL>
                        </DIV>
                    </LI>
                    <LI class=user_main_r>
                        <INPUT class="IbtnEnterCssClass" value=""
                               type="submit" src="${rc.contextPath}/css/login/user_botton.gif" /></LI></UL>
            <DD id=user_bottom>
                <UL>
                    <LI class=user_bottom_l></LI>
                    <LI class=user_bottom_c><SPAN style="MARGIN-TOP: 40px"></SPAN> </LI>
                    <LI class=user_bottom_r></LI>
                </UL>
            </DD>
        </DL>
    </DIV>
    <SPAN id=ValrUserName style="DISPLAY: none; COLOR: red"></SPAN>
    <SPAN id=ValrPassword style="DISPLAY: none; COLOR: red"></SPAN>
    <SPAN id=ValrValidateCode style="DISPLAY: none; COLOR: red"></SPAN>
    <DIV id=ValidationSummary1 style="DISPLAY: none; COLOR: red"></DIV>
    <DIV></DIV>
</form>
</BODY>
</HTML>
