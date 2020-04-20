/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {

    $("input:text,form").attr("autocomplete", "off");

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

        var searchTerm = $('#rejected-table').attr('term');

        $('#rejected-table').DataTable().destroy();

        $('#rejected-table').DataTable({
            bServerSide: false,
            bProcessing: true, 
            deferRender: true,
            aaSorting: [[5,'desc']],
            "oSearch": {"sSearch": searchTerm },
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
                    '</select>',
                sProcessing: "<div style='background-color:#64A5D4; height:50px; margin-top:200px'><p style='color:white; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
            }
        });
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}
