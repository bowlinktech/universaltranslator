


require(['./main'], function () {
    require(['jquery'], function ($) {
	
	try {
	    /* Table initialisation */
	    var sourceconfigdatatable = $('#sourceconfigdatatable').dataTable({
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
	    sourceconfigdatatable.fnSetColumnVis(5,false);
	    sourceconfigdatatable.fnSort([[5, 'desc']]);
	}
	catch(err) {}
	
	try {
	    /* Table initialisation */
	    var targetconfigdatatable = $('#targetconfigdatatable').dataTable({
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
	    targetconfigdatatable.fnSetColumnVis(5,false);
	    targetconfigdatatable.fnSort([[5, 'desc']]);
	}
	catch(err) {}


        $.ajaxSetup({
            cache: false
        });

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        $("input:text,form").attr("autocomplete", "off");

        $(document).on('click', '.editConfig', function () {
            window.location.href = "details?i=" + $(this).attr('rel');
        });
	
	$(document).on('click', '.deleteConfig', function() {
            
            var configId = $(this).attr('rel');
           
            if(confirm("Are you sure you want to delelete this configuration?")) {
                
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });
                
                $.ajax({
                    url: 'deleteConfiguration.do',
                    data: {
                        'configId': configId
                    },
                    type: 'POST',
                    success: function(data) {
                      window.location.href = "list?msg=deleted";
                    }
                });
            }
        });
	
	$(document).on('click', '.copyConfig', function() {
            
            var configId = $(this).attr('rel');
           
            if(confirm("Are you sure you want to copy this configuration?")) {
                
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Copying...'
                });
                
                $.ajax({
                    url: 'copyConfiguration.do',
                    data: {
                        'configId': configId
                    },
                    type: 'POST',
                    success: function(data) {
                      window.location.href = "details?i=" + data;
                    }
                });
                
            }
            
        });

        $('#searchConfigBtn').click(function () {
            $('#searchForm').submit();
        });

    });
});


