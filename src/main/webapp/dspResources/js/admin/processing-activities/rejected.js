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
            "columns": [
                { "width": "20%" },
                { "width": "20%" },
                { "width": "18%" },
                { "width": "10%" },
                { "width": "15%" },
                { "width": "12%" },
                { "width": "5%" }
             ],
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
    
    $(document).on('click', '.deleteTransactions', function() {

        var batchName = $(this).attr('rel');

        if(confirm("Are you sure you want to remove this batch?")) {

            $('body').overlay({
                glyphicon : 'floppy-disk',
                message : 'Deleting...'
            });

            $.ajax({
                url: 'deleteBatch.do',
                data: {
                    'batchName': batchName
                },
                type: 'POST',
                success: function(data) {
                   location.reload();
                }
            });

        }

    });
    
    $(document).ready(function() {
         var isDST = $('#DTS').val();
         
         if(isDST === '') {
            //CHeck if daylight savings time
            Date.prototype.stdTimezoneOffset = function () {
                var jan = new Date(this.getFullYear(), 0, 1);
                var jul = new Date(this.getFullYear(), 6, 1);
                return Math.max(jan.getTimezoneOffset(), jul.getTimezoneOffset());
            }

            Date.prototype.isDstObserved = function () {
                return this.getTimezoneOffset() < this.stdTimezoneOffset();
            }

            var today = new Date();
            var isDST = 0;
            if (today.isDstObserved()) { 
               isDST = 1;
            }
            $('#DTS').val(isDST);
            searchByDateRange();
         }
         
    });
    
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');
    
    if(!fromDate) {
       fromDate = $('#fromDate').attr('rel');
   }
   
   if(!toDate) {
       toDate = $('#toDate').attr('rel');
   }

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}
