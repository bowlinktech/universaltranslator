/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    $("input:text,form").attr("autocomplete", "off");

    $('#batchActivitiesDataTable').DataTable().destroy();

    $('#batchActivitiesDataTable').DataTable({
       bServerSide: false,
       bProcessing: false, 
       deferRender: true,
       iDisplayLength: -1,
       aaSorting: [[0,'asc']],
       sPaginationType: "bootstrap", 
       oLanguage: {
          sEmptyTable: "There were no files submitted for the selected date range.", 
          sSearch: "Filter Results: ",
          sLengthMenu: '<select class="form-control" style="width:150px">' +
               '<option value="10">10 Records</option>' +
               '<option value="20">20 Records</option>' +
               '<option value="30">30 Records</option>' +
               '<option value="40">40 Records</option>' +
               '<option value="50">50 Records</option>' +
               '<option value="-1">All</option>' +
               '</select>'
       },
       aoColumns: [
           {"width": "5%"},
           {"width": "80%",  "bSortable": false},
           {"width": "15%",  "bSortable": false}
       ]
   });

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