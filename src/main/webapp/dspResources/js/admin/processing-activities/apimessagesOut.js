/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {

    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the status detail overlay with the selected
    //status
    $(document).on('click', '.viewPayload', function (event) {
        var wsId = $(this).attr('rel');
        $.ajax({
            url: '/administrator/processing-activity/apimessage/viewPayload.do',
            type: "POST",
            data: {'messageId': wsId},
            success: function (data) {
                $("#payloadModal").html(data);
            }
        });
    });

    //This function will launch the status detail overlay with the selected
    //status
    $(document).on('click', '.viewHeader', function (event) {
        var apiMessageId = $(this).attr('rel');
        $.ajax({
            url: '/administrator/processing-activity/apimessageOut/viewHeader.do',
            type: "POST",
            data: {'messageId': apiMessageId},
            success: function (data) {
                $("#HeaderModal").html(data);
            }
        });
    });

    $(document).ready(function() {

        var fromDate = $('#fromDate').attr('rel');
        var toDate = $('#toDate').attr('rel');

        populateMessages(fromDate,toDate);

    });
});

function populateMessages(fromDate,toDate) {
    
    var batchName = $('#batchName').val();
    
    $('#apimessagesout-table').DataTable().destroy();
     
     $('#apimessagesout-table').DataTable({
	bServerSide: true,
	bProcessing: false, 
	deferRender: true,
	aaSorting: [[6,'desc']],
	sPaginationType: "bootstrap", 
	oLanguage: {
	   sEmptyTable: "There were no sent rest api messages for the selected date range.", 
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
	sAjaxSource: "/administrator/processing-activity/ajax/getAPIMessagesOut?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
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
		"mData": "configId", 
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
		"mData": "errorDisplayText", 
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
		   return '<a href="#HeaderModal" data-toggle="modal" class="viewHeader" rel="'+row.id+'" title="View Returned Headers"><span class="glyphicon glyphicon-edit"></span> View Returned Header</a>';
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
