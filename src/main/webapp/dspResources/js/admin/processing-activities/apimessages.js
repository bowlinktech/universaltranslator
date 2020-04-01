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
                var fileName = "RestAPIPayload_"+wsId;
                var blob = new Blob([data], { type: 'text/plain;charset=utf-8;' });
                if (navigator.msSaveBlob) { // IE 10+
                    navigator.msSaveBlob(blob, filename);
                } else {
                    var link = document.createElement("a");
                    if (link.download !== undefined) { // feature detection
                        // Browsers that support HTML5 download attribute
                        var url = URL.createObjectURL(blob);
                        link.setAttribute("href", url);
                        link.setAttribute("download", fileName);
                        link.style.visibility = 'hidden';
                        document.body.appendChild(link);
                        link.click();
                        document.body.removeChild(link);
                    }
                }
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
    
    $('#apimessagesin-table').DataTable().destroy();
     
     $('#apimessagesin-table').DataTable({
	bServerSide: true,
	bProcessing: false, 
	deferRender: true,
	aaSorting: [[6,'desc']],
	sPaginationType: "bootstrap", 
	oLanguage: {
	   sEmptyTable: "There were no received rest api messages for the selected date range.", 
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
	sAjaxSource: "/administrator/processing-activity/ajax/getAPIMessagesIn?fromDate="+fromDate+"&toDate="+toDate+"&batchName="+batchName,
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
		"mData": "batchUploadId", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "15%",
		"render": function ( data, type, row, meta ) {
		    if(data > 0) {
			return '<a href="/administrator/processing-activity/inbound/' + row.batchName + '" title="View Inbound Batch">' + row.batchName + '</a>';
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
	    }/*,
	    {
		"mData": "orgName", 
		"defaultContent": "",
		"bSortable":true,
		"sWidth": "10%",
		"className": "center-text actions-col",
		"render": function ( data, type, row, meta ) {
		   return '<a href="#!" class="viewPayload" rel="'+row.id+'" title="View Payload"><span class="glyphicon glyphicon-edit"></span> View Payload</a>';
		}
	    }*/
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
