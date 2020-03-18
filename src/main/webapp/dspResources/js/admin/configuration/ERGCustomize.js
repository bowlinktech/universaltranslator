
require(['./main'], function () {
    require(['jquery'], function ($) {
        
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

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }


        //If any field changes need to show the message in red that nothign
        //will be saved unless teh "Saved" button is pressed
        $(document).on('change', '.formField', function () {
            $('#saveMsgDiv').show();
        });

        //Function that will check off all 'use field' checkboxes
        $(document).on('click', '#selectAllFields', function () {
            $('.useFields').prop("checked", !$('.useFields').prop("checked"));

            if ($('.useFields').prop("checked") == true) {
                $(this).children("span").addClass("glyphicon-ok");
            } else {
                $(this).children("span").removeClass("glyphicon-ok");
            }
            $('#saveMsgDiv').show();
        });


        //Function that will handle changing a display position and
        //making sure another field in the same bucket does not have
        //the same position selected. It will swap display position
        //values with the requested position.
        $('.dspPos').change(function () {
            //Store the current position
            var currDspPos = $(this).attr('rel2');
            var bucketVal = $(this).attr('rel');
            var newDspPos = $(this).val();

            $('.dspPos_' + bucketVal).each(function () {
                if ($(this).attr('rel2') == newDspPos) {
                    $(this).val(currDspPos);
                    $(this).attr('rel2', currDspPos);
                }
            });

            $(this).val(newDspPos);
            $(this).attr('rel2', newDspPos);

        });


        //This function will save the messgae type field mappings
        $('#saveDetails').click(function (event) {
            $('#action').val('save');

            //Need to make sure all required fields are marked if empty.
            var hasErrors = 0;
            hasErrors = checkFormFields();

            if (hasErrors == 0) {

                var formData = $("#formFields").serialize();

                $.ajax({
                    url: 'saveFields',
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        $('.fieldsUpdated').show();
                        $('.alert').delay(2000).fadeOut(1000);
                    }
                });
                event.preventDefault();
                return false;

            }
        });

        $('#next').click(function (event) {
            $('#action').val('next');

            var hasErrors = 0;
            hasErrors = checkFormFields();

            if (hasErrors == 0) {

                var formData = $("#formFields").serialize();

                $.ajax({
                    url: 'saveFields',
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        window.location.href = 'translations';
                    }
                });
                event.preventDefault();
                return false;
            }
        });


    });

});


function checkFormFields() {
    var errorsFound = 0;

    var row = 0;
    var usedFields = 0;

    $('.alert-danger').hide();

    //Check field labels
    $('.fieldLabel').each(function () {
        row = $(this).attr('rel');
        $('#fieldLabel_' + row).removeClass("has-error");
        $('#fieldLabelMsg_' + row).html("");
        if ($(this).val() == '') {
            $('#fieldLabel_' + row).addClass("has-error");
            $('#fieldLabelMsg_' + row).html("The field label is required!");

            errorsFound = 1;
        }
    });

    if ($('.useFields:checked').length > 0) {
        usedFields = 1;
    }

    if (usedFields == 0) {
        $('.alert-danger').show();
        errorsFound = 1;
    }

    return errorsFound;
}



