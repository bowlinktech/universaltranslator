/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
        
    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewSoapMessage', function(event) {
            var wsId = $(this).attr('rel'); 
        $.ajax({
            url: 'wsmessage/viewSoapMessage.do',
            type: "POST",
            data: {'wsId': wsId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

  //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewSoapResponse', function(event) {
            var wsId = $(this).attr('rel'); 
        $.ajax({
            url: 'wsmessage/viewSoapResponse.do',
            type: "POST",
            data: {'wsId': wsId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

    //This function will launch the soap message overlay with the select wsId 
    $(document).on('click', '.viewBtachDLDetail', function(event) {
            var batchId = $(this).attr('rel'); 
        $.ajax({
            url: 'viewBatchDLDetails.do',
            type: "POST",
            data: {'batchId': batchId},
            success: function(data) {
                $("#soapModal").html(data);
            }
        });
    });

  //This will change between inbound and outbound
    $(document).on('change', '#wsDirection', function(event) {
            window.location.href = "/administrator/processing-activity/invalidIn";  
  });

    var oSettings = datatable.fnSettings();

    datatable.fnSort( [ [4,'desc'] ] );
        
     
});


function searchByDateRange() {
   var fromDate = $('.daterange span').attr('rel');
   var toDate = $('.daterange span').attr('rel2');
    
   $('#fromDate').val(fromDate);
   $('#toDate').val(toDate);
   
   $('body').overlay({
        glyphicon : 'floppy-disk',
        message : 'Processing...'
    });
   
   $('#searchForm').submit();

}
