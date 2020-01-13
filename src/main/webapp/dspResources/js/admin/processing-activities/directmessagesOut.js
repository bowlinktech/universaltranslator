/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");
	
        //Function to display the details of the selected batch received from a direct HISP
	$(document).on('click', '.viewDirectDetails', function () {
            $.ajax({
                url: '/administrator/processing-activity/viewDirectDetailsOutById' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#payloadModal").html(data);
                }
            });
        });
	
	$(document).ready(function() {
	   
	    var fromDate = $('#fromDate').attr('rel');
	    var toDate = $('#toDate').attr('rel');
	    
	    populateMessages(fromDate,toDate);
	    
	});
    });
});

function populateMessages(fromDate,toDate) {
    
    var batchName = $('#batchName').val();
    
    $('#directmessagesout-table').DataTable().destroy();
     
     $('#directmessagesout-table').DataTable({
	bServerSide: true,
	bProcessing: false, 
	deferRender: true,
	aaSorting: [[0,'desc']],
	sPaginationType: "bootstrap", 
	oLanguage: {
	   sEmptyTable: "There were no sent direct messages for the selected date range.", 
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
	sAjaxSource: "/administrator/processing-activity/ajax/getDirectMessagesOut?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
	aoColumns: [
	    {
		"mData": "id", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    return data;
		}
	    },
	    {
		"mData": "orgName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "20%",
		"render": function ( data, type, row, meta ) {
		   return data;
		}
	    },
	    {
		"mData": "batchDownloadId", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    if(data > 0) {
			return '<a href="/administrator/processing-activity/outbound/' + row.batchName + '" title="View Outbound Batch">' + row.batchName + '</a>';
		    }
		    else {
			return "N/A";
		    }
		}
	    },
	    {
		"mData": "statusName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		   return data;
		}
	    },
	    {
		"mData": "dateCreated", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"className": "center-text",
		"render": function ( data, type, row, meta ) {
		    var dateC = new Date(row.dateCreated);
		    var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear();
		    return myDateFormatted;
		}
	    },
	    {
		"mData": "orgName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text actions-col",
		"render": function ( data, type, row, meta ) {
		   return '<a href="#payloadModal" data-toggle="modal" class="viewDirectDetails" rel="'+row.id+'" title="View Direct Message Details"><span class="glyphicon glyphicon-edit"></span> View Details</a>';
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

    populateMessages(fromDate,toDate);

}
