
require(['./main'], function () {
    require(['jquery'], function ($) {


        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        //Function that will display the new field module
        $(document).on('click', '#addNewField', function () {
            var fieldNo = $('#maxFieldNo').val();

            $.ajax({
                url: 'addNewField',
                type: "GET",
                data: {'maxfieldNo': fieldNo},
                success: function (data) {
                    $("#newFieldModal").html(data);
                }
            });
        });

        //This function will save the messgae type field mappings
        $('#saveDetails').click(function (event) {

            //Need to make sure all required fields are marked if empty.
            var errorsFound = 0;
            var row = 0;

            $('#mappingErrorMsgDiv').hide();

            if (errorsFound == 0) {

		/*$('body').overlay({
                 glyphicon : 'floppy-disk',
                 message : 'Saving...'
                 });*/

                var formData = $("#fieldMappings").serialize();

                $.ajax({
                    url: 'mappings',
                    data: formData,
                    type: "POST",
                    async: false,
                    success: function (data) {
                        if (data == 1) {
                            $('.mappingsUpdated').show();
                            $('.alert').delay(2000).fadeOut(1000);
                        }
                    }
                });
                event.preventDefault();
                return false;
                //$('#fieldMappings').submit();

            } else {
                $('#mappingErrorMsgDiv').show();
            }

        });


    });
});


//This functin will be used to populate the tableCols drop down.
//function takes in the name of the selected table name and the
//row it is working with.
function populateTableColumns(tableName) {

    var result = [];

    $.ajax({
        async: false,
        url: 'getTableCols.do',
        data: {'tableName': tableName},
        dataType: 'json',
        success: function (data) {
            result = data;
        }
    });

    return result;
}

//This function will be used to populate the autoPopulatetableCols drop down.
//function takes in the name of the selected table name and the
//row it is working with.


function populateAutoTableColumns(tableName) {

    var result = [];

    $.ajax({
        async: false,
        url: 'getTableCols.do',
        data: {'tableName': tableName},
        dataType: 'json',
        success: function (data) {
            result = data;
        }
    });

    return result;
}


