
require(['./main'], function () {
    
    $(document).on('click','.createNewTemplate',function() {
      
        var configId = $(this).attr('rel');

        $.ajax({
            url: 'createNewFieldSettingsTemplate.do',
            data: {
                'configId': configId
            },
            type: "GET",
            dataType : 'text',
            contentType : 'application/json;charset=UTF-8',
            success: function(data) {
                if(data !== '') {
                    window.location.href = '/administrator/configurations/printNewFieldSettingsTemplate/'+ data;
                }
                else {
                    alert("An error occurred creating your template file. A Health-e-Link system administrator has been notified.");
                }
            }
        });
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

    $("input:text,form").attr("autocomplete", "off");

    $(document).on('click', '.createDataTranslationDownload', function() {
       $.ajax({
            url: '/administrator/configurations/createDataTranslationDownload',
            data: {
               'configId':$(this).attr('rel')
            },
            type: "GET",
            success: function(data) {
                $("#dtDownloadModal").html(data);
            }
        });
    });

    $(document).on('click', '#generateDTButton', function() {

        var errorFound = 0;

       //Makes sure name is entered and entity is selected
       if($('#fileName').val() === "") {
           $('#dtnameDiv').addClass("has-error");
           errorFound = 1;
       }

       if(errorFound == 0) {
           $.ajax({
                url: '/administrator/configurations/dataTranslationsDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val()
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWFile/'+ data;
                        $('#successMsg').show();
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
       }

    });

    //This function will save the messgae type field mappings
    $('#saveDetails').click(function () {
        $('#action').val('save');

        //Need to make sure all required fields are marked if empty.
        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {
            $('#messageSpecs').submit();
        }
    });

    $('#next').click(function (event) {
        $('#action').val('next');

        var hasErrors = 0;
        hasErrors = checkFormFields();

        if (hasErrors == 0) {
            $('#messageSpecs').submit();
        }
    });
});



function checkFormFields() {
    var hasErrors = 0;

    //Remove all has-error class
    $('div.form-group').removeClass("has-error");
    $('span.control-label').removeClass("has-error");
    $('span.control-label').html("");
    
    if ($('#parsingScriptFile').length > 1) {

        var filename = $('#parsingScriptFile').val();
        var extension = filename.replace(/^.*\./, '');

        if (extension == filename) {
            extension = '';
        } else {
            // if there is an extension, we convert to lower case
            // (N.B. this conversion will not effect the value of the extension
            // on the file upload.)
            extension = extension.toLowerCase();
        }

        if (extension != "jar") {
            $('#parsingTemplateDiv').addClass("has-error");
            $('#parsingTemplateMsg').addClass("has-error");
            $('#parsingTemplateMsg').html('The Parsing Script must be a jar file.');
            hasErrors = 1;
        }

    }

    //Make sure at least one reportable field is selected
    /*var rptField1 = $('#rptField1').val();
    var rptField2 = $('#rptField2').val();
    var rptField3 = $('#rptField3').val();
    var rptField4 = $('#rptField4').val();

    if (rptField1 == 0 && rptField2 == 0 && rptField3 == 0 && rptField4 == 0) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('At least one reportable field must be selected.<br />');
        hasErrors = 1;
    }

    // Check to make sure there are different selected fields 
    if (hasErrors == 0 && rptField1 > 0 && (rptField1 == rptField2 || rptField1 == rptField3 || rptField1 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }
    if (hasErrors == 0 && rptField2 > 0 && (rptField2 == rptField3 || rptField2 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }
    if (hasErrors == 0 && rptField3 > 0 && (rptField3 == rptField4)) {
        $('.rtpField').addClass("has-error");
        $('#rptFieldMsg').addClass("has-error");
        $('#rptFieldMsg').html('All reportable fields must be different.<br />');
        hasErrors = 1;
    }*/

    return hasErrors;
}


