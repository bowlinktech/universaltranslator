/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function($) {
        
        $("input:text,form").attr("autocomplete", "off");
        
        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewPayload', function(event) {
        	var wsId = $(this).attr('rel'); 
            $.ajax({
            	url: 'wsmessage/viewPayload.do',
                type: "POST",
                data: {'wsId': wsId},
                success: function(data) {
                    $("#payloadModal").html(data);
                }
            });
        });
        
        
      //This will change between inbound and outbound
        $(document).on('change', '#wsDirection', function(event) {
        		window.location.href = "invalidOut";  
            
        });
        
        var oSettings = datatable.fnSettings();
        
        datatable.fnSort( [ [4,'desc'] ] );
        
   });
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
