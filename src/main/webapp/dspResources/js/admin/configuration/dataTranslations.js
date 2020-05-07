

require(['./main'], function () {
    
    $(document).on('click','.deleteCrosswalk',function() {
        var dtsId = $(this).attr('rel2');
        var cwId = $(this).attr('rel');
        
        if((dtsId*1) > 0) {
             alert("The crosswalk is currenlty being used below and can't be deleted.");
        }
        else {
             if(confirm("Are you sure you want to remove this batch?")) {
                    $('body').overlay({
                        glyphicon : 'floppy-disk',
                        message : 'Deleting...'
                    });

                    $.ajax({
                        url: 'deleteCrosswalk.do',
                        data: {
                            'cwId': cwId
                        },
                        type: 'POST',
                        success: function(data) {
                           location.reload();
                        }
                    });
             }
        }
    });
        
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

    //This function will launch the new crosswalk overlay with a blank form
    $(document).on('click', '.macroDefinitions', function () {
        $.ajax({
            url: 'macroDefinitions',
            type: "GET",
            data: {
                'macroCategory': 1
            },
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    $(document).on('click', '.useMacro', function() {
        var macroId = $(this).attr('rel');
        $('#macro').val(macroId);
        $('#macro').trigger( "change" );
    });

    $(document).on('click', '.createCrosswalkDownload', function() {
       $.ajax({
            url: '/administrator/configurations/createCrosswalkDownload',
            data: {
               'configId':$(this).attr('rel')
            },
            type: "GET",
            success: function(data) {
                $("#cwDownloadModal").html(data);
            }
        });
    });

    $(document).on('click', '#generateCWButton', function() {

        var errorFound = 0;

       //Makes sure name is entered and entity is selected
       if($('#fileName').val() === "") {
           $('#cwnameDiv').addClass("has-error");
           errorFound = 1;
       }

       if(errorFound == 0) {
           $.ajax({
                url: '/administrator/configurations/crosswalksDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val()
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWFile/'+ data;
                        $('#successMsg').show();
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
       }

    });

    $(document).on('click', '.createDataTranslationDownload', function() {
       $.ajax({
            url: '/administrator/configurations/createDataTranslationDownload',
            data: {
               'configId':$(this).attr('rel')
            },
            type: "GET",
            success: function(data) {
                $("#dtDownloadModal").html(data);
            }
        });
    });

    $(document).on('click', '#generateDTButton', function() {

        var errorFound = 0;

       //Makes sure name is entered and entity is selected
       if($('#fileName').val() === "") {
           $('#dtnameDiv').addClass("has-error");
           errorFound = 1;
       }

       if(errorFound == 0) {
           $.ajax({
                url: '/administrator/configurations/dataTranslationsDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val()
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWFile/'+ data;
                        $('#successMsg').show();
                        //$('#dtDownloadModal').modal('toggle');
                    }
                    else {
                        $('#errorMsg').show();
                    }
                }
            });
       }
    });

    $("input:text,form").attr("autocomplete", "off");
    populateCrosswalks(1);
    populateExistingTranslations(0);

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(1000);
    }

    //Function that will check the selected macro and determine if a module
    //should be launched to ask questions.
    $('#macro').change(function () {
        var selMacro = $(this).val();
        var list = $('#macroLookUpList').val();

        if (selMacro > 0) {
            if (list.indexOf(selMacro) !== -1) {
                $.ajax({
                    url: 'getMacroDetails.do',
                    type: "GET",
                    data: {'macroId': selMacro},
                    success: function (data) {
                        $('#macroModal').html(data);
                        $('#macroModal').modal('toggle');
                        $('#macroModal').modal('show');
                    }
                });
            }
        }
    });

    //Function that will take in the macro details
    $(document).on('click', '.submitMacroDetailsButton', function () {
        var fieldA = $('#fieldAQuestion').val();
        var fieldB = $('#fieldBQuestion').val();
        var con1 = $('#Con1Question').val();
        var con2 = $('#Con2Question').val();

        //Clear all fields
        $('#fieldA').val("");
        $('#fieldB').val("");
        $('#constant1').val("");
        $('#constant2').val("");

        if (fieldA) {
            $('#fieldA').val(fieldA);
        }
        if (fieldB) {
            $('#fieldB').val(fieldB);
        }
        if (con1) {
            $('#constant1').val(con1);
        }
        if (con2) {
            $('#constant2').val(con2);
        }

        //Close the modal window
        $('#macroModal').modal('toggle');
        $('#macroModal').modal('hide');
    });


    //This function will get the next/prev page for the crosswalk list
    $(document).on('click', '.nextPage', function () {
        var page = $(this).attr('rel');
        populateCrosswalks(page);
    });

    //The function that will be called when the "Save" button
    //is clicked
    $('#saveDetails').click(function () {
        $.ajax({
            url: 'translations',
            type: "POST",
            data: {'categoryId': 1},
            success: function (data) {
                window.location.href = "translations?msg=updated";
            }
        });
    });

    //The function that will be called when the "Next Step" button
    //is clicked
    $('#next').click(function () {

        var configType = $('#configtype').attr('rel');
        var mappings = $('#configtype').attr('rel2');

        $.ajax({
            url: 'translations',
            type: "POST",
            data: {'categoryId': 1},
            success: function (data) {
                window.location.href = "scheduling?msg=updated";
            }
        });
    });

    $(document).on('click', '.cwClose', function() {
      window.location.reload();
    });

    //This function will launch the crosswalk overlay with the selected
    //crosswalk details
    $(document).on('click', '.viewCrosswalk', function () {
        $.ajax({
            url: 'viewCrosswalk' + $(this).attr('rel'),
            type: "GET",
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });


    //This function will launch the new crosswalk overlay with a blank form
    $(document).on('click', '#createNewCrosswalk', function () {
        var orgId = $('#orgId').val();

        $.ajax({
            url: 'newCrosswalk',
            type: "GET",
            data: {'orgId': orgId},
            success: function (data) {
                $("#crosswalkModal").html(data);
            }
        });
    });

    //The function to submit the new crosswalk
    $(document).on('click', '#submitCrosswalkButton', function (event) {
        $('.uploadError').hide();
        $('.uploadSuccess').hide();
        $('#crosswalkNameDiv').removeClass("has-error");
        $('#crosswalkNameMsg').removeClass("has-error");
        $('#crosswalkNameMsg').html('');
        $('#crosswalkDelimDiv').removeClass("has-error");
        $('#crosswalkDelimMsg').removeClass("has-error");
        $('#crosswalkDelimMsg').html('');
        $('#crosswalkFileDiv').removeClass("has-error");
        $('#crosswalkFileMsg').removeClass("has-error");
        $('#crosswalkFileMsg').html('');

        var errorFound = 0;
        var actionValue = $(this).attr('rel').toLowerCase();

        //Make sure a title is entered
        if ($('#name').val() == '') {
            $('#crosswalkNameDiv').addClass("has-error");
            $('#crosswalkNameMsg').addClass("has-error");
            $('#crosswalkNameMsg').html('The crosswalk name is a required field!');
            errorFound = 1;
        }

        //Need to make sure the crosswalk name doesn't already exist.
        var orgId = $('#orgId').val();

        if(actionValue === "create") {

            $.ajax({
                url: 'checkCrosswalkName.do',
                type: "POST",
                async: false,
                data: {'name': $('#name').val(), 'orgId': orgId},
                success: function (data) {
                    if (data == 1) {
                        $('#crosswalkNameDiv').addClass("has-error");
                        $('#crosswalkNameMsg').addClass("has-error");
                        $('#crosswalkNameMsg').html('The name entered is already associated with another crosswalk in the system!');
                        errorFound = 1;
                    }
                }
            });
        }

        //Make sure a delimiter is selected
        if ($('#delimiter').val() == '') {
            $('#crosswalkDelimDiv').addClass("has-error");
            $('#crosswalkDelimMsg').addClass("has-error");
            $('#crosswalkDelimMsg').html('The file delimiter is a required field!');
            errorFound = 1;
        }

        //Make sure a file is selected and is a text file
        if ($('#crosswalkFile').val() == '' || $('#crosswalkFile').val().indexOf('.txt') == -1) {
            $('#crosswalkFileDiv').addClass("has-error");
            $('#crosswalkFileMsg').addClass("has-error");
            $('#crosswalkFileMsg').html('The crosswalk file must be a text file!');
            errorFound = 1;
        }

        if (errorFound == 1) {
            event.preventDefault();
            return false;
        }
        else {

            //check and submit form
            var form = $('#crosswalkdetailsform')[0];
            var formData = new FormData(form);
            $.ajax({
                url: '/administrator/configurations/'+actionValue+'Crosswalk',
                type: "POST",
                enctype: 'multipart/form-data',
                processData: false,  // Important!
                contentType: false,
                cache: false,
                data: formData,
                success: function(data) {
                   if(data > 0) {
                       $.ajax({
                            url: 'viewCrosswalk?i=' + data,
                            type: "GET",
                            success: function(data) {
                                data = data.replace('close', 'close cwClose');
                                data = data.replace('uploadSuccess" role="alert" style="display:none;"', 'uploadSuccess" role="alert" style="display:block;"');
                                $("#crosswalkModal").html(data);
                            }
                        });
                   }
                   else {
                       $('.uploadError').show();
                   }
                }
            });
        }

        //$('#crosswalkdetailsform').attr('action', actionValue + 'Crosswalk');
        //$('#crosswalkdetailsform').submit();

    });

    //This function will handle populating the data translation table
    //The trigger will be when a crosswalk is selected along with a
    //field
    $(document).on('click', '#submitTranslationButton', function () {
        var selectedField = $('#field').val();
        var selectedFieldText = $('#field').find(":selected").text();
        var selectedCW = $('#crosswalk').val();
        var selectedCWText = $('#crosswalk').find(":selected").text();
        var selectedMacro = $('#macro').val();
        var selectedMacroText = $('#macro').find(":selected").text();

        //Remove all error classes and error messages
        $('div').removeClass("has-error");
        $('span').html("");

        var errorFound = 0;

        if (selectedField == "") {
            $('#fieldDiv').addClass("has-error");
            $('#fieldMsg').addClass("has-error");
            $('#fieldMsg').html('A field must be selected!');
            errorFound = 1;
        }
        if (selectedCW == "" && selectedMacro == "") {
            $('#crosswalkDiv').addClass("has-error");
            $('#crosswalkMsg').addClass("has-error");
            $('#crosswalkMsg').html('Either a macro or crosswalk must be selected!');
            $('#macroDiv').addClass("has-error");
            $('#macroMsg').addClass("has-error");
            $('#macroMsg').html('Either a macro or crosswalk must be selected!');
            errorFound = 1;
        }

        if (errorFound == 0) {
            $.ajax({
                url: "setTranslations",
                type: "GET",
                data: {'f': selectedField, 'fText': selectedFieldText, 'cw': selectedCW, 'CWText': selectedCWText, 'macroId': selectedMacro
                    , 'macroName': selectedMacroText, 'fieldA': $('#fieldA').val(), 'fieldB': $('#fieldB').val(), 'constant1': $('#constant1').val(), 'constant2': $('#constant2').val()
                    , 'passClear': $('.passclear:checked').val()
                    , 'categoryId': 1
                },
                success: function (data) {
                    $('#translationMsgDiv').show();
                    $("#existingTranslations").html(data);
                    //Need to clear out the select boxes
                    $('#field option:eq("")').prop('selected', true);
                    $('#crosswalk option:eq("")').prop('selected', true);
                    $('#macro option:eq("")').prop('selected', true);
                    //Need to clear out fields
                    $('#fieldA').val("");
                    $('#fieldB').val("");
                    $('#constant1').val("");
                    $('#constant2').val("");
                }
            });
        }

    });

    //Function that will handle changing a process order and
    //making sure another field does not have the same process 
    //order selected. It will swap display position
    //values with the requested position.
    $(document).on('change', '.processOrder', function () {
        //Store the current position
        var currDspPos = $(this).attr('rel');
        var newDspPos = $(this).val();

        $('.processOrder').each(function () {
            if ($(this).attr('rel') == newDspPos) {
                //Need to update the saved process order
                $.ajax({
                    url: 'updateTranslationProcessOrder?currProcessOrder=' + currDspPos + '&categoryId=1&newProcessOrder=' + newDspPos,
                    type: "POST",
                    success: function (data) {
                        $('#translationMsgDiv').show();
                        populateExistingTranslations(1);
                    }
                });
                $(this).val(currDspPos);
                $(this).attr('rel', currDspPos);
            }
        });

        $(this).val(newDspPos);
        $(this).attr('rel', newDspPos);

    });

    //Function that will handle removing a line item from the
    //existing data translations. Function will also update the
    //processing orders for each displayed.
    $(document).on('click', '.removeTranslation', function () {
        var currPos = $(this).attr('rel2');
        var fieldId = $(this).attr('rel');

        //Need to remove the translation
        $.ajax({
            url: 'removeTranslations?fieldId=' + fieldId + '&categoryId=1&processOrder=' + currPos,
            type: "POST",
            success: function (data) {
                $('#translationMsgDiv').show();
                populateExistingTranslations(1);
            }
        });

    });
});


function populateExistingTranslations(reload) {

    $.ajax({
        url: 'getTranslations.do',
        type: "GET",
        data: {'reload': reload, 'categoryId': 1},
        success: function (data) {
            $("#existingTranslations").html(data);
	    $('.dtDownloadLink').show();
        }
    });
}

function populateCrosswalks(page) {
    var orgId = $('#orgId').val();
    var configId = $('#configId').val();
   
    $.ajax({
        url: 'getCrosswalks.do',
        type: "GET",
        data: {'page': page, 'orgId': orgId, 'maxCrosswalks': 8, 'configId': configId},
        success: function (data) {
            $("#crosswalksTable").html(data);
        }
    });
}

function removeVariableFromURL(url_string, variable_name) {
    var URL = String(url_string);
    var regex = new RegExp("\\?" + variable_name + "=[^&]*&?", "gi");
    URL = URL.replace(regex, '?');
    regex = new RegExp("\\&" + variable_name + "=[^&]*&?", "gi");
    URL = URL.replace(regex, '&');
    URL = URL.replace(/(\?|&)$/, '');
    regex = null;
    return URL;
}


function populateFieldA() {
    if ($("#field option:selected").val() != '') {
        var idForOption = "o" + $("#field option:selected").val();
        var fieldAVal = $("#" + idForOption).attr('rel');
        $('#fieldAQuestion').val(fieldAVal);
    }
}
