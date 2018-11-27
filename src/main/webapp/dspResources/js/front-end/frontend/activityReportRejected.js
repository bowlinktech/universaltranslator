/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function () {
    require(['jquery'], function($) {
        
        $("input:text,form").attr("autocomplete", "off");
       
        var aoColumnsRejectedMessages = [
           null,
           null,
           null,
           null,
           { "bSortable" : false}
        ];
        
        $('#dtRejectedMessages').dataTable({
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
           "aoColumns" : aoColumnsRejectedMessages,
           "aaSorting" : [[3, "desc"]]
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
        
    });
});


