


require(['./main'], function () {
        
       $(document).on('click', '.uploadFile', function() {
           var fileDropLocation = $(this).attr('rel2');
           var configId = $(this).attr('rel');
           
           $.ajax({
                url: '/administrator/configurations/configFileUpload',
                data: {
                   'configId':configId,
		   'fileDropLocation':fileDropLocation
		},
                type: "GET",
                success: function(data) {
                    $("#configFileUploadModal").html(data);
                }
            });
       });
       
       //The function to submit the uploaded file
        $(document).on('click', '#submitFileButton', function (event) {
            var errorFound = 0;

            $('#configFileDiv').removeClass("has-error");
            $('#configFileMsg').removeClass("has-error");
            $('#configFileMsg').html('');
            $('#successMsg').hide();
            $('#errorMsg').hide();
            
            var expectedExtension = $('#expectedExt').val();
            
            if($('#configFile').val() !== '') {
                var file = $('#configFile').val();
                var uploadedFileExt = file.substr( (file.lastIndexOf('.') +1) );
            }
            
            //Make sure a file is selected and is a text file
            if ($('#configFile').val() === '') {
                $('#configFileDiv').addClass("has-error");
                $('#configFileMsg').addClass("has-error");
                $('#configFileMsg').html('The file is a required field.');
                errorFound = 1;
            }
            else if (uploadedFileExt != expectedExtension) {
                $('#configFileDiv').addClass("has-error");
                $('#configFileMsg').addClass("has-error");
                $('#configFileMsg').html('According to the configruation the file must have a .' + $('#expectedExt').val() + ' extension.');
                errorFound = 1;
            }

            if (errorFound == 1) {
                event.preventDefault();
                return false;
            }
            else {
              
                //check and submit form
		var form = $('#configFileForm')[0];
		var formData = new FormData(form);
		$.ajax({
		    url: '/administrator/configurations/submitConfigFileForProcessing',
		    type: "POST",
		    enctype: 'multipart/form-data',
		    processData: false,  // Important!
		    contentType: false,
		    cache: false,
		    data: formData,
		    success: function(data) {
                       if(data == 1) {
                           $('#configFile').val("");
                           $('#submitFileButton').hide();
                           $('#successMsg').show();
                       }
                       else {
                           $('#configFile').val("");
                           $('#errorMsg').show();
                       }
		    }
		});
            }


        });
        
       $(document).on('click','.printConfig',function() {
           /* $('body').overlay({
                glyphicon : 'print',
                message : 'Gathering Details...'
            });*/
            
            var configId = $(this).attr('rel');
            
            $.ajax({
                url: 'createConfigPrintPDF.do',
                data: {
                    'configId': configId
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/printConfig/'+ data;
                        $('#successMsg').show();
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
        });
        
	$('#myTabContent a[href="#source-config"]').tab('show');
	
	 $("a[data-toggle=\"tab\"]").on("shown.bs.tab", function (e) {
		$($.fn.dataTable.tables( true ) ).css('width', '100%');
		$($.fn.dataTable.tables( true ) ).DataTable().columns.adjust().draw();
            });
	
	try {
	    /* Table initialisation */
	    var sourceconfigdatatable = $('#sourceconfigdatatable').dataTable({
		"bStateSave": false,
		"sPaginationType": "bootstrap",
                columnDefs: [ { type: 'date', 'targets': [4,5] } ],
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
	    sourceconfigdatatable.fnSort([[0, 'desc']]);
	}
	catch(err) {}
	
	try {
	    /* Table initialisation */
	    var targetconfigdatatable = $('#targetconfigdatatable').dataTable({
		"bStateSave": false,
		"sPaginationType": "bootstrap",
                 columnDefs: [ { type: 'date', 'targets': [4,5] } ],
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
	    targetconfigdatatable.fnSort([[0, 'desc']]);
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


