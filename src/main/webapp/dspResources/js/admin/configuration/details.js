
require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }


        $('#saveDetails').click(function (event) {
            $('#action').val('save');
            var hasErrors = 0;
            hasErrors = checkform();
            if (hasErrors == 0) {
                $("#configuration").submit();
            }

        });

        $('#next').click(function (event) {
            $('#action').val('next');
            var hasErrors = 0;
            hasErrors = checkform();
            if (hasErrors == 0) {
                $("#configuration").submit();
            }
        });

        //When the Configuration type changes need to show the
        //source type field only if the config type is a source
        $('.type').change(function (event) {

            if ($(this).val() == 2) {
                $('#sourceTypeDiv').hide();
                $('input:radio[id="sourceType"]').get(0).checked = true;
            } else {
                $('#sourceTypeDiv').show();
                $('input:radio[id="sourceType"]').get(0).checked = true;
            }
        });


        //When the source type changes need to show the
        //associated message type field only if the source type is feedback report
        $('.sourceType').change(function (event) {

            if ($(this).val() == 1) {
                $('#associatedmessageTypeTopDiv').hide();
            } else {
                $('#associatedmessageTypeTopDiv').show();
            }
        });

    });
});


function checkform() {
    var errorFound = 0;

    //Check to make sure an organization is selected
    if ($('#organization').val() === '') {
        $('#orgDiv').addClass("has-error");
        $('#configOrgMsg').addClass("has-error");
        $('#configOrgMsg').html('The organization is a required field.');
        errorFound = 1;
    }

    //Check to make sure a message type is selected
    if ($('#messageTypeId').val() === '') {
        $('#messageTypeDiv').addClass("has-error");
        $('#configMessageTypeMsg').addClass("has-error");
        $('#configMessageTypeMsg').html('The message type is a required field.');
        errorFound = 1;
    }

    //Check to make sure a configuration name is entered
    if ($('#configName').val() === '') {
        $('#configNameDiv').addClass("has-error");
        $('#configNameMsg').addClass("has-error");
        $('#configNameMsg').html('The configuration name is a required field.');
        errorFound = 1;
    }
    //Make sure an associated message type is selected if it is a Feedback report configuration
    if ($('#sourceTypeVal').val() == 2 && $('#associatedmessageTypeId').val() === '') {
        $('#associatedmessageTypeDiv').addClass("has-error");
        $('#associatedmessageTypeMsg').addClass("has-error");
        $('#associatedmessageTypeMsg').html('The associated message type is a required field.');
        errorFound = 1;
    }

    return errorFound;

}


