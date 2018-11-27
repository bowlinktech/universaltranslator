/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function () {
            $.ajax({
                url: '../../viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function (data) {
                    $("#statusModal").html(data);
                }
            });
        });

        $(document).on('click', '.viewLink', function () {

            var transactionId = $(this).attr('rel');
            var configId = $(this).attr('rel2');

            $.ajax({
                url: '../../ViewMessageDetails',
                data: {'Type': 1, 'transactionId': transactionId, 'configId': configId},
                type: "GET",
                success: function (data) {
                    $("#messageDetailsModal").html(data);
                }
            });

        });

        $(document).on('click', '.viewMore', function () {
            var uaId = $(this).attr('rel');

            $.ajax({
                url: '../../ViewUATransactionList',
                data: {'Type': 1, 'uaId': uaId},
                type: "GET",
                success: function (data) {
                    $("#messageDetailsModal").html(data);
                }
            });

        });

    });
});