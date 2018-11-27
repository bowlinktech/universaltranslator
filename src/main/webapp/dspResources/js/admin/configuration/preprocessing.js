/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        populateExistingMacros(0);

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

        //This function will handle populating the data translation table
        //The trigger will be when a crosswalk is selected along with a
        //field
        $(document).on('click', '#submitTranslationButton', function () {
            var selectedField = $('#field').val();
            var selectedFieldText = $('#field').find(":selected").text();
            var selectedCW = 0;
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
                        , 'categoryId': 2
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
                        url: 'updateTranslationProcessOrder?currProcessOrder=' + currDspPos + '&newProcessOrder=' + newDspPos,
                        type: "POST",
                        success: function (data) {
                            $('#translationMsgDiv').show();
                            populateExistingMacros(1);
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
                url: 'removeTranslations?fieldId=' + fieldId + '&processOrder=' + currPos,
                type: "POST",
                success: function (data) {
                    $('#translationMsgDiv').show();
                    populateExistingMacros(1);
                }
            });

        });

        //The function that will be called when the "Save" button
        //is clicked
        $('#saveDetails').click(function () {
            $.ajax({
                url: 'translations',
                type: "POST",
                data: {'categoryId': 2},
                success: function (data) {
                    window.location.href = "preprocessing?msg=updated";
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
                data: {'categoryId': 2},
                success: function (data) {
                    window.location.href = "postprocessing?msg=updated";
                }
            });
        });



    });
});


function populateExistingMacros(reload) {

    $.ajax({
        url: 'getTranslations.do',
        type: "GET",
        data: {'reload': reload, 'categoryId': 2},
        success: function (data) {
            $("#existingTranslations").html(data);
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