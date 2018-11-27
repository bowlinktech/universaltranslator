

require(['./main'], function () {
    require(['jquery'], function ($) {

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }
	
	if($("#dataTable" ).length > 0) {
	    var oSettings = datatable.fnSettings();
        
	    datatable.fnSort( [ [5,'desc'] ] );
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
        $(document).on('change', '.selsrcOrganization', function () {
            var selOrg = $(this).val();
            var connectionId = $('#connectionId').val();

            if (selOrg === '') {
                $('#srcorgDiv').addClass("has-error");
            } else {
                populateConfigurations(selOrg, 'srcConfig');
                populateUsers(selOrg, 'srcUsersTable', connectionId);
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
                populateUsers(selOrg, 'tgtUsersTable', connectionId);
            }
        });


        //This function will save the messgae type field mappings
        $(document).on('click', '#submitButton', function () {
            var hasErrors = 0;
            var srcConfig = $('#srcConfig').val();
            var tgtConfig = $('#tgtConfig').val();
            var srcUsers = $('#srcUsers').val();
            var tgtUsers = $('#tgtUsers').val();

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

            if (srcUsers === '' || srcUsers == null) {
                $('#srcUsersDiv').addClass("has-error");
                hasErrors = 1;
            }

            if (tgtUsers === '' || tgtUsers == null) {
                $('#tgtUsersDiv').addClass("has-error");
                hasErrors = 1;
            }

            if (hasErrors == 0) {

                $('#connectionForm').submit();
            }

        });

        $(document).on('change', '#selectAllSrcUsers', function () {
            if ($(this).is(":checked")) {
                $('.srcUsers').each(function () {
                    $(this).prop('checked', true);
                });
            } else {
                $('.srcUsers').each(function () {
                    $(this).prop('checked', false);
                });
                $('.srcUsersSendEmail').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });

        $(document).on('change', '#sendAllSrcUsers', function () {
            if ($(this).is(":checked")) {
                $('.srcUsers').each(function () {
                    $(this).prop('checked', true);
                });
                $('.srcUsersSendEmail').each(function () {
                    $(this).prop('checked', true);
                });
            } else {
                $('.srcUsers').each(function () {
                    $(this).prop('checked', false);
                });
                $('.srcUsersSendEmail').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });

        $(document).on('change', '#selectAllTgtUsers', function () {
            if ($(this).is(":checked")) {
                $('.tgtUsers').each(function () {
                    $(this).prop('checked', true);
                });
            } else {
                $('.tgtUsers').each(function () {
                    $(this).prop('checked', false);
                });
                $('.tgtUsersSendEmail').each(function () {
                    $(this).prop('checked', false);
                });
            }
        });

        $(document).on('change', '#sendAllTgtUsers', function () {
            if ($(this).is(":checked")) {
                $('.tgtUsers').each(function () {
                    $(this).prop('checked', true);
                });
                $('.tgtUsersSendEmail').each(function () {
                    $(this).prop('checked', true);
                });
            } else {
                $('.tgtUsers').each(function () {
                    $(this).prop('checked', false);
                });
                $('.tgtUsersSendEmail').each(function () {
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
                    html += '<option value="' + data[i].id + '" selected>' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].messageTypeName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                } else {
                    html += '<option value="' + data[i].id + '">' + data[i].configName + '&nbsp;&#149;&nbsp;' + data[i].messageTypeName + '&nbsp;&#149;&nbsp;' + data[i].transportMethod + '</option>';
                }
            }
            $('#' + selectBoxId).html(html);
        }
    });
}

function populateUsers(orgId, selectBoxId, connectionId) {

    var users = $('#' + selectBoxId).attr('rel');

    var url = "";

    if (selectBoxId === "srcUsersTable") {
        url = "getAvailableSendingUsers.do";
    } else {
        url = "getAvailableReceivingUsers.do";
    }

    // 'getAvailableUsers.do'
    $.ajax({
        url: url,
        type: "GET",
        data: {'orgId': orgId, 'connectionId': connectionId},
        success: function (data) {
            //get value of preselected col
            /*var html = '<option value="">- Select - </option>';
             var len = data.length;
             
             if(len > 1) {
             $('#'+selectBoxId+'Found').html("(" + len + " users found)");
             }
             else {
             $('#'+selectBoxId+'Found').html("(" + len + " user found)"); 
             }
             
             for (var i = 0; i < len; i++) {
             
             if(users != '' && users.indexOf(data[i].id) != -1) {
             if(data[i].userType == 1) {
             html += '<option value="' + data[i].id+ '" selected>' + data[i].firstName + ' ' + data[i].lastName +' (Manager) - ' + data[i].orgName + ' </option>';
             }
             else {
             html += '<option value="' + data[i].id + '" selected>' + data[i].firstName + ' ' + data[i].lastName +' (Staff Member) - ' + data[i].orgName + ' </option>'; 
             }
             }
             else {
             if(data[i].userType == 1) {
             html += '<option value="' + data[i].id + '">' + data[i].firstName + ' ' + data[i].lastName +' (Manager) - ' + data[i].orgName + ' </option>';
             }
             else {
             html += '<option value="' + data[i].id + '">' + data[i].firstName + ' ' + data[i].lastName +' (Staff Member) - ' + data[i].orgName + ' </option>'; 
             }
             }
             }*/
            $('#' + selectBoxId).html(data);
        }
    });
}




