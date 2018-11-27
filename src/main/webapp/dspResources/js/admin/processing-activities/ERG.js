/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //this function will submit the batchId for viewing detailed audit report
        $(document).on('click', '.viewLink', function () {
            $('#viewBatchAuditReport').submit();
        });

        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function () {
            $.ajax({
                url: 'viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#statusModal").html(data);
                }
            });
        });

        $('.submitMessage').click(function () {
            var errorsFound = 0;

            //Only check form fields if Sending or Releasing the message
            errorsFound = checkFormFields();

            if (errorsFound == 0) {

                var formData = $("#messageForm").serialize();

                var batchName = $('#batchLink').attr('rel');

                $.ajax({
                    url: 'editMessage',
                    data: formData,
                    type: "POST",
                    async: false,
                    dataType: "json",
                    success: function (data) {
                        window.location.href = 'inbound/auditReport/' + batchName;
                    },
                    error: function (xhr) {
                        console.log(xhr.responseText);
                    }
                });


            } else {
                $('.alert-danger').show();
                $('.alert-danger').delay(2000).fadeOut(1000);
            }
        });

    });
});


function checkFormFields() {
    var errorFound = 0;

    $('div').removeClass("has-error");
    $('span.has-error').html("");

    //Look at all required fields.
    $('.required').each(function () {
        var fieldNo = $(this).attr('id');

        if ($(this).val() === '') {
            $('#fieldDiv_' + fieldNo).addClass("has-error");
            $('#errorMsg_' + fieldNo).addClass("has-error");
            $('#errorMsg_' + fieldNo).html('This is a required field.');
            errorFound = 1;
        }
    });

    //Look at all Email validation types
    $('.Email').each(function () {
        var fieldNo = $(this).attr('id');
        var emailVal = $(this).val();

        if (emailVal != '') {
            var emailValidated = validateEmail(emailVal);

            if (emailValidated === false) {
                $('#fieldDiv_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).html('This is not a valid email address.');
                errorFound = 1;
            }
        }
    });

    //Look at all Phone Number validation types
    $('.Phone-Number').each(function () {
        var fieldNo = $(this).attr('id');
        var phoneVal = $(this).val();

        if (phoneVal != '') {
            var phoneValidated = validatePhone(phoneVal);

            if (phoneValidated === false) {
                $('#fieldDiv_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).html('This is not a valid phone number.');
                errorFound = 1;
            }
        }
    });

    //Look at all numeric validation types
    $('.Numeric').each(function () {
        var fieldNo = $(this).attr('id');
        var fieldVal = $(this).val();

        if (fieldVal != '') {
            var fieldValidated = validateNumericValue(fieldVal);

            if (fieldValidated === false) {
                $('#fieldDiv_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).html('The value must be numeric.');
                errorFound = 1;
            }
        }
    });

    //Look at all URL validation types
    $('.URL').each(function () {
        var fieldNo = $(this).attr('id');
        var URLVal = $(this).val();

        if (URLVal != '') {
            var URLValidated = validateURL(URLVal);

            if (URLValidated === false) {
                $('#fieldDiv_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).html('This is not a valid URL.');
                errorFound = 1;
            }
        }
    });

    //Look at all Date validation types
    $('.Date').each(function () {
        var fieldNo = $(this).attr('id');
        var dateVal = $(this).val();

        if (dateVal != '') {
            var DateValidated = validateDate(dateVal);

            if (DateValidated === false) {
                $('#fieldDiv_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).addClass("has-error");
                $('#errorMsg_' + fieldNo).html('This is not a valid Date, format should be mm/dd/yyyy.');
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
    } else {
        return true;
    }
}

function validatePhone($phone) {
    var phoneRegExp = /^(?:(?:\+?1\s*(?:[.-]\s*)?)?(?:\(\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\s*\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\s*(?:[.-]\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\s*(?:[.-]\s*)?([0-9]{4})(?:\s*(?:#|x\.?|ext\.?|extension)\s*(\d+))?$/;

    if (!phoneRegExp.test($phone)) {
        return false;
    } else {
        return true;
    }
}

function validateNumericValue($fieldVal) {
    var numericReg = /^\d*[0-9](|.\d*[0-9]|,\d*[0-9])?$/;
    if (!numericReg.test($fieldVal)) {
        return false;
    } else {
        return true;
    }
}

function validateURL($URL) {
    var URLReg = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/;
    if (!URLReg.test($URL)) {
        return false;
    } else {
        return true;
    }
}

function validateDate($date) {
    var DateReg = /\b\d{1,2}[\/-]\d{1,2}[\/-]\d{4}\b/; // mm/dd/yyyy
    var DateReg2 = /\b\d{4}[\-]\d{1,2}[\-]\d{1,2}\b/; // yyyy-mm-dd
    if (!DateReg.test($date) && !DateReg2.test($date)) {
        return false;
    } else {
        return true;
    }
}
