

require(['./main'], function () {
    require(['jquery'], function ($) {
        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert-success').length > 0) {
            $('.alert-success').delay(2000).fadeOut(1000);
        }

        //If any field changes need to show the message in red that nothign
        //will be saved unless teh "Saved" button is pressed
        $(document).on('change', '.formField', function () {
            $('#saveMsgDiv').show();
        });
	
	var isHELConfiguration = $('.templateFields').attr('rel');
	
	if(isHELConfiguration) {
	    loadHELConfigurationFields();
	}
	
	$('#loadConfigurationFields').click(function(event) {
	   loadHELConfigurationFields();
	});
	
	/*$('.matchingField').change(function() {
	   var fieldNo = $(this).attr('fieldNo');
	   var validationId = $(this).val().split('-')[3];
	   
	   if((validationId*1) > 0) {
	       $('#validation_'+fieldNo).val(validationId);
	   }
	});*/
	
	$('.useField').change(function() {
	    if(!$(this).prop( "checked" )) {
		var fieldNo = $(this).attr('fieldNo');
		$('#validation_'+fieldNo).val(1);
		$('#match_'+fieldNo).val(0);
	    }
	    
	});

       
        //This function will save the messgae type field mappings
        $('#saveDetails').click(function (event) {
            $('#action').val('save');

            //Need to make sure all required fields are marked if empty.
            var hasErrors = 0;
            $('.alert-danger').hide();

            if (hasErrors == 0) {

                var formData = $("#formFields").serialize();

                $.ajax({
                    url: 'saveFields',
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        $('.fieldsUpdated').show();
                        $('.alert').delay(2000).fadeOut(1000);
                    }
                });
                event.preventDefault();
                return false;

            }
        });

        $('#next').click(function (event) {
            $('#action').val('next');
            $('.alert-danger').hide();

            var hasErrors = 0;

            if (hasErrors == 0) {
                var formData = $("#formFields").serialize();

                $.ajax({
                    url: 'saveFields',
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        window.location.href = 'translations';
                    }
                });
                event.preventDefault();
                return false;
            }
        });

    });
});

function loadHELConfigurationFields() {
    var HELRegistrySchemaName = $('#loadConfigurationFields').attr('schemaname');
    var helConfigId = $('#loadConfigurationFields').attr('helConfigId');
    
    $('.matchingField').each(function() {
	 $(this).find('option').remove().end().append('<option value="0">-</option>').val('');
    });
    
    $.ajax({
	url: 'getHELRegistryConfigurationFields?tenantId='+HELRegistrySchemaName,
	type: "GET",
	data: {
	    'helConfigId': helConfigId
	},
	success: function (data) {
	    //Load the available fields
	    $('#availableFields').html(data);
	   
	    var data = $(data);
	    
	    //find the element name
	    var fieldRows = data.find('.fieldRow');
	    
	    if(fieldRows.length > 0) {
		
		$('.matchingField').each(function() {
		    var selectObj = $(this);
		    var currVal = $(this).attr('currval');
		    
		    fieldRows.each(function(){
			var rowValues = $(this).attr('rel');
			var rowValuesSplit = rowValues.split('-');
			var validationId = rowValuesSplit[3];
			
			if((rowValuesSplit[0]*1) == (currVal*1)) {
			    selectObj.append($('<option selected></option>').val(rowValues).html(rowValuesSplit[2]));
			}
			else {
			    selectObj.append($('<option></option>').val(rowValues).html(rowValuesSplit[2]));
			}
			
		    });
		});
	    }
	}
    });
}

