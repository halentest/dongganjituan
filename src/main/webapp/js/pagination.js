$(document).ready(function(){
		
	  initpage();
      $('.pagination').jqPagination({
		    paged: function(page) {
		    	var status = $('#status').val();
		    	var distributor = $('#distributor').val();
		    	if(!distributor) {
		    		distributor='';
		    	}
		    	var start = $('#start').val();
                var end = $('#end').val();
                if(start.length>0 && end.length>0) {
                     if(!strDateTime(start)) {
                        alert("请输入正确的开始时间");
                        return false;
                     }
                     if(!strDateTime(end)) {
                        alert("请输入正确的结束时间");
                        return false;
                     }
                } else {
                    start = "";
                    end = "";
                }
		    	var seller_nick = $('#seller_nick').val();
		    	var name = $('#name').val();
		    	var tid = $('#tid').val();
		    	var delivery = $('#delivery').val();
		    	if(!page) {
		    	    page = 1;
		    	}
		        window.location.href="/trade/trade_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&name=" + name + "&tid=" + tid + "&dId=" + distributor + "&delivery=" + delivery + "&start=" + start
                    + "&end=" + end;;
		    }
	   });
	   
});

 function strDateTime(str) {
    var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
    var r = str.match(reg);
    if(r==null)return false;
    var d= new Date(r[1], r[3]-1,r[4],r[5],r[6],r[7]);
    return (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]&&d.getHours()==r[5]&&d.getMinutes()==r[6]&&d.getSeconds()==r[7]);
  }

//$('.pagination').jqPagination({
//		
//		max_page	: 40,
//		paged		: function(page) {
//			$('.log').prepend('<li>Requested page ' + page + '</li>');
//		}
//	});
