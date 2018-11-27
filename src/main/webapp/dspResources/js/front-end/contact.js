/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function() {
    require(['jquery'], function($) {

        $("input:text,form").attr("autocomplete", "off");
        
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
        
        $('.submitMessage').click(function() {
            var errorsFound = 0;

            //Only check form fields if Sending or Releasing the message
            errorsFound = checkFormFields();

            if (errorsFound == 0) {
                $('#contactForm').submit();
            }
            
        });
    });
});

function checkFormFields() {
    var errorFound = 0;

    $('div').removeClass("has-error");
    $('span.has-error').html("");

    //Look at all required fields.
    $('.required').each(function() {
        var fieldId = $(this).attr('id');

        if ($(this).val() === '') {
            $('#fieldDiv_' + fieldId).addClass("has-error");
            $('#errorMsg_' + fieldId).addClass("has-error");
            $('#errorMsg_' + fieldId).html('This is a required field.');
            errorFound = 1;
        }
    });

    //Look at all Email validation types
    $('.Email').each(function() {
        var fieldId = $(this).attr('id');
        var emailVal = $(this).val();

        if (emailVal != '') {
            var emailValidated = validateEmail(emailVal);

            if (emailValidated === false) {
                $('#fieldDiv_' + fieldId).addClass("has-error");
                $('#errorMsg_' + fieldId).addClass("has-error");
                $('#errorMsg_' + fieldId).html('This is not a valid email address.');
                errorFound = 1;
            }
        }
    });

    //Look at all Phone Number validation types
    $('.Phone-Number').each(function() {
        var fieldId = $(this).attr('id');
        var phoneVal = $(this).val();

        if (phoneVal != '') {
            var phoneValidated = validatePhone(phoneVal);

            if (phoneValidated === false) {
                $('#fieldDiv_' + fieldId).addClass("has-error");
                $('#errorMsg_' + fieldId).addClass("has-error");
                $('#errorMsg_' + fieldId).html('This is not a valid phone number.');
                errorFound = 1;
            }
        }
    });

    return errorFound;
}

function validateEmail($email) {
    var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
    if (!emailReg.test($email)) {
        return false;
    }
    else {
        return true;
    }
}

function validatePhone($phone) {
    //var phoneRegExp = /^(?:(?:\+?1\s*(?:[.-]\s*)?)?(?:\(\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\s*\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\s*(?:[.-]\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\s*(?:[.-]\s*)?([0-9]{4})(?:\s*(?:#|x\.?|ext\.?|extension)\s*(\d+))?$/;
    var phoneRegExp = /^[0-9-+]+$/;
    if (!phoneRegExp.test($phone)) {
        return false;
    }
    else {
        return true;
    }
}
