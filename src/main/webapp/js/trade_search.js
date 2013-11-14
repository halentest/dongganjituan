$(document).ready(function() {
    $('#search').click(function() {
        var criteria = $('#criteria').val();
        var criteria_type = $('#criteria_type').val();
        var page = $('#page').val();
        if(typeof(page) == "undefined") {
            page = 1;
        }
        window.location.href = "/trade/trade_search?criteria=" + criteria + "&criteria_type=" + criteria_type;
    })

    $('.pagination').jqPagination({
        paged: function(page) {
            var criteria = $('#criteria').val();
            var criteria_type = $('#criteria_type').val();
            if(typeof(page) == "undefined") {
                page = 1;
            }
            window.location.href= "/trade/trade_search?criteria=" + criteria + "&criteria_type=" + criteria_type + "&page=" + page;
        }
   });
})