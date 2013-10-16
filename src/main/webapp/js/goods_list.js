function cancelSyncStore() {
    $('#pop-up-sync-store').window('close');
}

$('#sync-store').click(function() {
    var checked = $('.wait-check:checked');
    if(checked.length==0) {
        alert('至少选中一个商品!');
        return false;
    }
    $('#pop-up-sync-store').window('open');
})

function saveSyncStore() {

    var shops = $('#seller-nick').val();
    var hids = "";
    var checked = $('.wait-check:checked');
    $(checked).each(function(index, item) {
        var hid = $(item).attr("data-hid");
        hids += hid;
        hids += ";";
    })
    $.ajax({
    type: "post",//使用get方法访问后台
    dataType: "json",//返回json格式的数据
    data: "hids=" + hids + "&action=sync-store&shops=" + shops,
    url: "/goods/batch_change",//要访问的后台地址
    success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.errorInfo != "success") {
                alert(result.errorInfo);
            }  else {
                $('#pop-up-sync-store').window('close');
            }
    }});
}

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
    url: "/goods/batch_change",//要访问的后台地址
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

function addGoods(tid, goods) {
    var selected = $("[data-goods='" + goods + "'][is-selected='true']");
    if(selected.size()==0) {
        alert("请选择要购买的商品！");
        return;
    }
    var orders = ''
    selected.each(function(index, element) {
        orders += goods + ',' + $(this).attr("data-url") + ',' + $(this).attr("data-title")
             + ',' + $(this).attr("data-color") + ',' +
            $(this).attr("data-size") + ',1';
        if(index != selected.size()-1) {
            orders += ':::';
        }
    })
    window.location.href = "/trade/action/buy_goods_form?addGoods=true&orders=" + orders + "&tid=" + tid;
}