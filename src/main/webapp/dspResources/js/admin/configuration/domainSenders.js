

require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        /** check the domain list to make sure there is at least one **/
        $(document).on('click', '#submitDomainButton', function (event) {
            //make sure at least one domain is filled in
            var found = false;
            $('#domainDanger').hide();
            $("#cwsf input[type=text]").each(function () {
                if (this.value.length > 0) {
                    found = true;
                    return true;
                }
            });

            if (!found) {
                $('#domainDanger').show();
                return false;
            } else {
                //check and submit form
                var formData = $("#cwsf").serialize();
                $.ajax({
                    url: 'saveDomainSenders.do',
                    type: "POST",
                    data: formData,
                    success: function (data) {
                        $("#domainModal").html(data);
                    }
                });
                event.preventDefault();
                return false;
            }
        });

    });
});


