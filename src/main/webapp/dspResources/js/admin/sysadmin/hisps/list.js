
require(['./main'], function () {

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(5000);
    }

    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the new dataItem overlay with a blank screen
    $(document).on('click', '#createNewHisp', function () {

        $.ajax({
            url: 'hisps/create',
            type: "GET",
            success: function (data) {
                $("#hispModal").html(data);
            }
        });
    });

    //This function will launch the edit hisp item overlay populating the fields
    $(document).on('click', '.hispEdit', function () {

        var hispDetailsAction = "hisps/view?i=" + $(this).attr('rel');

        $.ajax({
            url: hispDetailsAction,
            type: "GET",
            success: function (data) {
                $("#hispModal").html(data);
            }
        });
    });


    $(document).on('click', '#submitButton', function (event) {
        var formData = $("#hispform").serialize();
        var actionValue = "hisps/" + $(this).attr('rel').toLowerCase();

        var hasErrors = 0;

        if($('#hispName').val() === '') {
            $('#hispNameDiv').addClass("has-error");
            $('#hispNameMsg').addClass("has-error");
            $('#hispNameMsg').html('The HISP name is a required field.');
            hasErrors = 1;
        }

        if($('#utAPIUsername').val() === '') {
            $('#hispUTAPIUsernameDiv').addClass("has-error");
            $('#hispUTAPIUsernameMsg').addClass("has-error");
            $('#hispUTAPIUsernameMsg').html('The HISP UT API Username is a required field.');
            hasErrors = 1;
        }

        if($('#utAPIPassword').val() === '') {
            $('#hispUTAPIPasswordDiv').addClass("has-error");
            $('#hispUTAPIPasswordMsg').addClass("has-error");
            $('#hispUTAPIPasswordMsg').html('The HISP UT API Password is a required field.');
            hasErrors = 1;
        }

        if($('#hispAPIUsername').val() === '') {
            $('#hispAPIUsernameDiv').addClass("has-error");
            $('#hispAPIUsernameMsg').addClass("has-error");
            $('#hispAPIUsernameMsg').html('The HISP API Username is a required field.');
            hasErrors = 1;
        }

        if($('#hispAPIPassword').val() === '') {
            $('#hispAPIPasswordDiv').addClass("has-error");
            $('#hispAPIPasswordMsg').addClass("has-error");
            $('#hispAPIPasswordMsg').html('The HISP API Password is a required field.');
            hasErrors = 1;
        }

        if($('#hispAPIURL').val() === '') {
            $('#hispAPIURLDiv').addClass("has-error");
            $('#hispAPIURLMsg').addClass("has-error");
            $('#hispAPIURLMsg').html('The HISP API URL is a required field.');
            hasErrors = 1;
        }



        if(hasErrors == 0) {
            $.ajax({
                url: actionValue,
                data: formData,
                type: "POST",
                async: false,
                success: function (data) {

                    if (data.indexOf('hispUpdated') != -1) {
                        var goToUrl = "hisps?msg=updated";
                        window.location.href = goToUrl;
                    } else if (data.indexOf('hispCreated') != -1) {
                        var goToUrl = "hisps?msg=created";
                        window.location.href = goToUrl;
                    } else {
                        $("#hispModal").html(data);
                    }
                }

            });
        }
        event.preventDefault();
        return false;

    });
});



