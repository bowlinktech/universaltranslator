

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



        //This function will launch the new configuration connection overlay with a blank screen
        $(document).on('click', '#createNewCCDElement', function () {
            $.ajax({
                url: 'createNewCCDElement',
                type: "GET",
                success: function (data) {
                    $("#ccdElementModal").html(data);
                }
            });
        });

        //This function will launch the new configuration connection overlay with a blank screen
        $(document).on('click', '.editElement', function () {
            $.ajax({
                url: 'editCCDElement',
                data: {'elementId': $(this).attr('rel')},
                type: "GET",
                success: function (data) {
                    $("#ccdElementModal").html(data);
                }
            });
        });


        //Function to submit the new CCD Element 
        $(document).on('click', '#submitElementButton', function (event) {
            var errorFound = 0;

            //Remove any error message classes
            $('#elementDiv').removeClass("has-error");
            $('#elementMsg').removeClass("has-error");
            $('#elementMsg').html('');
            $('#fieldAppendDiv').removeClass("has-error");
            $('#fieldValueDiv').removeClass("has-error");
            $('#fieldValueMsg').removeClass("has-error");
            $('#fieldValueMsg').html('');

            //Make sure a name is added
            if ($('#element').val() == '') {
                $('#elementDiv').addClass("has-error");
                $('#elementMsg').addClass("has-error");
                $('#elementMsg').html('The element value is a required field.');
                errorFound = 1;
            }

            //Make sure either the default value is entered or a field is selected
            if ($('#newFieldValue').val() == '' && $('#defaultValue').val() == '') {
                $('#fieldValueDiv').addClass("has-error");
                $('#fieldAppendDiv').addClass("has-error");
                $('#fieldValueMsg').addClass("has-error");
                $('#fieldValueMsg').html('Please enter in a default value or select a field!');
                errorFound = 1;
            }


            if (errorFound == 1) {
                event.preventDefault();
                return false;
            }

            $('#ccdElementForm').attr('action', 'saveCCDElement');
            $('#ccdElementForm').submit();

        });



    });

});
