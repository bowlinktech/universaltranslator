

require(['./main'], function () {
    
    $(document).on('click', '.showMore', function() {
       var macroId = $(this).attr('rel');
       
       if(!$('#macro-'+macroId).is(":visible")) {
           $(this).html("Hide Details");
           $('#macro-'+macroId).show();
       }
       else {
           $(this).html("Show More Details");
           $('#macro-'+macroId).hide();
       }
    });
    
    $(document).on('click','.deleteCrosswalk',function() {
        var dtsId = $(this).attr('rel2');
        var cwId = $(this).attr('rel');
        
        if((dtsId*1) > 0) {
             //alert("The selected crosswalk is currently associated to one of the below data translations and cannot be deleted. \n\nIf you are trying to upload a new file for the selected crosswalk click on the 'View' link to upload a new file. \n\nTo completely remove this crosswalk you must first remove the associated translation below.");
             alert("The selected crosswalk is part of an existing configuration. You can click 'view' and upload an updated crosswalk file or you can delete the crosswalk after removing it from all configurations (data translations section) it is associated with.");
        }
        else {
            if(confirm("Are you sure you want to remove this crosswalk?")) {
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
           
           var fileType = $("input[name='fileType']:checked").val();
           
           $('body').overlay({
                glyphicon : 'floppy-disk',
                message : 'Generating Excel File!'
            });
             $('.overlay').css('display','block');
           
           $.ajax({
                //url: '/administrator/configurations/crosswalksDownload',
                url: '/administrator/configurations/crosswalksExcelFileDownload',
                data: {
                    'configId':$(this).attr('rel'),
                    'fileName': $('#fileName').val(),
                    'fileType': fileType
                },
                type: "GET",
                dataType : 'text',
                contentType : 'application/json;charset=UTF-8',
                success: function(data) {
                    if(data !== '') {
                        window.location.href = '/administrator/configurations/downloadDTCWExcelFile/'+ data;
                        $('#successMsg').show();
                        $('.overlay').css('display','none');
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
        var noErrors = 1;
        
        var fieldA = $('#fieldAQuestion').val();
        var fieldB = $('#fieldBQuestion').val();
        var con1 = $('#Con1Question').val();
        var con2 = $('#Con2Question').val();

        if(noErrors == 1) {
            //Clear all fields
            $('#fieldA').val("");
            $('#fieldB').val("");
            $('#constant1').val("");
            $('#constant2').val("");
            
            if($('#Con1QuestionSelect').val() !== 'undefined') {
                $('#constant1').attr('rel', $('#Con1Question option:selected').html());
            }
            if($('#Con2QuestionSelect').val() !== 'undefined') {
                $('#constant2').attr('rel', $('#Con2Question option:selected').html());
            }
            if($('#fieldAQuestionSelect').val() !== 'undefined') {
                $('#fieldA').attr('rel', $('#fieldAQuestion option:selected').html());
            }
            if($('#fieldBQuestionSelect').val() !== 'undefined') {
                $('#fieldB').attr('rel', $('#fieldBQuestion option:selected').html());
            }
            
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
        }
        
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
    
    $(document).on('click', '#clearTranslationButton', function() {
        $('#field').val("");
        $('#crosswalk').val("");
        $('#macro').val("");
        $('#fieldA').val("");
        $('#fieldB').val("");
        $('#constant1').val("");
        $('#constant2').val("");
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
        
        if (typeof $('#constant1').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant1').attr('rel');
        }
        if (typeof $('#constant2').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant2').attr('rel');
        }
        if (typeof $('#constant1').attr('rel') !== 'undefined') {
            selectedCWText = $('#constant1').attr('rel');
        }
        if (typeof $('#fieldA').attr('rel') !== 'undefined') {
            selectedCWText = $('#fieldA').attr('rel');
        }
        if (typeof $('#fieldB').attr('rel') !== 'undefined') {
            selectedCWText = $('#fieldB').attr('rel');
        }
        
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
                data: {
                    'f': selectedField, 
                    'fText': selectedFieldText, 
                    'cw': selectedCW, 
                    'CWText': selectedCWText, 
                    'macroId': selectedMacro, 
                    'macroName': selectedMacroText, 
                    'fieldA': $('#fieldA').val(), 
                    'fieldB': $('#fieldB').val(), 
                    'constant1': $('#constant1').val(), 
                    'constant2': $('#constant2').val(),
                    'passClear': $('.passclear:checked').val(),
                    'categoryId': 1
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
                    $("#fieldA").removeAttr("rel");
                    $("#fieldB").removeAttr("rel");
                    $("#constant1").removeAttr("rel");
                    $("#constant2").removeAttr("rel");
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
        
        $.ajax({
            url: 'updateTranslationProcessOrder?currProcessOrder=' + currDspPos + '&categoryId=1&newProcessOrder=' + newDspPos,
            type: "POST",
            success: function (data) {
                $('#translationMsgDiv').show();
                populateExistingTranslations(1);
            }
        });
        
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
        data: {
            'page': page, 
            'orgId': orgId, 
            'maxCrosswalks': 8, 
            'configId': configId
        },
        success: function (data) {
            
            $("#crosswalksTable").html(data);
            
            $("#crosswalksTable").find('#cwDataTable').DataTable({
                bServerSide: false,
                bProcessing: false, 
                deferRender: true,
                aaSorting: [[3,'desc']],
                 "columns": [
                    { "width": "10%" },
                    { "width": "20%" },
                    { "width": "20%", "type": "date" },
                    { "width": "20%", "type": "date" },
                    { "width": "10%" },
                    { "width": "10%" }
                 ],
                sPaginationType: "bootstrap", 
                searching: false,
                bLengthChange: false,
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
                        '</select>'
                }
            });
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
