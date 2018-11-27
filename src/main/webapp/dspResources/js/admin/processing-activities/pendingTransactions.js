/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        //This function will process the selected transaction
        $(document).on('click', '.process', function () {

            var confirmed = confirm("Are you sure you want to process this transaction?");

            if (confirmed) {

                $('body').overlay({
                    glyphicon: 'floppy-disk',
                    message: 'Processing...'
                });

                $.ajax({
                    url: '../processTransaction',
                    data: {'transactionId': $(this).attr('rel')},
                    type: "GET",
                    success: function (data) {
                        $('#searchForm').attr('action', '/administrator/processing-activity/pending/transactions?msg=processed');
                        $('#searchForm').submit();
                    }
                });
            }

        });

        //This function will not process the selected transaction
        $(document).on('click', '.donotprocess', function () {

            var confirmed = confirm("Are you sure you do not want to process this transaction?");

            if (confirmed) {

                $('body').overlay({
                    glyphicon: 'floppy-disk',
                    message: 'Canceling...'
                });

                $.ajax({
                    url: '../donotprocessTransaction',
                    data: {'transactionId': $(this).attr('rel')},
                    type: "GET",
                    success: function (data) {
                        $('#searchForm').attr('action', '/administrator/processing-activity/pending/transactions?msg=notprocessed');
                        $('#searchForm').submit();
                    }
                });

            }

        });

        $(document).on('click', '.nxtPage', function () {
            var page = $(this).attr('rel');

            $('#page').val(page);
            $('#searchForm').submit();
        });

    });
});

function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('#searchForm').submit();

}