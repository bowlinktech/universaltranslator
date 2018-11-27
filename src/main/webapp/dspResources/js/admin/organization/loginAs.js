
require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        $("input:text,form").attr("autocomplete", "off");

        //Function to submit the login as request and validates the password
        //It all checks pass the login as form will be submitted
        $('#submitLoginAsButton').click(function (event) {

            $('div.form-group').removeClass("has-error");
            $('span.control-label').removeClass("has-error");
            $('span.control-label').html("");

            //we do all the check
            var proceed = true;

            //first we check to make sure all fields have a value
            if ($('#userName').val().trim() == "") {
                $('#userNameDiv').addClass("has-error");
                $('#userNameMsg').html('This cannot be blank!');
                proceed = false;
            }

            if ($('#password').val().trim() == "") {
                $('#passwordDiv').addClass("has-error");
                $('#passwordMsg').html('This cannot be blank!');
                proceed = false;
            }

            if ($('#loginAsUser').val() == "") {
                $('#loginAsUserDiv').addClass("has-error");
                $('#loginAsUserMsg').html('Please select the user you would like to login as.');
                proceed = false;
            }

            if ($('#realUsername').val() != $('#userName').val()) {
                $('#userNameDiv').addClass("has-error");
                $('#userNameMsg').html('That is not correct!');
                proceed = false;
            }

            if (proceed) {
                var password = $('#password').val();
                var actionValue = 'loginAsCheck';

                $.ajax({
                    url: actionValue,
                    data: {'j_password': password},
                    type: "POST",
                    async: false,
                    success: function (data) {
                        if (data.indexOf('pwmatched') != -1) {
                            $('#form-admin-login').submit();
                        } else {
                            $('#passwordDiv').addClass("has-error");
                            $('#passwordMsg').html('Invalid Credentials');
                        }
                    }
                });
                event.preventDefault();
                return false;
            }

        });



    });
});




