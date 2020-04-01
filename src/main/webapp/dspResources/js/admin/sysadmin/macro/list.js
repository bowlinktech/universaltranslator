
require(['./main'], function () {

    //Fade out the updated/created message after being displayed.
    if ($('.alert').length > 0) {
        $('.alert').delay(2000).fadeOut(5000);
    }

    $("input:text,form").attr("autocomplete", "off");

    //this function will send them to table view
    $('#searchdataItemBtn').click(function () {
        $('#searchForm').submit();
    });

    //This function will remove the macro is clicked 
    $(document).on('click', '.marcoDelete', function () {
        var confirmed = confirm("Are you sure you want to remove this macro?");
        if (confirmed) {
            var id = $(this).attr('rel');
            window.location.href = "macros/delete?i=" + id;
        }
    });


    //This function will launch the new dataItem overlay with a blank screen
    $(document).on('click', '#createNewMacro', function () {

        $.ajax({
            url: 'macros/create',
            type: "GET",
            success: function (data) {
                $("#macroModal").html(data);
            }
        });
    });



    //This function will launch the edit macro item overlay populating the fields
    $(document).on('click', '.macroEdit', function () {

        var macroDetailsAction = "macros/view?i=" + $(this).attr('rel');

        $.ajax({
            url: macroDetailsAction,
            type: "GET",
            success: function (data) {
                $("#macroModal").html(data);
            }
        });
    });


    $(document).on('click', '#submitButton', function (event) {
        var currentPage = $('#currentPageHolder').attr('rel');
        var formData = $("#macroform").serialize();
        var actionValue = "macros/" + $(this).attr('rel').toLowerCase();
        $.ajax({
            url: actionValue,
            data: formData,
            type: "POST",
            async: false,
            success: function (data) {
                /** **/
                if (data.indexOf('macroUpdated') != -1) {
                    var goToUrl = "macros?msg=updated";
                    if (currentPage > 0) {
                        goToUrl = goToUrl + "&page=" + currentPage;
                    }
                    window.location.href = goToUrl;
                } else if (data.indexOf('macroCreated') != -1) {
                    var goToUrl = "macros?msg=created";
                    if (currentPage > 0) {
                        goToUrl = goToUrl + "&page=" + currentPage;
                    }
                    window.location.href = goToUrl;
                } else {
                    $("#macroModal").html(data);
                }

            }

        });
        event.preventDefault();
        return false;

    });
});



