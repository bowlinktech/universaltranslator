
require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	//Hide the Configuration Type if a target configuration type
	$('.type').change(function(event) {
	   $('#organization').val("");
	   $('#askTargetQuestion1').hide();
	   $('#askTargetQuestion2').hide();
	   $('#helRegistryConfigDiv').hide();
	   $('#messageTypeDiv').hide();
	  
	   if($(this).val() == 2) {
	       $('#configurationTypeDiv').hide();
	       $('#sourceTypeDiv').hide();
	   } 
	   else {
	       $('#configurationTypeDiv').show();
	       $('#sourceTypeDiv').show();
	   }
	});
	
	$('.basedOffHELConfig').change(function(event) {
	    
	    if($(this).val() == 0) {
		$('#askTargetQuestion2').hide();
		$('#helRegistryConfigDiv').hide();
		$('#messageTypeDiv').show();
		$('#helRegistry').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');
		$('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');
	    }
	    else {
		$('#messageTypeDiv').hide();
		$('#askTargetQuestion2').show();
		
		$.ajax({
		    url: '/administrator/organizations/getHELRegistries?tenantId=registries',
		    type: "GET",
		    data: {},
		    dataType: 'json',
		    success: function (data) {
			
			var helRegistrySelect = $('#helRegistry');

			$.each(data, function(index) {
			   helRegistrySelect.append($('<option></option>').val(data[index][0]+'-'+data[index][2]).html(data[index][1]));
			});
		    }
		});
	    }
	});
	
	//Registry is selected we need to go get the organizations set up for that organization
	$(document).on('change','#helRegistry', function() {
	    var helRegistryId = $(this).val().split("-")[0];
	    var helRegistrySchemaName = $(this).val().split("-")[1];
	    $('#savedhelRegistryId').val(helRegistryId);
	    $('#savedhelSchemaName').val(helRegistrySchemaName);
	    populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName);
	});
	
	var helRegistryId = $('option:selected', '#organization').attr('helRegistryId');
	var helRegistrySchemaName = $('option:selected', '#organization').attr('helRegistrySchemaName');
	
	var helRegistryIdOnConfig = $('.savedOrgId').attr('helRegistryId');
	var helRegistrySchemaNameOnConfig = $('.savedOrgId').attr('helRegistrySchemaName');
	
	if((helRegistryId > 0 && helRegistrySchemaName !== '') || (helRegistryIdOnConfig > 0 && helRegistrySchemaNameOnConfig !== '')) {
	     if(helRegistryId > 0 && helRegistrySchemaName !== '') {
		$('#savedhelRegistryId').val(helRegistryId);
		$('#savedhelSchemaName').val(helRegistrySchemaName);
		populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName);
	     }
	     else {
		 $('#savedhelRegistryId').val(helRegistryIdOnConfig);
		 $('#savedhelSchemaName').val(helRegistrySchemaNameOnConfig);
		 $('#askTargetQuestion1').show();
		 $('#basedOffHELConfig_1').attr('checked',true);
		 $('#askTargetQuestion2').show();
		 
		 populateHELRegistryConfigs(helRegistryIdOnConfig,helRegistrySchemaNameOnConfig);
		 $.ajax({
		    url: '/administrator/organizations/getHELRegistries?tenantId=registries',
		    type: "GET",
		    data: {},
		    dataType: 'json',
		    success: function (data) {
			
			var helRegistrySelect = $('#helRegistry');

			$.each(data, function(index) {
			   if(helRegistryIdOnConfig == data[index][0]) {
			       helRegistrySelect.append($('<option selected></option>').val(data[index][0]+'-'+data[index][2]).html(data[index][1]));
			   }
			   else {
			       helRegistrySelect.append($('<option></option>').val(data[index][0]+'-'+data[index][2]).html(data[index][1]));
			   }
			});
		    }
		});
	     }
	}
	
	$('#helRegistryOrgId').change(function(event) {
	    populateHELRegistryConfigs($(this).val(),$('#helRegistry').val());
	});
	
	$('#organization').change(function(event) {
	    $('#askTargetQuestion1').hide();
	    $('#askTargetQuestion2').hide();
	    $('#helRegistryConfigDiv').hide();
	   
	    var helRegistryId = $('option:selected', this).attr('helRegistryId');
	    var helRegistrySchemaName = $('option:selected', this).attr('helRegistrySchemaName');
	    
	    //If the selected organization is a health-e-link registtry
	    //then we need to get hte list of configurations set up
	    //for this registry
	    if(helRegistryId > 0 && helRegistrySchemaName !== '') {
		populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName);
	    }
	    else {
		
		if($('.type:checked').val() == 2) {
		    $('#askTargetQuestion1').show();
		}
		else {
		    $('#helRegistryConfigDiv').hide();
		    $('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');
		    $('#messageTypeDiv').show();
		}
		
	    }
	});
	
	
	/*//get the configuration type value
	var configTypeVal = $(".configurationType:checked").val();
	
	//If passThru hide the message type value, it is not needed for a passthru configuration
	if(configTypeVal == 2) {
	    $('#messageTypeOuterDiv').hide();
	    $('#helRegistryConfigOuterDiv').hide();
	}
	else {
	    $('#messageTypeOuterDiv').show();
	    $('#helRegistryConfigOuterDiv').hide();
	}
	
	
        $('.configurationType').change(function (event) {
	    $("#messageTypeId").val($("#messageTypeId option:first").val());
            if ($(this).val() == 2) {
		$('#messageTypeOuterDiv').hide();
		$('#helRegistryConfigOuterDiv').hide();
            } else {
                $('#messageTypeOuterDiv').show();
		$('#helRegistryConfigOuterDiv').show();
            }
        });*/

        $('#saveDetails').click(function (event) {
            $('#action').val('save');
            var hasErrors = 0;
            hasErrors = checkform();
            if (hasErrors == 0) {
                $("#configuration").submit();
            }

        });

        $('#next').click(function (event) {
            $('#action').val('next');
            var hasErrors = 0;
            hasErrors = checkform();
            if (hasErrors == 0) {
                $("#configuration").submit();
            }
        });

    });
});

function populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName) {
    $('#messageTypeDiv').hide();
		
    $.ajax({
	url: 'getHELRegistryConfigurations?tenantId='+helRegistrySchemaName,
	type: "GET",
	data: {},
	dataType: 'json',
	success: function (data) {
	    $('#helRegistryConfigDiv').show();
	    $('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');

	    var selHELRegistryConfigId = $('#helRegistryConfigId').attr('rel');

	    var helRegistryConfigSelect = $('#helRegistryConfigId');

	    $.each(data, function(index) {
	       if(data[index].id == selHELRegistryConfigId) {
		   helRegistryConfigSelect.append($('<option selected></option>').val(data[index].id).html(data[index].name));
	       }
	       else {
		   helRegistryConfigSelect.append($('<option></option>').val(data[index].id).html(data[index].name));
	       }
	    });
	}
    });
}


function checkform() {
    var errorFound = 0;

    //Check to make sure an organization is selected
    if ($('#organization').val() === '') {
        $('#orgDiv').addClass("has-error");
        $('#configOrgMsg').addClass("has-error");
        $('#configOrgMsg').html('The organization is a required field.');
        errorFound = 1;
    }
    
    var helRegistryId = $('option:selected','#organization').attr('helRegistryId');
    
    //Check to make sure a message type is selected
    if(helRegistryId > 0) {
	if($('#helRegistryConfigId').val() === '') {
	    $('#helRegistryConfigDiv').addClass("has-error");
	    $('#helRegistryConfigIdMsg').addClass("has-error");
	    $('#helRegistryConfigIdMsg').html('The HEL registry configuration is a required field.');
	}
    }
    else {
	
	if($('#helRegistry').val() != 0) {
	    if ($('#helRegistryConfigId').val() === '') {
		$('#helRegistryConfigDiv').addClass("has-error");
		$('#helRegistryConfigIdMsg').addClass("has-error");
		$('#helRegistryConfigIdMsg').html('The HEL registry configuration is a required field.');
		errorFound = 1;
	    }
	}
	else {
	    if ($('#messageTypeId').val() === '') {
		$('#messageTypeDiv').addClass("has-error");
		$('#configMessageTypeMsg').addClass("has-error");
		$('#configMessageTypeMsg').html('The message type is a required field.');
		errorFound = 1;
	    }
	}
    }
    

    //Check to make sure a configuration name is entered
    if ($('#configName').val() === '') {
        $('#configNameDiv').addClass("has-error");
        $('#configNameMsg').addClass("has-error");
        $('#configNameMsg').html('The configuration name is a required field.');
        errorFound = 1;
    }
    
    return errorFound;

}


