

require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	if($("#dataTable" ).length > 0) {
	    var oSettings = datatable.fnSettings();
        
	    datatable.fnSort( [ [3,'desc'] ] );
	    
	}
	
	$(document).on('click', '.deleteConnection', function() {
            
            var connectionId = $(this).attr('rel');
           
            if(confirm("Are you sure you want to delelete this connection?")) {
                
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });
                
                $.ajax({
                    url: '/administrator/configurations/connections/deleteConnection.do',
                    data: {
                        'connectionId': connectionId
                    },
                    type: 'POST',
                    success: function(data) {
                      window.location.href = "connections?msg=deleted";
                    }
                });
            }
        });
	
        //Update the status of the connection
        $('.changeStatus').click(function () {
            var connectionId = $(this).attr('rel');
            var newStatusVal = $(this).attr('rel2');

            $.ajax({
                url: '/administrator/configurations/connections/changeConnectionStatus.do',
                type: "POST",
                data: {'statusVal': newStatusVal, 'connectionId': connectionId},
                success: function (data) {
                    if (data === 1) {
                        window.location.href = 'connections?msg=updated'
                    }
                }
            });
        });

    });
});



