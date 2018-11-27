


require(['./main'], function () {
    require(['jquery'], function ($) {
	
	if ($('#dataTable').length) {

	    var oSettings = datatable.fnSettings();

	    datatable.fnSort([[6, 'desc']]);
	}

        $.ajaxSetup({
            cache: false
        });

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
        $("input:text,form").attr("autocomplete", "off");

        //This function will launch the new watch entry overlay with a blank screen
        $(document).on('click', '#createNewWatchEntry', function () {
            $.ajax({
                url: 'createWatchEntry',
                type: "GET",
                success: function (data) {
                    $("#watchEntryModal").html(data);
                }
            });
        });
	
	$(document).on('click', '.deleteWatchEntry', function() {
            
            var watchId = $(this).attr('rel');
           
            if(confirm("Are you sure you want to remove this watch entry?")) {
                
                $('body').overlay({
                    glyphicon : 'floppy-disk',
                    message : 'Deleting...'
                });
                
                $.ajax({
                    url: 'deleteWatchEntry',
                    data: {
                        'watchId': watchId
                    },
                    type: 'POST',
                    success: function(data) {
                       location.reload();
                    }
                });
                
            }
            
        });

        //This function will launch the edit watch entry overlay
        $(document).on('click', '.watchEntryEdit', function () {
            var entryId = $(this).attr('rel');
            $.ajax({
                url: 'editWatchEntry',
                type: "GET",
                data: {
		    'entryId': entryId
		},
                success: function (data) {
		    
		    data = $(data);
		   
                    $("#watchEntryModal").html(data);
                }
            });
        });
	
	//Go get the existing message types for the selected organization'
        $(document).on('change', '.selOrganization', function () {
            var selOrg = $(this).val();

            if (selOrg === '') {
                $('#orgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg);
            }
        });
	
	//This function will save the messgae type field mappings
        $(document).on('click', '#submitButton', function () {
            var hasErrors = 0;
	    var msg = $('#entryMessage').val();
	    var org = $('#organization').val();
            var config = $('#config').val();

            $('div.form-group').removeClass("has-error");
            $('span.control-label').removeClass("has-error");
            $('span.control-label').html("");
            $('.alert-danger').hide();
	    
	    if($('.tab-pane.active').attr("id") == 'generic') {
		if (msg === '') {
		    $('#entryMessageDiv').addClass("has-error");
		    hasErrors = 1;
		}
		if ($('#expectFirstFile').val() === '') {
		    $('#expectFirstFileDiv').addClass("has-error");
		    hasErrors = 1;
		}
	    }
	    else {
		if (org === '') {
		    $('#orgDiv').addClass("has-error");
		    hasErrors = 1;
		}

		if (config === '') {
		    $('#configDiv').addClass("has-error");
		    hasErrors = 1;
		}
		if ($('#expectFirstFileMT').val() === '') {
		    $('#expectFirstFileMTDiv').addClass("has-error");
		    hasErrors = 1;
		}
	    }
	    

            if (hasErrors == 0) {
		
		if($('.tab-pane.active').attr("id") == 'generic') {
		    //Check to see if a time has been selected
		    if($('#expectedTimeHour').val() != "" && 
			$('#expectedTimeMinute').val() != "" && 
			$('#expectedTimeHAMPM').val() != "") {

			var selectedExpectedTime = $('#expectedTimeHour').val()+":"+$('#expectedTimeMinute option:selected').text()+" "+$('#expectedTimeAMPM').val();
			$('#expectFirstFileTime').val(selectedExpectedTime);
		    }
		    $('#watchListGenericEntryForm').submit();
		}
		else {
		    //Check to see if a time has been selected
		    if($('#expectedTimeHourMT').val() != "" && 
			$('#expectedTimeMinuteMT').val() != "" && 
			$('#expectedTimeHAMPMMT').val() != "") {

			var selectedExpectedTime = $('#expectedTimeHourMT').val()+":"+$('#expectedTimeMinuteMT option:selected').text()+" "+$('#expectedTimeAMPMMT').val();
			$('#expectFirstFileTimeMT').val(selectedExpectedTime);
		    }
		    $('#watchListEntryForm').submit();
		}
            }

        });

    });
});

function populateConfigurations(orgId) {

    var currConfigId = $('#config').attr('rel');

    $.ajax({
        url: '/administrator/configurations/getAvailableConfigurations.do',
        type: "GET",
        data: {'orgId': orgId},
        success: function (data) {
            //get value of preselected col
            var html = '<option value="">- Select - </option>';
            var len = data.length;

            for (var i = 0; i < len; i++) {
                if (data[i].id == currConfigId) {
                    html += '<option value="' + data[i].id + '" selected>' + data[i].configName +  '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                } else {
                    html += '<option value="' + data[i].id + '">' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                }
            }
            $('#config').html(html);
        }
    });
}
