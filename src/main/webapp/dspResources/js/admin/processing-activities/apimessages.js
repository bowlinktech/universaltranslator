/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        $("input:text,form").attr("autocomplete", "off");

        //This function will launch the status detail overlay with the selected
        //status
        $(document).on('click', '.viewPayload', function (event) {
            var wsId = $(this).attr('rel');
            $.ajax({
                url: '/administrator/processing-activity/apimessage/viewPayload.do',
                type: "POST",
                data: {'messageId': wsId},
                success: function (data) {
		    var fileName = "RestAPIPayload_"+wsId;
		    var blob = new Blob([data], { type: 'text/plain;charset=utf-8;' });
		    if (navigator.msSaveBlob) { // IE 10+
			navigator.msSaveBlob(blob, filename);
		    } else {
			var link = document.createElement("a");
			if (link.download !== undefined) { // feature detection
			    // Browsers that support HTML5 download attribute
			    var url = URL.createObjectURL(blob);
			    link.setAttribute("href", url);
			    link.setAttribute("download", fileName);
			    link.style.visibility = 'hidden';
			    document.body.appendChild(link);
			    link.click();
			    document.body.removeChild(link);
			}
		    }
                }
            });
        });
	
	//This will change between inbound and outbound
        $(document).on('change', '#apiDirection', function (event) {
            window.location.href = "/administrator/processing-activity/apimessagesOut";
        });


        var oSettings = datatable.fnSettings();

        datatable.fnSort([[6, 'desc']]);

    });
});


function searchByDateRange() {
    var fromDate = $('.daterange span').attr('rel');
    var toDate = $('.daterange span').attr('rel2');

    $('#fromDate').val(fromDate);
    $('#toDate').val(toDate);

    $('body').overlay({
        glyphicon: 'floppy-disk',
        message: 'Processing...'
    });

    $('#searchForm').submit();

}
