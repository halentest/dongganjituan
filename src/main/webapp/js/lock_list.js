function batchChangeManaualLock(action) {
    var checked = $('#lock-list').datagrid("getChecked");
    if(checked.length==0) {
        alert('请选择要操作的条目!');
        return false;
    }
    var ids = "";
    $(checked).each(function(index, item) {
        var id = item.sku_id.trim();
        ids += id;
        ids += ",";
    })
        $.ajax({
            type: "post",//使用get方法访问后台
            beforeSend: function() {
                $(this).attr('disabled',"true");
            },
            dataType: "json",//返回json格式的数据
            data: "ids=" + ids + "&action=" + action,
            url: "/goods/action/batch_change_manaual_lock",//要访问的后台地址
            success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
                window.location.reload();
            } else {
                window.location.reload();
            }
        }});
}