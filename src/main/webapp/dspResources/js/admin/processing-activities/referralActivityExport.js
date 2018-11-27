/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        $(document).on('click', '.createExport', function () {
            $('body').overlay({
                glyphicon: 'floppy-disk',
                message: 'Creating Export...'
            });
            $('#searchForm').submit();
        });

        var oSettings = datatable.fnSettings();

        //datatable.fnSort( [ [0,'desc'] ] );
    });
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);



}