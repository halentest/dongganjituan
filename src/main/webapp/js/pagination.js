$(document).ready(function(){
		
	  initpage();
      $('.pagination').jqPagination({
		    paged: function(page) {
		    	var status = $('#status').val();
		    	var distributor = $('#distributor').val();
		    	if(!distributor) {
		    		distributor='';
		    	}
		    	var seller_nick = $('#seller_nick').val();
		    	var name = $('#name').val();
		        window.location.href="/trade/trade_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&name=" + name + "&dId=" + distributor;
		    }
	   });
	   
	   $('#search').click(function() {
	   		var status = $('#status').val();
	   		var distributor = $('#distributor').val();
	   		if(!distributor) {
	    		distributor='';
	    	}
	    	var seller_nick = $('#seller_nick').val();
	    	var name = $('#name').val();
	    	var page = $('#page').attr('data-current-page');
	   		window.location.href="/trade/trade_list?page=" + page + "&status=" + status + "&seller_nick=" + seller_nick
		        	+ "&name=" + name + "&dId=" + distributor;
	   });
});

//$('.pagination').jqPagination({
//		
//		max_page	: 40,
//		paged		: function(page) {
//			$('.log').prepend('<li>Requested page ' + page + '</li>');
//		}
//	});
