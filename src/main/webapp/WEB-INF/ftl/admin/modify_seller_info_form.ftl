<#import "/templates/root.ftl" as root >

<@root.html active=2 css=["table.css"] js=[]>
<br>
<form action="${rc.contextPath}/admin/modify_seller_info" method="">
    <fieldset>
        <legend>修改发货信息</legend>
        <p>发件人姓名：
            <input name="sender" class="my-input" type="text"/>
        </p>
        <p>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;省份：
            <input name="from_state" class="my-input" style="width: 10%;" type="text"/>
        </p>
        <p>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;城市：
            <input name="from_city" class="my-input" style="width: 10%;" type="text"/>
        </p>
        <p>
            &nbsp;&nbsp;&nbsp;发件公司：
            <input name="from_company" class="my-input" type="text"/>
        </p>
        <p>
            &nbsp;&nbsp;&nbsp;详细地址：
            <input name="from_address" class="my-input" type="text" style="width: 50%;"/>
        </P>
        <p>
            &nbsp;&nbsp;&nbsp;联系电话：
            <input name="mobile" class="my-input" type="text"/>
        </p>

        <div class="form-actions" >
            <input type="submit" class="btn btn-primary" value="保存更改" style="margin-left: 10%;">
            <input onclick="history.go(-1)" style="margin-left: 10px;" type="reset" class="btn btn-inverse" value="取消"/>
        </div>
    </fieldset>
</form>
</@root.html>