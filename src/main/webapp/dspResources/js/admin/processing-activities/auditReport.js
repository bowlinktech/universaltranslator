/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {

    $("input:text,form").attr("autocomplete", "off");

    $(document).on('click', '.print', function() {
        var index = $(this).attr('rel');

        var divHeadingToPrint = $('.printHeading');
        var divToPrint=$('#errorTable-'+index);

        var newWin=window.open('','Print-Window');

        newWin.document.open();

        newWin.document.write('<html><body><div>'+divHeadingToPrint.html()+'</div><div>'+divToPrint.html()+'</div></body></html>');
        newWin.print();

        newWin.document.close();

        setTimeout(function(){newWin.close();},10);

     });

        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function () {
            $.ajax({
                url: '../../viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#statusModal").html(data);
                }
            });
        });
        
        $(document).on('click', '.droppedValueCollapse', function() {
	    var batchId = $(this).attr('rel');
	    var totalErrors = $(this).attr('total');
	    var type = $(this).attr('rel3');
	    
	    var isExpanded = $('#collapse-droppedValues').hasClass("expanded");
	    
	    if(isExpanded == false) {
		$('#collapse-droppedValues').addClass("expanded");
		$(".errorList-droppedValues").html("");
		$(".spinner-droppedValues").show();
		
		//Go get error records;
		$.ajax({
		    url: '../../loadDroppedValues.do',
		    data: {
			'batchId': batchId,
			'totalErrors': totalErrors,
			'type': type
		    },
		    type: "GET",
		    success: function (data) {
			$(".spinner-droppedValues").hide();
			$(".errorList-droppedValues").html(data);
		    }
		});
	    }
	    else {
		$('#collapse-droppedValues').removeClass("expanded");
	    } 
	});
	
	$(document).on('click', '.errorCollapse', function() {
	    var batchId = $(this).attr('rel');
	    var errorId = $(this).attr('error');
	    var indexVal = $(this).attr('rel2');
	    var totalErrors = $(this).attr('total');
	    var type = $(this).attr('rel3');
	    
	    var isExpanded = $('#collapse-'+indexVal).hasClass("expanded");
	    
	    if(isExpanded == false) {
		$('#collapse-'+indexVal).addClass("expanded");
		$(".errorList-"+indexVal).html("");
		$(".spinner-"+indexVal).show();
		
		//Go get error records;
		$.ajax({
		    url: '../../loadErrors.do',
		    data: {
			'batchId': batchId,
			'errorId': errorId,
			'totalErrors': totalErrors,
			'indexVal': indexVal,
			'type': type
		    },
		    type: "GET",
		    success: function (data) {
			$(".spinner-"+indexVal).hide();
                        
                         data = $(data);
                        
                        data.find('#errordatatable').dataTable({
                            "bStateSave": true,
                            "iCookieDuration": 60,
                            "sPaginationType": "bootstrap",
                             "bSort": false,
                             "pageLength": 100,
                             "dom": 't<"bottom"p><"clear">' ,
                            "oLanguage": {
                                "sSearch": "_INPUT_",
                                "sLengthMenu": '<select class="form-control" style="width:150px">' +
                                        '<option value="10">10 Records</option>' +
                                        '<option value="20">20 Records</option>' +
                                        '<option value="30">30 Records</option>' +
                                        '<option value="40">40 Records</option>' +
                                        '<option value="50">50 Records</option>' +
                                        '<option value="-1">All</option>' +
                                        '</select>'
                            }
                        });
                        
			$(".errorList-"+indexVal).html(data);
		    }
		});
	    }
	    else {
		$('#collapse-'+indexVal).removeClass("expanded");
	    }
	    
	});

    $(document).on('click', '.viewLink', function () {

        var transactionId = $(this).attr('rel');
        var configId = $(this).attr('rel2');

        $.ajax({
            url: '../../ViewMessageDetails',
            data: {'Type': 1, 'transactionId': transactionId, 'configId': configId},
            type: "GET",
            success: function (data) {
                $("#messageDetailsModal").html(data);
            }
        });

    });

    //This function will process the batch
    $(document).on('click', '.processBatch', function () {

        var confirmed = confirm("Are you sure you want to process the batch now?");

        if (confirmed) {
            // hide buttons
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../inboundBatchOptions',
                data: {'batchOption': $(this).attr('rel'), 'batchId': $(this).attr('rel2')},
                type: "POST",
                success: function (data) {
                    location.reload();
                }
            });
        }

    });

    //This function will cancel the batch
    $(document).on('click', '.cancelBatch', function () {

        var confirmed = confirm("Are you sure you want to set this batch to 'Cancel'?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../inboundBatchOptions',
                data: {
                    'batchOption': $(this).attr('rel'), 
                    'batchId': $(this).attr('rel2')
                },
                type: "POST",
                success: function (data) {
                     window.location.href = '/administrator/processing-activity/inbound';
                }
            });
        }

    });

    //This function will reset the batch
    $(document).on('click', '.resetBatch', function () {

        var confirmed = confirm("Are you sure you want to reset this batch?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../inboundBatchOptions',
                data: {
                    'batchOption': $(this).attr('rel'), 
                    'batchId': $(this).attr('rel2')
                },
                type: "POST",
                success: function (data) {
                    window.location.href = '/administrator/processing-activity/inbound';
                }
            });
        }

    });

    //This function will reset the outbound batch
    $(document).on('click', '.resetOutboundBatch', function () {

        var confirmed = confirm("Are you sure you want to reset this batch?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../outboundBatchOptions',
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

    //This function will reset the outbound batch
    $(document).on('click', '.cancelOutboundBatch', function () {

        var confirmed = confirm("Are you sure you want to set this batch to 'Cancel'?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../outboundBatchOptions',
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

    $(document).on('click', '.deleteTransactions', function() {

        var batchName = $(this).attr('rel');

        if(confirm("Are you sure you want to remove this inbound batch?")) {

            $('body').overlay({
                glyphicon : 'floppy-disk',
                message : 'Deleting...'
            });

            $.ajax({
                url: '../../deleteBatch.do',
                data: {
                    'batchName': batchName
                },
                type: 'POST',
                success: function(data) {
                   location.reload();
                }
            });

        }

    });

    //This function will release the batch
    $(document).on('click', '.releaseOutboundBatch', function () {

        var confirmed = confirm("Are you sure you want to release this outbound batch?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../outboundBatchOptions',
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

    //This function will release the batch
    $(document).on('click', '.releaseBatch', function () {

        var confirmed = confirm("Are you sure you want to release this batch?");

        if (confirmed) {
            $("#actionRowBottom").hide();
            $("#actionRowTop").hide();
            $.ajax({
                url: '../../inboundBatchOptions',
                data: {
                    'batchOption': $(this).attr('rel'), 
                    'batchId': $(this).attr('rel2')
                },
                type: "POST",
                success: function (data) {
                    window.location.href = '/administrator/processing-activity/inbound';
                }
            });
        }
    });

    //This function will release the batch
    $(document).on('click', '.rejectMessage', function () {

        var confirmed = confirm("Are you sure you want to reject this transaction?");

        if (confirmed) {
            $.ajax({
                url: '../../inboundBatchOptions',
                data: {'batchOption': $(this).attr('rel'), 'batchId': $(this).attr('rel2'), 'tId': $(this).attr('rel3')},
                type: "POST",
                success: function (data) {
                    window.location.href = 'inbound';
                }
            });
        }
    });

    //this function will submit the transactionInId to the ERG form for edit
    $(document).on('click', '.fixErrors', function () {
        $('input[name="transactionInId"]').val($(this).attr('rel'));
        $('#editTransaction').submit();
    });
});
