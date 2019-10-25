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
	setInterval(function(){getInboundMessages()}, 50000);
	setInterval(function(){getOutboundMessages()}, 50000);

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
    
    $('#inbounddataTable').DataTable().destroy();
     
     $('#inbounddataTable').DataTable({
	bServerSide: true,
	bProcessing: true, 
	deferRender: true,
	aaSorting: [[1,'desc']],
	sPaginationType: "bootstrap", 
	 drawCallback: function() {
	    $('[data-toggle="popover"]').popover();
	},
	oLanguage: {
	   sEmptyTable: "There were no files submitted for the selected date range.", 
	   sSearch: "Filter Results: ",
	   sLengthMenu: '<select class="form-control" style="width:150px">' +
		'<option value="10">10 Records</option>' +
		'<option value="20">20 Records</option>' +
		'<option value="30">30 Records</option>' +
		'<option value="40">40 Records</option>' +
		'<option value="50">50 Records</option>' +
		'<option value="-1">All</option>' +
		'</select>',
	    sProcessing: "<div style='background-color:#64A5D4; height:50px; margin-top:200px'><p style='color:white; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
	},
	sAjaxSource: "/administrator/processing-activity/dashboardInBoundBatches?fromDate="+fromDate+"&toDate="+toDate,
	createdRow: function(row, data, index) {
	    $(row).addClass('batchRow');
	    
	    if(data.uploadType === 'Watch List Entry') {
		$(row).addClass('table-primary');
	    }
	    else {
		$(row).attr('data-trigger', 'hover');
		$(row).attr('data-toggle', 'popover');
		$(row).attr('data-placement', 'top');
		$(row).attr('data-html', 'true');
		$(row).attr('title', 'File Status');


		if(data.statusId == 23 || data.statusId == 24) {
		    if(data.errorRecordCount == data.totalRecordCount) {
			$(row).addClass('table-danger');
			$(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
		    }
		    else if(data.errorRecordCount > 0) {
			var percent = (data.errorRecordCount * 100 / data.totalRecordCount);

			if(percent > data.threshold) {
			    $(row).addClass('table-danger');
			    $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
			}
			else {
			    $(row).addClass('table-warning');
			    $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Contains Errors</b>");
			}
		    }
		    else {
		       $(row).addClass("table-success");  
		       $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully</b>");
		    }
		}
		else if(data.statusId == 58 || data.statusId == 7|| data.statusId == 1 || data.statusId == 41 || data.statusId == 39 || data.statusId == 30 || data.statusId == 29) {
		    $(row).addClass('table-danger');
		    $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed to Process</b>");
		}
		else if(data.errorRecordCount > 0) {
		    var percent = (data.errorRecordCount * 100 / data.totalRecordCount);

		    if(percent > data.threshold) {
			$(row).addClass('table-danger');
			$(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
		    }
		}
	    }
	},
	aoColumns: [
	    {
		"mData": "uploadType", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "dateSubmitted", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text table-success",
		"render": function ( data, type, row, meta ) {
		    var dateC = new Date(data);
		    var minutes = dateC.getMinutes();
		    var hours = dateC.getHours();
		    var ampm =  hours >= 12 ? 'pm' : 'am';
		    hours = hours % 12;
		    hours = hours ? hours : 12;
		    minutes = minutes < 10 ? '0'+minutes : minutes;
		    var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
		    return myDateFormatted;
		}
	    },
	    {
		"mData": "uploadType", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    
		    if(data === 'Watch List Entry') {
			returnData = 'N/A';
		    }
		    else {
			returnData = '<a href="/administrator/processing-activity/inbound/'+row.utBatchName+'" class="dashboard-link" title="View Inbound Batch" role="button">'+row.utBatchName+'</a>';
		    }
		    return returnData;
		}
	    },
	    {
		"mData": "orgName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "configName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		   return data;
		}
	    },
	    {
		"mData": "transportMethod", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "uploadType", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    
		    if(data === 'Watch List Entry') {
			returnData = 'N/A';
		    }
		    else {
			returnData = commaSeparateNumber(row.totalRecordCount);
		    }
		    return returnData;
		}
	    },
	    {
		"mData": "uploadType", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    
		    if(data === 'Watch List Entry') {
			returnData = 'N/A';
		    }
		    else {
			returnData = commaSeparateNumber(row.errorRecordCount);
		    }
		    return returnData;
		}
	    },
	    {
		"mData": "uploadType", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    
		    if(data === 'Watch List Entry') {
			returnData = 'N/A';
		    }
		    else {
			returnData = commaSeparateNumber(row.threshold) + '%';
		    }
		    return returnData;
		}
	    }
	 ]
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
    
    $('#outbounddataTable').DataTable().destroy();
     
     $('#outbounddataTable').DataTable({
	bServerSide: true,
	bProcessing: true, 
	deferRender: true,
	aaSorting: [[1,'desc']],
	sPaginationType: "bootstrap", 
	 drawCallback: function() {
	    $('[data-toggle="popover"]').popover();
	},
	oLanguage: {
	   sEmptyTable: "There were no files sent out for the selected date range.", 
	   sSearch: "Filter Results: ",
	   sLengthMenu: '<select class="form-control" style="width:150px">' +
		'<option value="10">10 Records</option>' +
		'<option value="20">20 Records</option>' +
		'<option value="30">30 Records</option>' +
		'<option value="40">40 Records</option>' +
		'<option value="50">50 Records</option>' +
		'<option value="-1">All</option>' +
		'</select>',
	    sProcessing: "<div style='background-color:#64A5D4; height:50px; margin-top:200px'><p style='color:white; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
	},
	sAjaxSource: "/administrator/processing-activity/dashboardOutBoundBatches?fromDate="+fromDate+"&toDate="+toDate,
	createdRow: function(row, data, index) {
	    $(row).addClass('outboundbatchRow');
	    $(row).attr('data-trigger', 'hover');
	    $(row).attr('data-toggle', 'popover');
	    $(row).attr('data-placement', 'top');
	    $(row).attr('data-html', 'true');
	    $(row).attr('title', 'File Status');

	    if(data.statusId == 28) {
		if(data.errorRecordCount == data.totalRecordCount) {
		    $(row).addClass('table-danger');
		    $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed Threshold</b>");
		}
		else if(data.errorRecordCount > 0) {
		    $(row).addClass('table-warning');
		    $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Contains Errors</b>");
		}
		else {
		   $(row).addClass("table-success");  
		   $(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Processed Successfully</b>");
		}
	    }
	    else if(data.statusId == 58 || data.statusId == 7|| data.statusId == 1 || data.statusId == 41 || data.statusId == 39 || data.statusId == 30 || data.statusId == 29) {
		$(row).addClass('table-danger');
		$(row).attr('data-content', data.endUserDisplayText + "<br />" + "<b>File Failed to Process</b>");
	    }
	},
	aoColumns: [
	    {
		"mData": "id", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    return "On Demand";
		}
	    },
	    {
		"mData": "dateCreated", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text table-success",
		"render": function ( data, type, row, meta ) {
		    var dateC = new Date(data);
		    var minutes = dateC.getMinutes();
		    var hours = dateC.getHours();
		    var ampm =  hours >= 12 ? 'pm' : 'am';
		    hours = hours % 12;
		    hours = hours ? hours : 12;
		    minutes = minutes < 10 ? '0'+minutes : minutes;
		    var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
		    return myDateFormatted;
		}
	    },
	    {
		"mData": "utBatchName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"render": function ( data, type, row, meta ) {
		    return '<a href="/administrator/processing-activity/outbound/'+row.utBatchName+'" class="dashboard-link" title="View Outbound Batch" role="button">'+row.utBatchName+'</a>';
		}
	    },
	    {
		"mData": "orgName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "configName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		   return data;
		}
	    },
	    {
		"mData": "transportMethod", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "totalRecordCount", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    return commaSeparateNumber(data);
		}
	    },
	    {
		"mData": "errorRecordCount", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		     return commaSeparateNumber(data);
		}
	    },
	    {
		"mData": "threshold", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		   return commaSeparateNumber(data) + '%';
		}
	    }
	 ]
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


function commaSeparateNumber(val){
    while (/(\d+)(\d{3})/.test(val.toString())){
      val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
    }
    return val;
  }