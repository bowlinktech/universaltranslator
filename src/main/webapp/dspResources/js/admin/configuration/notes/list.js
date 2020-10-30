


require(['./main'], function () {
    
     //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }
    
   
        
    var configurationnotesdatatable = $('#configurationnotes').dataTable({
        "bStateSave": false,
        "sPaginationType": "bootstrap",
        columnDefs: [ { type: 'date', 'targets': [2] } ],
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
    
    configurationnotesdatatable.fnSort([[2, 'desc']]);
    
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
    
    //This function will launch the new configuration note overlay with a blank form
    $(document).on('click', '#createNewNote', function () {
        
        var configId = $(this).attr('rel');

        $.ajax({
            url: 'newConfigurationNote',
            type: "GET",
            data: {
                'configId': configId
            },
            success: function (data) {
                $("#configNoteModal").html(data);
            }
        });
    });
    
    //This function will launch the configuration note overlay with the details of the selected note
    $(document).on('click', '.editNote', function () {
        
        var noteId = $(this).attr('rel');

        $.ajax({
            url: 'editConfigurationNote',
            type: "GET",
            data: {
                'noteId': noteId
            },
            success: function (data) {
                $("#configNoteModal").html(data);
            }
        });
    });
    
    //The function to submit the new configuration note
    $(document).on('click', '#submitConfigurationNote', function (event) {
        $('#updateMadeDiv').removeClass("has-error");
        $('#updateMadeMsg').removeClass("has-error");
        $('#updateMadeMsg').html('');

        var errorFound = 0;

        //Make sure a note is entered
        if ($('#updateMade').val() === '') {
            $('#updateMadeDiv').addClass("has-error");
            $('#updateMadeMsg').addClass("has-error");
            $('#updateMadeMsg').html('The note is a required field!');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {
            $('#configurationNoteForm').submit();
        }
    });
    
    $(document).on('click','.deleteNote',function() {
        var noteId = $(this).attr('rel');
        
        if(confirm("Are you sure you want to remove this configuration note?")) {
            $('body').overlay({
                glyphicon : 'floppy-disk',
                message : 'Deleting...'
            });

            $.ajax({
                url: 'deleteConfigurationNote.do',
                data: {
                    'noteId': noteId
                },
                type: 'POST',
                success: function(data) {
                   location.reload();
                }
            });
        }
    });
});


