/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



require(['./main'], function () {
    require(['jquery'], function($) {
        
        $("input:text,form").attr("autocomplete", "off");
        
        $('#searchBatchesBtn').click(function() { 
            $('#searchForm').submit();
        });

        $(document).on('click','.changePage', function() {
           $('#page').val($(this).attr('rel'));
           $('#searchForm').submit();
        });
        
        //this function will submit the batchId for viewing detailed audit report
        $(document).on('click', '.viewLink', function() {
        	$('input[name="batchId"]').val($(this).attr('rel'));
        	//somehow we need to submit form
        	$('#searchForm').attr('action', 'auditReport');
        	$('#searchForm').submit();
        });
        

        //This function will launch the new file upload overlay with a blank screen
        $(document).on('click', '.uploadFile', function() {
            $.ajax({
                url: 'fileUploadForm',
                type: "GET",
                success: function(data) {
                    $("#uploadFile").html(data);
                }
            });
        });

        //Function to submit file upload from the modal window.
        $(document).on('click', '#submitButton', function(event) {
            var errorFound = 0;

            //Remove any error message classes
            $('#configIdsDiv').removeClass("has-error");
            $('#configIdsMsg').removeClass("has-error");
            $('#configIdsMsg').html('');
            $('#uploadedFileDiv').removeClass("has-error");
            $('#uploadedFileMsg').removeClass("has-error");
            $('#uploadedFileMsg').html('');

            //Make sure at least one message type is selected
            if ($('#configIds').val() == '' || $('#configIds').val() == null) {
                $('#configIdsDiv').addClass("has-error");
                $('#configIdsMsg').addClass("has-error");
                $('#configIdsMsg').html('A message type must be selected.');
                errorFound = 1;
            }

            //Make sure a file is uploaded
            if ($('#uploadedFile').val() == '') {
                $('#uploadedFileDiv').addClass("has-error");
                $('#uploadedFileMsg').addClass("has-error");
                $('#uploadedFileMsg').html('The file is a required field.');
                errorFound = 1;
            }
            //Make sure the file is not an invalid format
            //(.jpg, .gif, .jpeg)
            if ($('#uploadedFile').val() != '' && ($('#uploadedFile').val().indexOf('.jpg') != -1 &&
                    $('#uploadedFile').val().indexOf('.jpeg') != -1 &&
                    $('#uploadedFile').val().indexOf('.gif') != -1 &&
                    $('#uploadedFile').val().indexOf('.pdf') != -1)) {

                $('#uploadedFileDiv').addClass("has-error");
                $('#uploadedFileMsg').addClass("has-error");
                $('#uploadedFileMsg').html('The uploaded file cannot be an image.');
                errorFound = 1;

            }

            if (errorFound == 1) {
                event.preventDefault();
                return false;
            }

            $('#fileUploadForm').submit();

        });

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
           "aaSorting" : [[4, "desc"]]
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

