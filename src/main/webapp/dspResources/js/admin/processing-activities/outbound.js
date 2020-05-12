/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {

    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the status detail overlay with the selected
    //status
    $(document).on('click', '.viewStatus', function () {
        $.ajax({
            url: '/administrator/processing-activity/viewStatus' + $(this).attr('rel'),
            type: "GET",
            success: function (data) {
                $("#statusModal").html(data);
            }
        });
    });

    $(document).ready(function() {

        var fromDate = $('#fromDate').attr('rel');
        var toDate = $('#toDate').attr('rel');

        populateMessages(fromDate,toDate);

    });

    //This function will release the batch
    $(document).on('click', '.releaseOutboundBatch', function () {

        var confirmed = confirm("Are you sure you want to release this outbound batch?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '/administrator/processing-activity/outboundBatchOptions',
                data: {
                    'batchOption': $(this).attr('rel'), 
                    'batchId': $(this).attr('rel2')
                },
                type: "POST",
                success: function (data) {
                    window.location.href = '/administrator/processing-activity/outbound';
                }
            });
        }
    });
});

function populateMessages(fromDate,toDate) {
    
    var batchName = $('#batchName').val();
    
    var userRole = $('#userRole').val();
    
     var searchTerm = $('#batchdownloads-table').attr('term');
    
    $('#batchdownloads-table').DataTable().destroy();
     
     $('#batchdownloads-table').DataTable({
	bServerSide: true,
	bProcessing: true, 
	deferRender: true,
	aaSorting: [[6,'desc']],
        "oSearch": {"sSearch": searchTerm },
	sPaginationType: "bootstrap", 
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
	sAjaxSource: "/administrator/processing-activity/ajax/getBatchDownloads?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
	aoColumns: [
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
		    var returnData = '';
		    if(data !== '') {
			returnData = '<strong>'+data+'</strong><br />';
		    }
		    else {
			returnData = '<strong>Invalid File</strong><br />';
		    }
		    returnData += row.utBatchName;
		    
		    if(row.outputFileName !== '' && (row.statusId == 28 || row.statusId == 58 || row.statusId == 30)) {
			returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=outbound&filename='+row.outputFileName+'&utBatchName='+row.utBatchName+'&foldername=archivesOut&orgId='+row.orgId+'" title="View Original File">Download Outbound File</a>';
		    }
		    
		   return returnData;
		}
	    },
	    {
		"mData": "fromBatchName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
                    var returnData = row.srcOrgName + '</br>';
		    returnData += '<a href="/administrator/processing-activity/inbound/'+data+'" title="View Inbound Batch" role="button">'+data+'</a>';
		    
		    if(row.fromBatchFile !== '') {
			returnData += '<br /><a href="/FileDownload/downloadFile.do?fromPage=outbound&filename='+row.fromBatchFile+'&foldername=archivesIn&orgId=0" title="View Uploaded Source File">Download Uploaded File</a>';
		    }
		    
		   return returnData;
		}
	    },
	    {
		"mData": "transportMethod", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "12%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var returnData = '';
		    
		    if(data === 'File Upload') {
			returnData = 'File Download';
		    }
		    else if(row.transportMethodId == 6) {
			returnData = '<a href="/administrator/processing-activity/wsmessageOut/'+row.utBatchName+'" title="View Web Services Status">'+data+'</a>';
		    }
		    else if(row.transportMethodId == 9) {
			returnData = '<a href="/administrator/processing-activity/apimessagesOut/'+row.utBatchName+'" title="View Rest API Message">'+data+'</a>';
		    }
		    else {
			return data;
		    }
		}
	    },
	    {
		"mData": "statusValue", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		   return '<a href="#statusModal" data-toggle="modal" class="viewStatus" rel="'+row.statusId+'" title="View this Status">'+data+'</a>';
		}
	    },
	    {
		"mData": "totalRecordCount", 
		"defaultContent": "",
		"bSortable":false,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    var returnData = 'Total Transactions: <strong>';
		    returnData += commaSeparateNumber(data) + '</strong><br />';
		    returnData += 'Total Errors: <strong>'+ commaSeparateNumber(row.totalErrorCount) + '</strong>';
		   return returnData;
		}
	    },
	    {
		"mData": "dateCreated", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "18%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var dateC = new Date(data);
		    var minutes = dateC.getMinutes();
		    var hours = dateC.getHours();
		    var ampm =  hours >= 12 ? 'pm' : 'am';
		    hours = hours % 12;
		    hours = hours ? hours : 12;
		    minutes = minutes < 10 ? '0'+minutes : minutes;
		    var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                    
                    if(row.startDateTime != null) {
                        dateC = new Date(row.startDateTime);
                        minutes = dateC.getMinutes();
                        hours = dateC.getHours()-1;
                        ampm =  hours >= 12 ? 'pm' : 'am';
                        hours = hours % 12;
                        hours = hours ? hours : 12;
                        minutes = minutes < 10 ? '0'+minutes : minutes;
                        
                        myDateFormatted += '<br />Start: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                    }
                    
                    if(row.endDateTime != null) {
                        dateC = new Date(row.endDateTime);
                        minutes = dateC.getMinutes();
                        hours = dateC.getHours()-1;
                        ampm =  hours >= 12 ? 'pm' : 'am';
                        hours = hours % 12;
                        hours = hours ? hours : 12;
                        minutes = minutes < 10 ? '0'+minutes : minutes;
                        
                        myDateFormatted += '<br />End: ' + ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear() + ' ' + hours+':'+minutes+ ' ' + ampm;
                    }
                    
		    return myDateFormatted;
		}
	    },
	    {
		"mData": "transportMethodId", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "5%",
		"className": "center-text actions-col",
		"render": function ( data, type, row, meta ) {
		   var returnData = '<div class="dropdown pull-left"><button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown"><i class="fa fa-cog"></i></button><ul class="dropdown-menu pull-right">';
		   
		   if(data != 2) {
		       returnData += '<li><a href="/administrator/processing-activity/outbound/batchActivities/'+row.utBatchName+'" class="viewBatchActivities" title="View Batch Activities"><span class="glyphicon glyphicon-edit"></span>View Batch Activities</a></li>';
		       returnData += '<li><a href="/administrator/processing-activity/outbound/auditReport/'+row.utBatchName+'" title="View Audit Report"><span class="glyphicon glyphicon-edit"></span> View Audit Report</a></li>';
		   }
		   
		   if(row.statusId == 64 || row.statusId == 59) {
		       returnData += '<li><a href="#!" id="release" class="releaseOutboundBatch" rel="releaseBatch" rel2="'+row.id+'"><span class="glyphicon glyphicon-ok-sign"></span> Process Now</a></li>';
		   }
		   
		   return returnData;
		}
	    }
	 ]
    });   
}

function commaSeparateNumber(val){
    while (/(\d+)(\d{3})/.test(val.toString())){
      val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
    }
    return val;
 }

function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    populateMessages(fromDate,toDate);

}
