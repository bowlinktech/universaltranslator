/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function () {
    require(['jquery'], function($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(10000);
        }

        $("input:text,form").attr("autocomplete", "off");

     // Delete Path 
      //Function to delete the message 
        $(document).on('click', '.deleteFilePath', function() {

            var confirmed = confirm("Are you sure you want to delete this path?");

            if (confirmed) {

            	var pathId = $(this).attr('rel');

                $.ajax({
                    url: 'moveFilePaths',
                    type: 'POST',
                    data: {'pathId': pathId},
                    success: function(data) {
                    	window.location.href='moveFilePaths?msg=deleted'

                    }
                });
            }

        });
        
    });
});



