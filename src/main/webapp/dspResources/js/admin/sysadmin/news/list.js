
require(['./main'], function () {
    require(['jquery'], function ($) {

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
        $(document).on('click', '.articleDelete', function () {
            var confirmed = confirm("Are you sure you want to remove this article?");
            if (confirmed) {
                var id = $(this).attr('rel');
                window.location.href = "news/delete?i=" + id;
            }
        });


    });
});



