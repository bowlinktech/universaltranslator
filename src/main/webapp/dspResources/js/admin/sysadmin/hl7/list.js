require(['./main'], function () {

    $.ajaxSetup({
        cache: false
    });

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }

    $("input:text,form").attr("autocomplete", "off");

    $(document).on('click', '#hl7Row', function () {
        window.location.href = "hl7/details?hl7Id=" + $(this).attr('rel');
    });
});