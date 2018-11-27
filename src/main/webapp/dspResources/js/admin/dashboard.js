/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function ($) {
	
	getGenericMessages();
	getInboundMessages();
	getOutboundMessages();
	setInterval(function(){getGenericMessages()}, 50000);
	setInterval(function(){getInboundMessages()}, 30000);
	setInterval(function(){getOutboundMessages()}, 45000);

        $("input:text,form").attr("autocomplete", "off");
	
	$('.date-range-picker-trigger').daterangepicker(
            {
                ranges: {
                    'See All': [$('#fromDate').attr('rel2'), moment()],
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
                    'Last 7 Days': [moment().subtract('days', 6), moment()],
                    'Last 30 Days': [moment().subtract('days', 29), moment()],
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
                },
                startDate: $('#fromDate').attr('rel'),
                endDate: $('#toDate').attr('rel')
            },
            function (start, end) {
                $('.daterange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                $('.daterange span').attr('rel', start.format('MM/DD/YYYY'));
                $('.daterange span').attr('rel2', end.format('MM/DD/YYYY'));
		getGenericMessages();
		getInboundMessages();
		getOutboundMessages();
            }
	);

    });
});

function getGenericMessages() {
  
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');
    
    if(fromDate == null) {
	fromDate = $('#fromDate').attr('rel');
    }
    
    if(toDate == null) {
	toDate = $('#toDate').attr('rel');
    }
    
    $.ajax({
        url: '/administrator/processing-activity/dashboardGenericBatches',
        data: {
	    'fromDate': fromDate, 
	    'toDate': toDate
	},
        type: "POST",
        success: function(data) {
	    
	   data = $(data);
	   
           $('.genericMessages').html(data);
        }
    });
}

function getInboundMessages() {
  
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');
    
    if(fromDate == null) {
	fromDate = $('#fromDate').attr('rel');
    }
    
    if(toDate == null) {
	toDate = $('#toDate').attr('rel');
    }
    
    $.ajax({
        url: '/administrator/processing-activity/dashboardInBoundBatches',
        data: {
	    'fromDate': fromDate, 
	    'toDate': toDate
	},
        type: "POST",
        success: function(data) {
	    
	   data = $(data);
	   
           $('.inboundMessages').html(data);
        }
    });
}

function getOutboundMessages() {
  
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');
    
    if(fromDate == null) {
	fromDate = $('#fromDate').attr('rel');
    }
    
    if(toDate == null) {
	toDate = $('#toDate').attr('rel');
    }
    
    $.ajax({
        url: '/administrator/processing-activity/dashboardOutBoundBatches',
        data: {
	    'fromDate': fromDate, 
	    'toDate': toDate
	},
        type: "POST",
        success: function(data) {
	    
	   data = $(data);
	   
           $('.outboundMessages').html(data);
        }
    });
}


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}