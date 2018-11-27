
require(['./main'], function () {
    require(['jquery'], function($) {
        
        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(5000);
        }

        $("input:text,form").attr("autocomplete", "off");

        //Function to submit the changes to an admin user
        $(document).on('click', '#submitButton', function(event) {
        	
        	var buttonVal = $(this).attr('rel');
        	if (buttonVal == 'Update') {
	        	var passwordVal = $('#password').val();
	            var confirmPasswordVal = $('#confirmPassword').val();
	            var firstName = $('#firstName').val();
	            var lastName = $('#lastName').val();
	            var email = $('#email').val();
	            var userName = $('#username').val();
	            
	            var proceed = true;
	            
	            $('div.form-group').removeClass("has-error");
	            $('span.control-label').removeClass("has-error");
	            $('span.control-label').html("");
	            
	            if (firstName.trim() == '') {
	                $('#firstNameDiv').addClass("has-error");
	                $('#firstNameMsg').addClass("has-error");
	                $('#firstNameMsg').html('First name is required.');
	                event.preventDefault();
	                proceed = false;
	            }
            
	            if (lastName.trim() == '') {
	                $('#lastNameDiv').addClass("has-error");
	                $('#lastNameMsg').addClass("has-error");
	                $('#lastNameMsg').html('Last name is required.');
	                event.preventDefault();
	                proceed = false;
	            }
	            
	            if (!isEmail(email)) {
	                $('#emailDiv').addClass("has-error");
	                $('#emailMsg').addClass("has-error");
	                $('#emailMsg').html('Please enter a valid email.');
	                event.preventDefault();
	                proceed = false;
	            }
	            
	            if (passwordVal != confirmPasswordVal) {
	                $('#confirmPasswordDiv').addClass("has-error");
	                $('#passwordDiv').addClass("has-error");
	                $('#confimPasswordMsg').addClass("has-error");
	                $('#confimPasswordMsg').html('The two passwords do not match!');
	                event.preventDefault();
	                proceed = false;
	            }
	            
	            
	            if (userName.trim() == '') {
	                $('#userNameDiv').addClass("has-error");
	                $('#userNameMsg').addClass("has-error");
	                $('#userNameMsg').html('User Name is required.');
	                event.preventDefault();
	                proceed = false;
	            }
            
	            if (proceed) {
	                var formData = $("#userdetailsform").serialize();
	
	                var actionValue = '/settings';
	
	                $.ajax({
	                    url: actionValue,
	                    data: formData,
	                    type: "POST",
	                    async: false,
	                    success: function(data) {
	                    	$("#settingsModal").html(data);
	                    }
	                });
	                event.preventDefault();
	                return false;
	            }
       
        	} else {
        		 $('#settingsModal').modal('hide');
        	}
        	});
        	
        	
        	function isEmail(email) {
        	  var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        	  return regex.test(email);
        	}
        
    });
});



