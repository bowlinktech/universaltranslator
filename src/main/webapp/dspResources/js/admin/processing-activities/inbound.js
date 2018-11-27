/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

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

        var oSettings = datatable.fnSettings();
        
        datatable.fnSort( [ [5,'desc'] ] );
	

        $(document).on('click', '.deleteTransactions', function() {
            
            var batchName = $(this).attr('rel');
           
            if(confirm("Are you sure you want to remove this batch?")) {
                
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });
                
                $.ajax({
                    url: 'deleteBatch.do',
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

    });
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}
