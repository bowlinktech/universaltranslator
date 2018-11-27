/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function () {
    require(['jquery'], function($) {
        
        $("input:text,form").attr("autocomplete", "off");
        
        var panelTitle = $('.panel-title').attr('rel');
        
        if(panelTitle.indexOf("Feedback Reports") >= 0) {
             var aoColumnsReceivedMessages = [
                null,
                null,
                null,
                null,
		null
             ];
        }
        else {
       
            var aoColumnsSentMessages = [
               null,
               null,
               null,
               null,
	       null
            ];
        }
        
        $('#dtSentMessages').dataTable({
            "sPaginationType": "bootstrap",
            "oLanguage": {
                "sSearch": "_INPUT_",
                "sLengthMenu": '<select class="form-control" style="width:150px">'+
                      '<option value="10">10 Records</option>'+
                      '<option value="20">20 Records</option>'+
                      '<option value="30">30 Records</option>'+
                      '<option value="40">40 Records</option>'+
                      '<option value="50">50 Records</option>'+
                      '<option value="-1">All</option>'+
                      '</select>'
            },
           "aoColumns" : aoColumnsSentMessages,
           "aaSorting" : [[4, "desc"]]
        });
        
        $(document).on('click', '.viewSent', function() {
           
            var transactionId = $(this).attr('rel');
            
            $.ajax({
                url: '/frontend/history/sent/messageDetails',
                data: {'transactionId': transactionId, 'fromPage': 'sent'},
                type: "POST",
                success: function(data) {
                    $("#messageDetailsModal").html(data);
                }
            });
            
        });
        
        //Function to handle the form actions
        $(document).on('click', '.print', function() {
            window.print();
        });
        
        //Function to handle the form actions
        $(document).on('click', '.printDetails', function() {
            $("#detailsToPrint").printElement();
        });
        
        $(document).on('click', '.goBack', function() {
             window.history.back();
        });
        
        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function() {
            $.ajax({
                url: '/frontend/viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function(data) {
                    $("#statusModal").html(data);
                }
            });
        });
        
	
	//this function will submit the batchId for viewing detailed audit report
        $(document).on('click', '.viewLink', function() {
	    $('input[name="batchId"]').val($(this).attr('rel'));
	    $('#searchForm').attr('action', '/frontend/auditReport');
	    $('#searchForm').submit();
        });
    });
});


