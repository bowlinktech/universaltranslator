/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");


        $('#submitButton').click(function () {
            $.ajax({
                url: '/forgotPassword.do',
                type: "Post",
                data: {'identifier': $('#identifier').val()},
                success: function (data) {
                    if (data > 0) {
                        sendEmail(data);
                    } else {
                        $('#noResults').show();
                        $('.alert').delay(2000).fadeOut(1000);
                    }
                }
            });
        });

    });
});

function sendEmail(userId) {
    $('#noResults').hide();
    $('#emailSent').show();
    $('.alert').delay(2000).fadeOut(1000);
    $.ajax({
        url: '/sendPassword.do',
        type: "Post",
        data: {'userId': userId}
    });
}