$('.modify-delivery').click(function() {
    $(this).css("display", "none");
    $(this).next().css("display", "inline");
    $(this).next().next().css("display", "inline");
    $(this).prev().prev("span").css("display", "none");
    $(this).prev("select").css("display", "inline");
})

$('.modify-delivery-cancel').click(function() {
    $(this).css("display", "none");
    $(this).prev().css("display", "none");
    $(this).prev().prev().css("display", "inline");
    $(this).prev().prev().prev().prev("span").css("display", "inline");
    $(this).prev().prev().prev("select").css("display", "none");
})

$('.modify-delivery-submit').click(function() {
    var selected = $(this).prev().prev().val();
    var curr = $(this);
    $.ajax({
        type: "post",//使用get方法访问后台
        dataType: "json",//返回json格式的数据
        data: "delivery=" + selected + "&id=" + $(this).attr("data-id"),
        url: "/trade/action/change_delivery",//要访问的后台地址
        success: function(result){//msg为返回的数据，在这里做数据绑定
            if(result.success == false) {
                alert(result.errorInfo);
            } else {
                $(curr).css("display", "none");
                $(curr).next().css("display", "none");
                $(curr).prev().css("display", "inline");
                $(curr).prev().prev().css("display", "none");
                $(curr).prev().prev().prev().html(result.errorInfo);
                $(curr).prev().prev().prev().css("display", "inline");
            }
        }});
})