/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function ($) {

        $('#submitButton').click(function () {

            //Remove any error message classes
            $('#newPasswordDiv').removeClass("has-error");
            $('#newPasswordMsg').removeClass("has-error");
            $('#newPasswordMsg').html('');

            $('#confirmPasswordDiv').removeClass("has-error");
            $('#confirmPasswordMsg').removeClass("has-error");
            $('#confirmPasswordMsg').html('');

            var newPassword = $('#newPassword').val();
            var confirmPassword = $('#confirmPassword').val();

            if (newPassword.length < 5) {
                $('#newPasswordDiv').addClass("has-error");
                $('#newPasswordMsg').addClass("has-error");
                $('#newPasswordMsg').html('The new password must be between 5 and 15 characters.');
            } else if (confirmPassword.length < 5) {
                $('#confirmPasswordDiv').addClass("has-error");
                $('#confirmPasswordMsg').addClass("has-error");
                $('#confirmPasswordMsg').html('The confirm password must be between 5 and 15 characters.');
            } else if (newPassword != confirmPassword) {
                $('#confirmPasswordDiv').addClass("has-error");
                $('#confirmPasswordMsg').addClass("has-error");
                $('#confirmPasswordMsg').html('The confirm password must be equal to the new password.');
            } else {

                $('#resetPassword').submit();

            }


        });


    });
});