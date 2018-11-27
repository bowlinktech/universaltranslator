
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
            hasErrors = checkFileUploaded();
            if (hasErrors == 0) {
                $('body').overlay({
                    glyphicon: 'floppy-disk',
                    message: 'Saving...'
                });
                $("#messageType").submit();
            }

        });

        $('#next').click(function (event) {
            $('#action').val('next');
            var hasErrors = 0;
            hasErrors = checkFileUploaded();
            if (hasErrors == 0) {
                $('body').overlay({
                    glyphicon: 'floppy-disk',
                    message: 'Saving...'
                });
                $("#messageType").submit();
            }
        });

    });
});


function checkFileUploaded() {
    var errorFound = 0;

    if ($('#templateFile').val() == '' && $('#id').val() == 0) {
        $('#templateFileDiv').addClass("has-error");
        $('#templateFileMsg').addClass("has-error");
        $('#templateFileMsg').html('The message type file is a required field.');
        errorFound = 1;
    }
    //Make sure the file is a correct format
    //(.xlsx)
    if ($('#templateFile').val() != '' && $('#templateFile').val().indexOf('.xlsx') == -1) {

        $('#templateFileDiv').addClass("has-error");
        $('#templateFileMsg').addClass("has-error");
        $('#templateFileMsg').html('The message type file must be an excel file (.xlsx format).');
        errorFound = 1;

    }

    return errorFound;
}


