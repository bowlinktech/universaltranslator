

require(['./main'], function () {
    require(['jquery', 'bootstrapwysihtlm5', 'wysihtlm5'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        $('#longDesc').wysihtml5({
            "font-styles": true,
            "emphasis": true,
            "lists": true,
            "html": false,
            "link": true,
            "image": false,
            "color": true,
            "stylesheets": ["/dspResources/css/admin/wysiwyg-color.css"]
        });

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        $('#saveDetails').click(function (event) {
            $('#action').val('save');

            //Need to make sure all required fields are marked if empty.
            $("#articleForm").submit();
        });


    });
});

