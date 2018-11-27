
require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(5000);
        }

        $("input:text,form").attr("autocomplete", "off");
	
	//This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '#lastloginsButton', function () {
	    
            $.ajax({
                url: 'systemAdminLogins',
		data: {
		    'adminId': $(this).attr('rel')
		},
                type: "GET",
                success: function (data) {
                    $("#lastloginsModal").html(data);
                }
            });
        });
	
	//This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '#createNewSystemAdmin', function () {
	    
            $.ajax({
                url: 'adminInfo',
		data: {
		    'adminId': 0
		},
                type: "GET",
                success: function (data) {
                    $("#profileModal").html(data);
                }
            });
        });

        //This function will launch the new dataItem overlay with a blank screen
        $(document).on('click', '#profileButton', function () {
	    
            $.ajax({
                url: 'adminInfo',
		data: {
		    'adminId': $(this).attr('rel')
		},
                type: "GET",
                success: function (data) {
                    $("#profileModal").html(data);
                }
            });
        });
	
	//Function to submit the changes to an admin user
        $(document).on('click', '#submitButton', function (event) {
	    var buttonVal = $(this).attr('rel');
	    
	    var passwordVal = $('#newPassword').val();
	    var confirmPasswordVal = $('#confirmPassword').val();
	    var firstName = $('#firstName').val();
	    var lastName = $('#lastName').val();
	    var username = $('#username').val();
	    var email = $('#email').val();
	    var proceed = true;
	    
	    $('div.form-group').removeClass("has-error");
	    $('span.control-label').removeClass("has-error");
	    $('span.control-label').html("");
	    
	    if (firstName.trim() === '') {
		$('#firstNameDiv').addClass("has-error");
		$('#firstNameMsg').addClass("has-error");
		$('#firstNameMsg').html('First name is required.');
		event.preventDefault();
		proceed = false;
	    }

	    if (lastName.trim() === '') {
		$('#lastNameDiv').addClass("has-error");
		$('#lastNameMsg').addClass("has-error");
		$('#lastNameMsg').html('Last name is required.');
		event.preventDefault();
		proceed = false;
	    }

	    if (username.trim() === '') {
		$('#usernameDiv').addClass("has-error");
		$('#usernameMsg').addClass("has-error");
		$('#usernameMsg').html('Username is required.');
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
	    
	    if (passwordVal !== confirmPasswordVal) {
		$('#confirmPasswordDiv').addClass("has-error");
		$('#newPasswordDiv').addClass("has-error");
		$('#confimPasswordMsg').addClass("has-error");
		$('#confimPasswordMsg').html('The two passwords do not match.');
		event.preventDefault();
		proceed = false;
	    }
	    
	    if (buttonVal === 'Update') {
                var existingPasswordVal = $('#existingPassword').val();

                if (existingPasswordVal.trim() === '') {
                    $('#existingPasswordDiv').addClass("has-error");
                    $('#existingPasswordMsg').addClass("has-error");
                    $('#existingPasswordMsg').html('Existing password cannot be blank.');
                    event.preventDefault();
                    proceed = false;
                }
            } 
	    
	    if (proceed) {
		var formData = $("#userdetailsform").serialize();

		var actionValue = 'adminInfo';

		$.ajax({
		    url: actionValue,
		    data: formData,
		    type: "POST",
		    async: false,
		    success: function (data) {
			$("#profileModal").html(data);
		    }
		});
		event.preventDefault();
		return false;
	    }
        });


        function isEmail(email) {
            var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            return regex.test(email);
        }

    });
});



