
require(['./main'], function () {

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
            $("#logoForm").submit();
        }

    });
});



function checkFileUploaded() {
    var errorFound = 0;

    if ($('#frontEndFile').val() == '' && $('#backEndFile').val() == '') {
        $('#frontEndFileDiv').addClass("has-error");
        $('#frontEndFileMsg').addClass("has-error");
        $('#frontEndFileMsg').html('Please upload an image file here or below.');
        $('#backEndFileDiv').addClass("has-error");
        $('#backEndFileMsg').addClass("has-error");
        $('#backEndFileMsg').html('Please upload an image file here or above.');
        errorFound = 1;
    }

    //Make sure the file is a correct format
    var frontEndExt = $('#frontEndFile').val().toLowerCase();
    if (frontEndExt != '' && frontEndExt.indexOf('.jpg') == -1
            && frontEndExt.indexOf('.png') == -1 && frontEndExt.indexOf('.gif') == -1
            ) {
        $('#frontEndFileDiv').addClass("has-error");
        $('#frontEndFileMsg').addClass("has-error");
        $('#frontEndFileMsg').html('The front end logo file must be an png, gif or jpg file (.png, .gif or .jpg format).');
        errorFound = 1;

    }

    //Make sure the file is a correct format
    var backEndExt = $('#backEndFile').val().toLowerCase();
    if (backEndExt != '' && backEndExt.indexOf('.jpg') == -1
            && backEndExt.indexOf('.png') == -1 && backEndExt.indexOf('.gif') == -1
            ) {

        $('#backEndFileDiv').addClass("has-error");
        $('#backEndFileMsg').addClass("has-error");
        $('#backEndFileMsg').html('The back end logo file must be an png, gif or jpg file (.png, .gif or .jpg format).');
        errorFound = 1;

    }
    return errorFound;
}


