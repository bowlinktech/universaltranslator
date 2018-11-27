/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function () {
    require(['jquery'], function($) {

        $("input:text,form").attr("autocomplete", "off");
        
        populateNumbers();
        
        var aoColumnsSentMessages = [
           null,
           null,
           null,
           { "bSortable" : false}
        ];
        
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
           "aaSorting" : [[2, "desc"]]
        });
        
        
         var aoColumnsReceivedMessages = [
           null,
           null,
           null,
           null,
           null,
           { "bSortable" : false}
        ];
        
        $('#dtReceivedMessages').dataTable({
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
           "aoColumns" : aoColumnsReceivedMessages,
           "aaSorting" : [[2, "desc"]]
        });
        
        $(document).on('change', '#organization', function() {
           populateNumbers(); 
        });
        
        
        $(document).on('click', '.viewLink', function() {
            var selRow = $(this).attr('rel');
            $('#selRow').val(selRow);
            
             $('body .overlay').toggle();
            
             $('body').overlay({
                glyphicon : 'search',
                message : 'Retrieving...'
              });
                    
             setTimeout( function () { 
                 $("#viewSentMessagesForm").submit();
                 $('body .overlay').toggle();
              }, 300);   
            
        });
        
        $(document).on('click', '.viewAllSentLink', function() {
            $('#selRow').val(-1);
            
             $('body .overlay').toggle();
            
              $('body').overlay({
                glyphicon : 'search',
                message : 'Retrieving...'
              });
                    
             setTimeout( function () { 
                 $("#viewSentMessagesForm").submit();
                 $('body .overlay').toggle();
              }, 300);   
            
        });
        
        $(document).on('click', '.viewReceivedLink', function() {
            var selRow = $(this).attr('rel');
            $('#selReceivedRow').val(selRow);
            
             $('body .overlay').toggle();
            
             $('body').overlay({
                glyphicon : 'search',
                message : 'Retrieving...'
              });
                    
             setTimeout( function () { 
                 $("#viewReceivedMessagesForm").submit();
                 $('body .overlay').toggle();
              }, 300);   
            
        });
        
        $(document).on('click', '.viewAllReceivedLink', function() {
            $('#selReceivedRow').val(-1);
            
             $('body .overlay').toggle();
            
              $('body').overlay({
                glyphicon : 'search',
                message : 'Retrieving...'
              });
                    
             setTimeout( function () { 
                 $("#viewReceivedMessagesForm").submit();
                 $('body .overlay').toggle();
              }, 300);   
            
        });
        
        $(document).on('click', '.viewAllRejectedLink', function() {
            $('#fromRejectedDate').val($('#fromDate').val());
            $('#toRejectedDate').val($('#toDate').val());
            $('#selOrg').val($('#organization').val());
            
             $('body .overlay').toggle();
            
              $('body').overlay({
                glyphicon : 'search',
                message : 'Retrieving...'
              });
                    
             setTimeout( function () { 
                 $("#viewRejectedMessagesForm").submit();
                 $('body .overlay').toggle();
              }, 300);   
            
        });
        
    });
    
});


function searchByDateRange() {
   var fromDate = $('.daterange span').attr('rel');
   var toDate = $('.daterange span').attr('rel2');
   
   $('#fromDate').val(fromDate);
   $('#toDate').val(toDate);
   populateNumbers();

}

function populateNumbers() {
  
   var fromDate = $('#fromDate').val();
   var toDate = $('#toDate').val();
   var selOrg = $('#organization').val();
   
    $.ajax({
        async: false,
        url: '/frontend/activityReport/getSentMessages.do',
        data: {'fromDate': fromDate, 'toDate': toDate, 'selOrg': selOrg},
        type: "GET",
        success: function(data) {
           $('#sentMessages').html(data);
        }
    });
    
    $.ajax({
        async: false,
        url: '/frontend/activityReport/getReceivedMessages.do',
        data: {'fromDate': fromDate, 'toDate': toDate, 'selOrg': selOrg},
        type: "GET",
        success: function(data) {
           $('#receivedMessages').html(data);
        }
    });
    
    var totalSent = 0;
    /* Loop through all sent totals */
    $('.sentMessageTotal').each(function() {
        totalSent += $(this).html()*1;
    });
    
    $('#totalSent').html(totalSent);
    
    var totalRejected = 0;
    $('.rejectedMessageTotal').each(function() {
       totalRejected += $(this).html()*1;     
    });
    
    $('#totalRejected').html(totalRejected);
    
    var totalReceived = 0;
    /* Loop through all received totals */
    $('.receivedMessageTotal').each(function() {
        totalReceived += $(this).html()*1;
    });
    
    $('#totalReceived').html(totalReceived);
    
}

