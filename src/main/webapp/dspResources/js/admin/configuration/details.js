
require(['./main'], function () {
        
    $(document).on('click','.printConfig',function() {
      /* $('body').overlay({
           glyphicon : 'print',
           message : 'Gathering Details...'
       });*/

       var configId = $(this).attr('rel');

       $.ajax({
           url: 'createConfigPrintPDF.do',
           data: {
               'configId': configId
           },
           type: "GET",
           dataType : 'text',
           contentType : 'application/json;charset=UTF-8',
           success: function(data) {
               if(data !== '') {
                   window.location.href = '/administrator/configurations/printConfig/'+ data;
                   $('#successMsg').show();
                   //$('#dtDownloadModal').modal('toggle');
               }
               else {
                   $('#errorMsg').show();
               }
           }
       });
   });

   $("input:text,form").attr("autocomplete", "off");

   //Fade out the updated/created message after being displayed.
   if ($('.alert').length > 0) {
       $('.alert').delay(2000).fadeOut(1000);
   }

   //Hide the Configuration Type if a target configuration type
   $('.type').change(function(event) {

      if($(this).val() == 2) {
          $('#configurationTypeDiv').hide();
      } 
      else {
          $('#configurationTypeDiv').show();
      }
   });


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
    
    var selectedType = $('.type:checked').val();
    
    //Check to make sure an organization is selected
    if ($('#organization').val() === '') {
        $('#orgDiv').addClass("has-error");
        $('#configOrgMsg').addClass("has-error");
        $('#configOrgMsg').html('The organization is a required field.');
        errorFound = 1;
    }
    
    //Check to make sure a configuration name is entered
    if ($('#configName').val() === '') {
        $('#configNameDiv').addClass("has-error");
        $('#configNameMsg').addClass("has-error");
        $('#configNameMsg').html('The configuration name is a required field.');
        errorFound = 1;
    }
    else {
        //Check to see if organization name contains any special characters
        var configName = $('#configName').val();
        if(/^[a-zA-Z0-9- ]*$/.test(configName) == false) {
            $('#configNameDiv').addClass("has-error");
            $('#configNameMsg').addClass("has-error");
            $('#configNameMsg').html('The configuration name contains illegal characters. Only letters and numbers are accepted.');
            errorFound = 1;
        }
    }
    
    return errorFound;
}


