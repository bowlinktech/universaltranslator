/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function($) {

        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewStatus', function() {
            $.ajax({
                url: '/frontend/viewStatus' + $(this).attr('rel'),
                type: "GET",
                success: function(data) {
                    $("#statusModal").html(data);
                }
            });
        });
    
        //This function will update the status of the batch when the download link is clicked.
        $(document).on('click', '.downloadFile', function(e) {
            var batchId = $(this).attr('rel');
            var fileName = $(this).attr('rel1');
            var orgId = $(this).attr('rel2');
            e.preventDefault();
            
            $.ajax({
                url: '/frontend/downloadBatch.do',
                type: 'POST',
                data: {'batchId': batchId},
                success: function(data) {

                   //Need to rewrite the status of the clicked batch
                   var statusHTML = '<a href="#statusModal" data-toggle="modal" class="btn btn-link viewStatus" rel="22" title="View this Status">SDL&nbsp;<span class="badge badge-help" data-placement="top" title="" data-original-title="">?</span></a>';
                   $('#statusDiv'+batchId).html(statusHTML);

                   var d = new Date();
                   var month = d.getMonth()+1;
                   var day = d.getDate();

                   var hourString;
                   var minString;
                   var amPm = "AM";
                   if ( d.getHours() > 11 ) {
                        amPm = "PM"
                        if(d.getHours() == 12) {
                            hourString = (d.getHours() - 0);
                        }
                        else {
                            hourString = (d.getHours() - 12);
                        }

                    } else {
                        amPm = "AM"
                        hourString = d.getHours();
                    }


                    if(d.getMinutes() < 10) {
                        minString = "0"+d.getMinutes();
                    }
                    else {
                        minString = d.getMinutes();
                    }

                   $('#lastDownloadDiv'+batchId).html(month + '/' + (day<10 ? '0' : '') + day + '/' + d.getFullYear() + '<br/>' + hourString + ':' + minString + ':' + d.getSeconds() + ' ' + amPm);
                   
                   window.location.href = "/FileDownload/downloadFile.do?filename="+fileName+"&foldername=output files&orgId="+orgId;
                   
                }
            });
            
             
        });

        $('#searchBatchesBtn').click(function() { 
            $('#searchForm').submit();
        });

        $(document).on('click','.changePage', function() {
           $('#page').val($(this).attr('rel'));
           $('#searchForm').submit();
        });
        
        var aoColumns = [
           null,
           null,
           null,
           null,
           null,
           { "bSortable" : false}
        ];
        
        $('#dataTable').dataTable({
            "sPaginationType": "bootstrap",
            "oLanguage": {
                "sSearch": "_INPUT_",
                "sLengthMenu": '<select class="form-control" style="width:150px">'+
                      '<option value="10">10 Records</option>'+
                      '<option value="20">20 Records</option>'+
                      '<option value="30">30 Records</option>'+
                      '<option value="40">40 Records</option>'+
                      '<option value="50">50 Records</option>'+
                      '<option value="-1">All</option>'+
                      '</select>'
            },
           "aoColumns" : aoColumns,
           "aaSorting" : [[3, "desc"]]
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



