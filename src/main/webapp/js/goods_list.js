$('#change-template').click(function() {
    var checked = $('.wait-check:checked');
    if(checked.length==0) {
        alert('至少选中一个商品!');
        return false;
    }
    var hids = "";
    $(checked).each(function(index, item) {
        var hid = $(item).attr("data-hid");
        hids += hid;
        hids += ";";
    })
    $('#hids').val(hids);
    $('#pop-up').window('open');
})

function saveTemplate() {
  var template = $('#template').val();
  var hids = $('#hids').val();
  $.ajax({
    type: "post",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    data: "hids=" + hids + "&template=" + template + '&action=change-template',
    url: "/goods/action/batch_change",//要访问的后台地址
    success: function(result){//msg为返回的数据，在这里做数据绑定
        if(result.errorInfo != "success") {
            alert(result.errorInfo);
        } else {
            window.location.reload();
        }
    }});
}

function cancelTemplate() {
    $('#pop-up').window('close');
}