

require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	var selSrcOrgId = $('.selSendingOrganization').val();
	if(selSrcOrgId > 0) {
	     populateConfigurations(selSrcOrgId, 'srcConfig');
	}
	
	var selTgtOrgId = $('.seltgtOrganization').val();
	if(selTgtOrgId > 0) {
	     populateConfigurations(selTgtOrgId, 'tgtConfig');
	}

        //Go get the existing message types for the selected organization'
        $(document).on('change', '.selSendingOrganization', function () {
            var selOrg = $(this).val();
            var connectionId = $('#connectionId').val();
	    
	    $('#srcorgDiv').removeClass('has-error');
	    $('#srcOrgMsg').html("");

            if (selOrg === '') {
                $('#srcorgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg, 'srcConfig');
                //populateUsers(selOrg, 'srcContactsTable', connectionId);
            }
        });

        //Go get the existing message types for the selected organization
        $(document).on('change', '.seltgtOrganization', function () {
            var selOrg = $(this).val();
            var connectionId = $('#connectionId').val();
	    
	    $('#tgtorgDiv').removeClass('has-error');
	    $('#tgtOrgMsg').html("");

            if (selOrg === '') {
                $('#tgtorgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg, 'tgtConfig');
                //populateUsers(selOrg, 'tgtContactsTable', connectionId);
            }
        });
	
	//Go get the existing message types for the selected organization
        $(document).on('change', '#srcConfig', function () {
	    var selConfigId = $(this).val();
	    
	    $('#srcConfigDiv').removeClass('has-error');
	    $('#srcConfigMsg').html("");
	    
	    if(selConfigId !== '') {
		populateConfigurationDataElements(selConfigId,'src');
	    }
	    else {
		$('.').hide();
	    }
	});
	
	//Go get the existing message types for the selected organization
        $(document).on('change', '#tgtConfig', function () {
	    var selConfigId = $(this).val();
	    
	    $('#tgtConfigDiv').removeClass('has-error');
	    $('#tgtConfigMsg').html("");
	    
	    if(selConfigId !== '') {
		populateConfigurationDataElements(selConfigId,'tgt');
	    }
	    else {
		$('.').hide();
	    }
	});
	

        //This function will save the messgae type field mappings
        $(document).on('click', '#saveDetails', function () {
            
	    var selectedSourceConfig = $('#srcConfig').val();
	    var selectedTargetConfig = $('#tgtConfig').val();
	    var connectionId = $('#connectionId').val();
	    
	    var mappingArray = [];
	    
	    var errorFound = 0;
	    
	    $('.matchField').each(function() {
		var targetFieldNo = $(this).attr('fieldNo');
		var targetFieldDesc = $(this).attr('fieldDesc');
		var targetUseField = $('#useField'+targetFieldNo).is(':checked');
		var matchingField = $(this).val();
		
		if(matchingField == 0 && targetUseField) {
		    $('#matchField'+targetFieldNo).addClass('has-error');
		    errorFound = 1;
		}
		mappingArray.push(targetFieldNo+'|'+targetFieldDesc+'|'+targetUseField+'|'+matchingField);
		
	    });
	    
	    if($('.selSendingOrganization').val() === '') {
		$('#srcorgDiv').addClass('has-error');
		$('#srcOrgMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(selectedSourceConfig === '') {
		$('#srcConfigDiv').addClass('has-error');
		$('#srcConfigMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if($('.seltgtOrganization').val() === '') {
		$('#tgtorgDiv').addClass('has-error');
		$('#tgtOrgMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(selectedTargetConfig === '') {
		$('#tgtConfigDiv').addClass('has-error');
		$('#tgtConfigMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(errorFound == 0) {
		if(mappingArray.length > 0) {
		    $.ajax({
			url: 'saveConnectionElementMappings',
			type: "POST",
			data: {
			    'connectionId': connectionId,
			    'sourceConfigId': selectedSourceConfig,
			    'targetConfigId': selectedTargetConfig,
			    'mappedFields': mappingArray
			},
			success: function (data) {
			    window.location.replace('/administrator/configurations/connections/details?i='+data+'&msg=saved');
			}
		    });
		}
	    }
	    
        });
	
	//This function will save the messgae type field mappings
        $(document).on('click', '#saveCloseDetails', function () {
            
	    var selectedSourceConfig = $('#srcConfig').val();
	    var selectedTargetConfig = $('#tgtConfig').val();
	    var connectionId = $('#connectionId').val();
	    
	    var mappingArray = [];
	    
	     var errorFound = 0;
	    
	    $('.matchField').each(function() {
		var targetFieldNo = $(this).attr('fieldNo');
		var targetFieldDesc = $(this).attr('fieldDesc');
		var targetUseField = $('#useField'+targetFieldNo).is(':checked');
		var matchingField = $(this).val();
		
		if(matchingField == 0 && targetUseField) {
		    $('#matchField'+targetFieldNo).addClass('has-error');
		    errorFound = 1;
		}
		
		mappingArray.push(targetFieldNo+'|'+targetFieldDesc+'|'+targetUseField+'|'+matchingField);
		
	    });
	    
	    if($('.selSendingOrganization').val() === '') {
		$('#srcorgDiv').addClass('has-error');
		$('#srcOrgMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(selectedSourceConfig === '') {
		$('#srcConfigDiv').addClass('has-error');
		$('#srcConfigMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if($('.seltgtOrganization').val() === '') {
		$('#tgtorgDiv').addClass('has-error');
		$('#tgtOrgMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(selectedTargetConfig === '') {
		$('#tgtConfigDiv').addClass('has-error');
		$('#tgtConfigMsg').html("This is a required field.");
		errorFound = 1;
	    }
	    
	    if(errorFound == 0) {
	    
		if(mappingArray.length > 0) {
		    $.ajax({
			url: 'saveConnectionElementMappings',
			type: "POST",
			data: {
			    'connectionId': connectionId,
			    'sourceConfigId': selectedSourceConfig,
			    'targetConfigId': selectedTargetConfig,
			    'mappedFields': mappingArray
			},
			success: function (data) {
			    window.location.replace('/administrator/configurations/connections?msg=saved');
			}
		    });
		}
	    }
        });

        $(document).on('change', '#sendAllSourceContacts', function () {
	   
            if ($(this).is(":checked")) {
                $('.srcEmailNotifications').each(function () {
                    $(this).prop('checked', true);
                });
            }
	    else {
                $('.srcEmailNotifications').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });

        $(document).on('change', '#sendAllTargetContacts', function () {
	   
            if ($(this).is(":checked")) {
                $('.tgtEmailNotifications').each(function () {
                    $(this).prop('checked', true);
                });
            } 
	    else {
                $('.tgtEmailNotifications').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });
    });
});

function populateConfigurations(orgId, selectBoxId) {

    var currConfigId = $('#' + selectBoxId).attr('rel');
    var found = 0;
    
    $.ajax({
        url: 'getAvailableConfigurations.do',
        type: "GET",
        data: {
	    'orgId': orgId,
	    'selectBoxId': selectBoxId
	},
        success: function (data) {
            //get value of preselected col
            var html = '<option value="">- Select - </option>';
            var len = data.length;

            for (var i = 0; i < len; i++) {
                if (data[i].id == currConfigId) {
		    found = 1;
                    html += '<option value="' + data[i].id + '" selected>' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
		    
                } else {
                    html += '<option value="' + data[i].id + '">' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                }
            }
	    
	    if(found == 1 && selectBoxId === 'srcConfig') {
		populateConfigurationDataElements(currConfigId,'src');
	    }
	    else if (found == 1 && selectBoxId === 'tgtConfig') {
		populateConfigurationDataElements(currConfigId,'tgt');
	    }
	    
            $('#' + selectBoxId).html(html);
        }
    });
}

function populateUsers(orgId, selectBoxId, connectionId) {

    var users = $('#' + selectBoxId).attr('rel');

    var url = "";
    
    if (selectBoxId === "srcContactsTable") {
        url = "getAvailableSendingContacts.do";
    } else {
        url = "getAvailableReceivingContacts.do";
    }

    $.ajax({
        url: url,
        type: "GET",
        data: {
	    'orgId': orgId, 
	    'connectionId': connectionId
	},
        success: function (data) {
            $('#' + selectBoxId).html(data);
        }
    });
}


function populateConfigurationDataElements(configId, section) {
    
    var sourceConfigId = 0;
    
    if(section === 'tgt') {
	sourceConfigId = $('#srcConfig').val();
	if(sourceConfigId === '' && $('#connectionId').val() > 0) {
	    sourceConfigId = $('#srcConfig').attr('rel');
	}
    }
    
    if(section === 'tgt' && sourceConfigId === '') {
	$('#srcConfigDiv').addClass('has-error');
	$('#srcConfigMsg').html("This is a required field.");
	$('#tgtConfig').val('');
    }
    else {
    
	$.ajax({
	    url: "getConfigurationDataElements",
	    type: "GET",
	    data: {
		'selConfigId': configId, 
		'sourceConfigId': sourceConfigId,
		'connectionId': $('#connectionId').val(),
		'section': section
	    },
	    success: function (data) {

		if(section === 'src') {
		     $('.sourceDataElements').html(data);
		     $('.dataElementDivs').show();
		     $('.sourceDataElementDiv').show();
		}
		else {
		     $('.targetDataElements').html(data);
		     $('.dataElementDivs').show();
		     $('.targetDataElementDiv').show();
		}
	    }
	});
    }
    
}

