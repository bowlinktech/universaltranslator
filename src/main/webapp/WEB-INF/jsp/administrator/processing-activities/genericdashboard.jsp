<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


          
<table class="table table-dash-bordered table-default"  <c:if test="${not empty genericbatches}">id="genericdataTable"</c:if>>
    <thead class="thead-light">
	<tr>
	    <th scope="col" class="center-text">Completed</th>
	    <th scope="col" class="center-text">Date/Time</th>
	    <th scope="col">Message</th>
	</tr>
    </thead>
    <tbody>
	<c:choose>
	    <c:when test="${not empty genericbatches}">
		<c:forEach var="generic" items="${genericbatches}">
		     <tr id="genericRow_${generic.id}" class="${generic.dashboardRowColor}">
			 <td scope="row" class="center-text">
			    <input class="complateGenericEntry" rel="${generic.id}" type="checkbox" name="completed" <c:if test="${generic.watchListCompleted == true}">checked="checked"</c:if> />
			 </td>
			<td scope="row" class="center-text"><fmt:formatDate value="${generic.dateSubmitted}" type="both" pattern="M/dd/yy h:mm a" /></td>
			<td scope="row">
			    ${generic.entryMessage}
			</td>
		    </tr>
		</c:forEach>     
	    </c:when>   
	    <c:otherwise>
		<tr><td colspan="3" class="center-text">There are currently no generic messages for the selected date range.</td></tr>
	    </c:otherwise>
	</c:choose>           
    </tbody>
</table>
        
<script>
    $(document).on('click', '.complateGenericEntry', function () {
	var entryId = $(this).attr('rel');
	
	if(this.checked) {
	    $("#genericRow_"+entryId).removeClass("table-primary");
	    $("#genericRow_"+entryId).addClass("table-success");
	}
	else {
	    $("#genericRow_"+entryId).removeClass("table-success");
	    $("#genericRow_"+entryId).addClass("table-primary");
	}
	
	$.ajax({
	    url: '/administrator/processing-activity/completeGenericWatchList',
	    data: {
		'entryId': entryId, 
		'isChecked': this.checked
	    },
	    type: "POST",
	    success: function(data) {}
	});
	
    });
    
    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });

    /* API method to get paging information */
    $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings)
    {
        return {
            "iStart": oSettings._iDisplayStart,
            "iEnd": oSettings.fnDisplayEnd(),
            "iLength": oSettings._iDisplayLength,
            "iTotal": oSettings.fnRecordsTotal(),
            "iFilteredTotal": oSettings.fnRecordsDisplay(),
            "iPage": Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
            "iTotalPages": Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
        };
    }

    /* Bootstrap style pagination control */
    $.extend($.fn.dataTableExt.oPagination, {
        "bootstrap": {
            "fnInit": function (oSettings, nPaging, fnDraw) {
                var oLang = oSettings.oLanguage.oPaginate;
                var fnClickHandler = function (e) {
                    e.preventDefault();
                    if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                        fnDraw(oSettings);
                    }
                };
                $(nPaging).append(
                        '<ul class="pagination pull-right">' +
                        '<li class="prev disabled"><a href="#">&laquo;</a></li>' +
                        '<li class="next disabled"><a href="#">&raquo;</a></li>' +
                        '</ul>'
                        );
                var els = $('a', nPaging);
                $(els[0]).bind('click.DT', {action: "previous"}, fnClickHandler);
                $(els[1]).bind('click.DT', {action: "next"}, fnClickHandler);
            },
            "fnUpdate": function (oSettings, fnDraw) {
                var iListLength = 5;
                var oPaging = oSettings.oInstance.fnPagingInfo();
                var an = oSettings.aanFeatures.p;
                var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

                if (oPaging.iTotalPages < iListLength) {
                    iStart = 1;
                    iEnd = oPaging.iTotalPages;
                } else if (oPaging.iPage <= iHalf) {
                    iStart = 1;
                    iEnd = iListLength;
                } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
                    iStart = oPaging.iTotalPages - iListLength + 1;
                    iEnd = oPaging.iTotalPages;
                } else {
                    iStart = oPaging.iPage - iHalf + 1;
                    iEnd = iStart + iListLength - 1;
                }

                for (i = 0, iLen = an.length; i < iLen; i++) {
                    // Remove the middle elements
                    $('li:gt(0)', an[i]).filter(':not(:last)').remove();

                    // Add the new list items and their event handlers
                    for (j = iStart; j <= iEnd; j++) {
                        sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
                        $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                                .insertBefore($('li:last', an[i])[0])
                                .bind('click', function (e) {
                                    e.preventDefault();
                                    oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                                    fnDraw(oSettings);
                                });
                    }

                    // Add / remove disabled classes from the static elements
                    if (oPaging.iPage === 0) {
                        $('li:first', an[i]).addClass('disabled');
                    } else {
                        $('li:first', an[i]).removeClass('disabled');
                    }

                    if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
                        $('li:last', an[i]).addClass('disabled');
                    } else {
                        $('li:last', an[i]).removeClass('disabled');
                    }
                }
            }
        }
    });
    
    $('#genericdataTable').dataTable({
	"bAutoWidth": false,
	"bStateSave": true,
	"iCookieDuration": 60,
	"sPaginationType": "bootstrap",
	"oLanguage": {
	    "sSearch": "_INPUT_",
	    "sLengthMenu": '<select class="form-control" style="width:150px">' +
		    '<option value="10">10 Records</option>' +
		    '<option value="20">20 Records</option>' +
		    '<option value="30">30 Records</option>' +
		    '<option value="40">40 Records</option>' +
		    '<option value="50">50 Records</option>' +
		    '<option value="-1">All</option>' +
		    '</select>'
	},
       "aoColumns" : [
	    { "sWidth": "5%" },
	    { "sWidth": "10%" },
	    { "sWidth": "85%" }
	],
       "aaSorting" : [[1, "desc"]]
    });
</script>