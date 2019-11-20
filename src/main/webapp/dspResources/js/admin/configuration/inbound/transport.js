

require(['./main'], function () {
    require(['jquery'], function ($) {
	
        $("input:text,form").attr("autocomplete", "off");
	
	//Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	//Selected transport method
	var transportMethod = $('#transportMethod').val();
	var helRegistryId = $('#helRegistryId').val();
	var helSchemaName = $('#helSchemaName').val();
	var messageTypeId = $('#messageTypeId').val();
	
        showCorrectFieldsByTransportMethod(transportMethod);
	
	//if the selected transport method is From a Health-e-Link Registry or going to a Health-e-Link registry
	//show the conifguration box
	if(messageTypeId == 1 &&transportMethod == 10 && helRegistryId > 0 && helSchemaName !== "") {
	    populateHELRegistryConfigs(helRegistryId,helSchemaName);
	}
	
	$('#transportMethod').change(function () {
            var methodId = $(this).val();
	    
	    if(methodId == 10 && helRegistryId > 0 && helSchemaName !== "") {
		populateHELRegistryConfigs(helRegistryId,helSchemaName);
		
		//If method == 10 (Coming from a HEL Registry online form preset the values
		$('#fileType').val(2);
		$('#fileExt').val('txt');
	    }
	    else {
		$('#helRegistryConfigDiv').hide();
		$('#helRegistryConfigId').find('option').remove().end().append('<option value="">- Select Registry Configuration -</option>').val('');
	    }
	    
	    showCorrectFieldsByTransportMethod(methodId);
        });
	
	$(document).on('change','#enableDirect', function() {
	    if($(this).is(':checked')) {
		$('.directDetails').removeClass('collapse');
	    }
	    else {
		$('.directDetails').addClass('collapse');
	    }
	});
	
	$(document).on('change','#enableRest', function() {
	    if($(this).is(':checked')) {
		$('.restDetails').removeClass('collapse');
	    }
	    else {
		$('.restDetails').addClass('collapse');
	    }
	});
	
	$(document).on('change','#enableFTP', function() {
	    if($(this).is(':checked')) {
		$('.ftpDetails').removeClass('collapse');
	    }
	    else {
		$('.ftpDetails').addClass('collapse');
	    }
	});
	
        $(document).on('change','#dmFindConfig',function() {
            if($(this).val() == 1) {
                $('.dmConfigKeywordDiv').show();
            }
            else {
                $('#dmConfigKeyword').val("");
                $('.dmConfigKeywordDiv').hide();
            }
        });

        //This function will save the messgae type field mappings
        $('#saveDetails').click(function () {
            $('#action').val('save');

            //Need to make sure all required fields are marked if empty.
            var hasErrors = 0;
            hasErrors = checkFormFields();
	    
            if (hasErrors == 0) {
                $('#transportDetails').submit();
            }
        });

        $('#next').click(function (event) {
            $('#action').val('next');

            var hasErrors = 0;
            hasErrors = checkFormFields();
	    
            if (hasErrors == 0) {
                $('#transportDetails').submit();
            }
        });

        //Set the default file extension when the file type is selected
        $('#fileType').change(function () {
            $('#ccdSampleDiv').hide();
            $('#hl7PDFSampleDiv').hide();
	    $('#jsonWrapperElementDiv').hide();
	    $('#fileDelimiterDiv').hide();
	    $('#lineTerminatortDiv').hide();
	    $('#encodingDiv').hide();
	    
            var fileType = $(this).val();
            $('#fileDelimiterDiv').show();
	    $('#lineTerminatortDiv').show();

            if (fileType == 2) {
                $('#fileExt').val('txt');
		$('#fileDelimiterDiv').show();
		$('#lineTerminatortDiv').show();
		$('#encodingDiv').show();
            } 
	    else if (fileType == 3) {
                $('#fileExt').val('csv');
		$('#fileDelimiterDiv').show();
		$('#lineTerminatortDiv').show();
		$('#encodingDiv').show();
            } 
	    else if (fileType == 4) {
                $('#fileExt').val('hr');
		$('#fileDelimiterDiv').show();
		$('#lineTerminatortDiv').show();
		$('#encodingDiv').show();
            } 
	    else if (fileType == 5) {
                $('#fileExt').val('mdb');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();
		$('#encodingDiv').hide();
            } 
	    else if (fileType == 6) {
                $('#fileExt').val('pdf');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();
		$('#encodingDiv').hide();
            } 
	    else if (fileType == 7) {
                $('#fileExt').val('odbc');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();
		$('#encodingDiv').hide();
            } 
	    else if (fileType == 8) {
                $('#fileExt').val('xls');
		$('#fileDelimiterDiv').show();
		$('#lineTerminatortDiv').show();
		$('#encodingDiv').show();
            } 
	    else if (fileType == 9) {
                $('#fileExt').val('xml');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();

            } 
	    else if (fileType == 10) {
                $('#fileExt').val('doc');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();
		$('#encodingDiv').hide();
	    } 
	    else if (fileType == 11) {
                $('#fileExt').val('xlsx');
		$('#fileDelimiterDiv').show();
		$('#lineTerminatortDiv').show();
		$('#encodingDiv').show();
            }
	    else if (fileType == 12) {
		$('#jsonWrapperElementDiv').show();
                $('#fileExt').val('json');
		$('#fileDelimiterDiv').hide();
		$('#lineTerminatortDiv').hide();
		$('#encodingDiv').hide();
            }
        });

	$('.zipped').change(function () {
	   if($(this).val() == 1) {
	       $('#zipTypeTopDiv').show();
	   }
	   else {
	       $('#zipTypeTopDiv').hide();
	   }
	});

    });
});


function showCorrectFieldsByTransportMethod(transportMethod) {
    $('#fileDetailsDiv').show();
   
    if(transportMethod == 13) {
	$('#fileDropDetailsDiv').show();
	$('#ftpDetailsDiv').show();
	$('#restDetailsDiv').show();
	$('#directMessageDetailsDiv').show();
    }
    else {
	$('#fileDropDetailsDiv').hide();
	$('#ftpDetailsDiv').hide();
	$('#restDetailsDiv').hide();
	$('#directMessageDetailsDiv').hide();
    }
}


function checkFormFields() {
    var hasErrors = 0;

    //Remove all has-error class
    $('div.form-group').removeClass("has-error");
    $('span.control-label').removeClass("has-error");
    $('span.control-label').html("");
    $('.alert-danger').hide();
    
    var type = $('#configType').attr('rel');
    
    var selMethodId = $('#transportMethod').val();
    var fileType = $('#fileType').val();
    
    //Make sure a transport method is chosen
    if ($('#transportMethod').val() === "") {
        $('#transportMethodDiv').addClass("has-error");
        $('#transportMethodMsg').addClass("has-error");
        $('#transportMethodMsg').html('The transport method is a required field.');
        hasErrors = 1;
    }
    
    if (selMethodId != 8) {
	//Make sure the error threshold is numeric and greater than 0
        if ($('#threshold').val() > 100 || !$.isNumeric($('#threshold').val())) {
            $('#thresholdDiv').addClass("has-error");
            $('#thresholdMsg').addClass("has-error");
            $('#thresholdMsg').html('The error threshold is a required field and must be a numeric value.');
            hasErrors = 1;
        }
    }
    
    //Make sure the file size is numeric and greate than 0
    if ($('#maxFileSize').val() <= 0 || !$.isNumeric($('#maxFileSize').val())) {
	$('#maxFileSizeDiv').addClass("has-error");
	$('#maxFileSizeMsg').addClass("has-error");
	$('#maxFileSizeMsg').html('The max file size is a required field and must be a numeric value.');
	hasErrors = 1;
    }

    //Make sure the file type is selected
    if ($('input[type="radio"][name="zipped"]:checked').val() === "1") {
	if($('#zipType').val() === "") {
	    $('#zipTypeDiv').addClass("has-error");
	    $('#zipTypeMsg').addClass("has-error");
	    $('#zipTypeMsg').html('The file will be zipped, the zip type is a required field.');
	    hasErrors = 1;
	}
    }
	
    //Make sure the file type is selected
    if ($('#fileType').val() === "") {
	$('#fileTypeDiv').addClass("has-error");
	$('#fileTypeMsg').addClass("has-error");
	$('#fileTypeMsg').html('The file type is a required field.');
	hasErrors = 1;
    }

    //Make sure the file ext is entered
    if ($('#fileExt').val() === "") {
	$('#fileExtDiv').addClass("has-error");
	$('#fileExtMsg').addClass("has-error");
	$('#fileExtMsg').html('The file extension is a required field.');
	hasErrors = 1;
    }

    //make sure encoding is selected
    if ($('#encodingId').val() === "") {
	$('#encodingDiv').addClass("has-error");
	$('#encodingMsg').addClass("has-error");
	$('#encodingMsg').html('Encoding is a required field.');
	hasErrors = 1;
    } 
    else {
	//Remove any '.' in the extension
	$('#fileExt').val($('#fileExt').val().replace('.', ''));
    }

    //Make sure the file delimiter is selected
    if ($('#fileDelimiter').val() === "") {
	$('#fileDelimiterDiv').addClass("has-error");
	$('#fileDelimiterMsg').addClass("has-error");
	$('#fileDelimiterMsg').html('The file delimiter is a required field.');
	hasErrors = 1;
    }
	
    if ($('#enableFTP').is(':checked')) {
	var IPReg = /^(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])\.(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])\.(\d\d?)|(1\d\d)|(0\d\d)|(2[0-4]\d)|(2[0-5])$/;

	if ($('#ip1').val() === "") {
	    $('#ip1Div').addClass("has-error");
	    $('#ip1Msg').addClass("has-error");
	    $('#ip1Msg').html('The IP address is a required field.');
	    hasErrors = 1;
	} else if (!IPReg.test($('#ip1').val())) {
	    $('#ip1Div').addClass("has-error");
	    $('#ip1Msg').addClass("has-error");
	    $('#ip1Msg').html('The IP address entered is invalid.');
	    hasErrors = 1;
	}
	if ($('#username1').val() === "") {
	    $('#username1Div').addClass("has-error");
	    $('#username1Msg').addClass("has-error");
	    $('#username1Msg').html('The username is a required field.');
	    hasErrors = 1;
	}

	if ($('#protocol1').val() === "SFTP") {
	    if ($('#password1').val() === "" && $('#file1').val() === "" && $('#certification1').val() === "") {
		$('#password1Div').addClass("has-error");
		$('#password1Msg').addClass("has-error");
		$('#password1Msg').html('The password or certification is a required field.');
		hasErrors = 1;
	    }
	} else {
	    if ($('#password1').val() === "") {
		$('#password1Div').addClass("has-error");
		$('#password1Msg').addClass("has-error");
		$('#password1Msg').html('The password is a required field.');
		hasErrors = 1;
	    }
	}


	if ($('#ftpdirectory1').val() === "") {
	    $('#directory1Div').addClass("has-error");
	    $('#directory1Msg').addClass("has-error");
	    $('#directory1Msg').html('The directory is a required field.');
	    hasErrors = 1;
	}
    }
    else {
	$('#ip1').val("");
	$('#username1').val("");
	$('#password1').val("");
	$('#ftpdirectory1').val("");
    }

    if ($('#enableRest').is(':checked')) {
	var apiURL = $('#restAPIURL').val();
	var apiUsername = $('#restAPIUsername').val();
	var apiPassword = $('#restAPIPassword').val();
	var apiType = $('#restAPIType').val();
	var apiFunction = $('#restAPIFunctionId').val();
	    
	if(apiURL === "") {
	    $('#apiURLDiv').addClass("has-error");
	    $('#apiURLMsg').addClass("has-error");
	    $('#apiURLMsg').html('The Rest API URL is a required field.');
	    hasErrors = 1;
	}

	if(apiUsername === "") {
	    $('#apiUsernameDiv').addClass("has-error");
	    $('#apiUsernameMsg').addClass("has-error");
	    $('#apiUsernameMsg').html('The Rest API Username is a required field.');
	    hasErrors = 1;
	}
	else if(apiUsername.length < 5) {
	    $('#apiUsernameDiv').addClass("has-error");
	    $('#apiUsernameMsg').addClass("has-error");
	    $('#apiUsernameMsg').html('The Rest API Username must be at least 5 characters.');
	    hasErrors = 1;
	}

	if(apiPassword === "") {
	    $('#apiPasswordDiv').addClass("has-error");
	    $('#apiPasswordMsg').addClass("has-error");
	    $('#apiPasswordMsg').html('The Rest API Password is a required field.');
	    hasErrors = 1;
	}
	else if(apiPassword.length < 5) {
	    $('#apiPasswordDiv').addClass("has-error");
	    $('#apiPasswordMsg').addClass("has-error");
	    $('#apiPasswordMsg').html('The Rest API Password must be at least 5 characters.');
	    hasErrors = 1;
	}

	if(apiType === "") {
	    $('#restAPITypeDiv').addClass("has-error");
	    $('#restAPITypeMsg').addClass("has-error");
	    $('#restAPITypeMsg').html('The Rest API Type is a required field.');
	    hasErrors = 1;
	}

	if(apiFunction === "") {
	    $('#restAPIFunctionIdDiv').addClass("has-error");
	    $('#restAPIFunctionIdMsg').addClass("has-error");
	    $('#restAPIFunctionIdMsg').html('The Rest API Function is a required field.');
	    hasErrors = 1;
	}
	    
	//Check for a duplicate url for rest api
	if(hasErrors != 1) {

	} 
    }
    else {
	$('#restAPIURL').val("");
	$('#restAPIUsername').val("");
	$('#restAPIPassword').val("");
    }
    
    if ($('#enableDirect').is(':checked')) {
	if($('#directDomain').val() === "") {
	    $('.directDomainDiv').addClass("has-error");
	    $('#directDomainMsg').addClass("has-error");
	    $('#directDomainMsg').html('This is a required field.');
	    hasErrors = 1;
	}

	if($('#dmFindConfig').val() === "") {
	    $('.dmFindConfigDiv').addClass("has-error");
	    $('#dmFindConfigMsg').addClass("has-error");
	    $('#dmFindConfigMsg').html('This is a required field.');
	    hasErrors = 1;
	}

	if($('#dmFindConfig').val() == 1 && $('#dmConfigKeyword').val() === "") {
	    $('.dmConfigKeywordDiv').addClass("has-error");
	    $('#dmConfigKeywordMsg').addClass("has-error");
	    $('#dmConfigKeywordMsg').html('This is a required field.');
	    hasErrors = 1;
	}
    }
    else {
	$('#directDomain').val("");
	$('#dmFindConfig').val("");
	$('#dmConfigKeyword').val("");
    }

    return hasErrors;
}

function populateHELRegistryConfigs(helRegistryId,helRegistrySchemaName) {
    
    $.ajax({
	url: '/administrator/configurations/getHELRegistryConfigurations?tenantId='+helRegistrySchemaName,
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
