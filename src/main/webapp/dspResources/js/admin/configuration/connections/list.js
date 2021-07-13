

require(['./main'], function () {
    
    $(document).on('click', '.importConnection', function() {
        
        $.ajax({
             url: '/administrator/configurations/connections/connectionImportUpload',
             data: {},
             type: "GET",
             success: function(data) {
                 $("#connectionFileUploadModal").html(data);
             }
         });
    });
    
    //The function to submit the import file
    $(document).on('click', '#submitImportFileButton', function (event) {
        var errorFound = 0;

        $('#importConnectionFileDiv').removeClass("has-error");
        $('#importConnectionFileMsg').removeClass("has-error");
        $('#importConnectionFileMsg').html('');
        $('#errorMsg').hide();

        var expectedExtension = $('#expectedExt').val();

        if($('#importConnectionFile').val() !== '') {
            var file = $('#importConnectionFile').val();
            var uploadedFileExt = file.substr( (file.lastIndexOf('.') +1) );
        }

        //Make sure a file is selected and is a text file
        if ($('#importConnectionFile').val() === '') {
            $('#importConnectionFileDiv').addClass("has-error");
            $('#importConnectionFileMsg').addClass("has-error");
            $('#importConnectionFileMsg').html('The file is a required field.');
            errorFound = 1;
        }
        else if (uploadedFileExt != expectedExtension) {
            $('#importConnectionFileDiv').addClass("has-error");
            $('#importConnectionFileMsg').addClass("has-error");
            $('#importConnectionFileMsg').html('The connection import file must have a .' + $('#expectedExt').val() + ' extension.');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {
            //check and submit form
            var form = $('#importConnectionFileForm')[0];
            var formData = new FormData(form);
            $.ajax({
                url: '/administrator/configurations/connections/submitConnectionImportFile',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   
                   if(data == 1) {
                       window.location.href = '/administrator/configurations/connections';
                   }
                   else if(data == 2) {
                       $('#importConnectionFile').val("");
                       $('#importError').html("The file uploaded was not a correct connection import script.");
                       $('#errorMsg').show();
                   }
                   else {
                       $('#importConnectionFile').val("");
                       $('#errorMsg').show();
                   }
                }
            });
        }
     });
    
    $(document).on('click', '.exportConnection', function() {
            
        var connectionId = $(this).attr('rel');

        if(confirm("Are you sure you want to export this connection? The source and target configuration must first be exported and imported into the new system before you can import the connection.")) {

            $.ajax({
                url: '/administrator/configurations/connections/createConnectionExportFile.do',
                data: {
                    'connectionId': connectionId
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/connections/printConnectionExport/'+ data;
                    }
                    else {
                        $('#exportErrorMsg').show();
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

        var connectionId = $(this).attr('rel');

        $.ajax({
            url: '/administrator/configurations/connections/createConnectionPrintPDF.do',
            data: {
                'connectionId': connectionId
            },
            type: "GET",
            dataType : 'text',
            contentType : 'application/json;charset=UTF-8',
            success: function(data) {
                if(data !== '') {
                    window.location.href = '/administrator/configurations/connections/printConfig/'+ data;
                    $('#successMsg').show();
                    //$('#dtDownloadModal').modal('toggle');
                }
                else {
                    $('#errorMsg').show();
                }
            }
        });
    });

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



