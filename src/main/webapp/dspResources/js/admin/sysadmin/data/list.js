
require(['./main'], function () {

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(5000);
    }

    $("input:text,form").attr("autocomplete", "off");

    //This function will launch the new dataItem overlay with a blank screen
    $(document).on('click', '#createNewdataItem', function () {

        $.ajax({
            url: $('#goToURL').attr('rel') + '/create',
            type: "GET",
            success: function (data) {
                $("#addLUDataModal").html(data);
            }
        });
    });


    //this function will send them to table view
    $('#searchdataItemBtn').click(function () {
        $('#searchForm').submit();
    });

    //This function will remove the clicked dataItem 
    $(document).on('click', '.dataItemDelete', function () {

        var confirmed = confirm("Are you sure you want to remove this data?");

        if (confirmed) {
            var id = $(this).attr('rel');
            var path = $('#urlIdInfo').attr('rel') + "/delete?i=" + id;
            window.location.href = path;
        }
    });

    //This function will launch the edit data item overlay populating the fields
    //with the data of the clicked user.
    $(document).on('click', '.dataEdit', function () {
        var dataDetailsAction = $('#goToURL').attr('rel') + "/tableData?i=" + $(this).attr('rel');
        $.ajax({
            url: dataDetailsAction,
            type: "GET",
            success: function (data) {
                $("#addLUDataModal").html(data);
            }
        });
    });

    $(document).on('click', '#submitButton', function (event) {
        var currentPage = $('#currentPageHolder').attr('rel');
        var formData = $("#tabledataform").serialize();
        var actionValue = $(this).attr('rel').toLowerCase();
        var goToUrl = $('#urlIdInfo').attr('rel');
        $.ajax({
            url: actionValue,
            data: formData,
            type: "POST",
            async: false,
            success: function (data) {
                /**now add codes to refresh window and close modal window**/
                if (data.indexOf('dataUpdated') != -1) {
                    goToUrl = goToUrl + "?msg=updated";
                    if (currentPage > 0) {
                        goToUrl = goToUrl + "&page=" + currentPage;
                    }
                    window.location.href = goToUrl;
                } else if (data.indexOf('dataCreated') != -1) {
                    goToUrl = goToUrl + "?msg=created";
                    if (currentPage > 0) {
                        goToUrl = goToUrl + "&page=" + currentPage;
                    }
                    window.location.href = goToUrl;
                } else {
                    $("#addLUDataModal").html(data);
                }
            }
        });
        event.preventDefault();
        return false;

    });
});



