

require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	if($("#dataTable" ).length > 0) {
	    var oSettings = datatable.fnSettings();
        
	    datatable.fnSort( [ [2,'desc'] ] );
	    
	}
	
        //Update the status of the connection
        $('.changeStatus').click(function () {
            var connectionId = $(this).attr('rel');
            var newStatusVal = $(this).attr('rel2');

            $.ajax({
                url: 'changeConnectionStatus.do',
                type: "POST",
                data: {'statusVal': newStatusVal, 'connectionId': connectionId},
                success: function (data) {
                    if (data === 1) {
                        window.location.href = 'connections?msg=updated'
                    }
                }
            });
        });

        //This function will launch the new configuration connection overlay with a blank screen
        $(document).on('click', '#createNewConnection', function () {
	    $.ajax({
                url: 'createConnection',
                type: "GET",
                success: function (data) {
                    $("#connectionsModal").html(data);
                }
            });
        });

        //This function will launch the edit connection overlay
        $(document).on('click', '.connectionEdit', function () {
            var connectionId = $(this).attr('rel');
            $.ajax({
                url: 'editConnection',
                type: "GET",
                data: {'connectionId': connectionId},
                success: function (data) {
                    $("#connectionsModal").html(data);
                }
            });
        });

        //Go get the existing message types for the selected organization'
        $(document).on('change', '.selSendingOrganization', function () {
            var selOrg = $(this).val();
            var connectionId = $('#connectionId').val();

            if (selOrg === '') {
                $('#srcorgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg, 'srcConfig');
                populateUsers(selOrg, 'srcContactsTable', connectionId);
            }
        });

        //Go get the existing message types for the selected organization
        $(document).on('change', '.seltgtOrganization', function () {
            var selOrg = $(this).val();
            var connectionId = $('#connectionId').val();

            if (selOrg === '') {
                $('#tgtorgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg, 'tgtConfig');
                populateUsers(selOrg, 'tgtContactsTable', connectionId);
            }
        });

        //This function will save the messgae type field mappings
        $(document).on('click', '#submitButton', function () {
            var hasErrors = 0;
            var srcConfig = $('#srcConfig').val();
            var tgtConfig = $('#tgtConfig').val();

            $('div.form-group').removeClass("has-error");
            $('span.control-label').removeClass("has-error");
            $('span.control-label').html("");
            $('.alert-danger').hide();

            if (srcConfig === '') {
                $('#srcConfigDiv').addClass("has-error");
                hasErrors = 1;
            }

            if (tgtConfig === '') {
                $('#tgtConfigDiv').addClass("has-error");
                hasErrors = 1;
            }

            if (hasErrors == 0) {

                $('#connectionForm').submit();
            }

        });

        $(document).on('change', '#sendAllSourceContacts', function () {
	   
            if ($(this).is(":checked")) {
                $('.srcEmailNotifications').each(function () {
                    $(this).prop('checked', true);
                });
            }
	    else {
                $('.srcEmailNotifications').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });

        $(document).on('change', '#sendAllTargetContacts', function () {
	   
            if ($(this).is(":checked")) {
                $('.tgtEmailNotifications').each(function () {
                    $(this).prop('checked', true);
                });
            } 
	    else {
                $('.tgtEmailNotifications').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });
    });
});

function populateConfigurations(orgId, selectBoxId) {

    var currConfigId = $('#' + selectBoxId).attr('rel');

    $.ajax({
        url: 'getAvailableConfigurations.do',
        type: "GET",
        data: {'orgId': orgId},
        success: function (data) {
            //get value of preselected col
            var html = '<option value="">- Select - </option>';
            var len = data.length;

            for (var i = 0; i < len; i++) {
                if (data[i].id == currConfigId) {
                    html += '<option value="' + data[i].id + '" selected>' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                } else {
                    html += '<option value="' + data[i].id + '">' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                }
            }
            $('#' + selectBoxId).html(html);
        }
    });
}

function populateUsers(orgId, selectBoxId, connectionId) {

    var users = $('#' + selectBoxId).attr('rel');

    var url = "";
    
    if (selectBoxId === "srcContactsTable") {
        url = "getAvailableSendingContacts.do";
    } else {
        url = "getAvailableReceivingContacts.do";
    }

    $.ajax({
        url: url,
        type: "GET",
        data: {
	    'orgId': orgId, 
	    'connectionId': connectionId
	},
        success: function (data) {
            $('#' + selectBoxId).html(data);
        }
    });
}




