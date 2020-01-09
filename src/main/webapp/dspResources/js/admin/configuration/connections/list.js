

require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	var connectiondataTable = $('#connectiondataTable').dataTable({
	    "bStateSave": true,
	    "iCookieDuration": 60,
	    "sPaginationType": "bootstrap",
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
	connectiondataTable.fnSort([[0, 'desc']]);
	
	
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



