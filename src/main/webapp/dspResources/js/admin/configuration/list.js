


require(['./main'], function () {
    require(['jquery'], function ($) {

        var oSettings = datatable.fnSettings();

        datatable.fnSort([[6, 'desc']]);

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


