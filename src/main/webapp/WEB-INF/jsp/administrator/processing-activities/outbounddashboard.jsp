<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


          
<table class="table table-dash-bordered table-default"  <c:if test="${not empty outboundbatches}">id="outbounddataTable"</c:if>>
    <thead class="thead-light">
	<tr>
	    <th scope="col" class="center-text">Type</th>
	    <th scope="col" class="center-text">Date/Time</th>
	    <th scope="col" class="center-text">Trans. ID</th>
	    <th scope="col" class="center-text">Target</th>
	    <th scope="col" class="center-text">Message Type</th>
	    <th scope="col" class="center-text">Interface</th>
	    <th scope="col" class="center-text">Total Trans.</th>
	    <th scope="col" class="center-text">Total Errors</th>
	    <th scope="col" class="center-text">Threshold</th>
	</tr>
    </thead>
    <tbody>
	<c:choose>
	    <c:when test="${not empty outboundbatches}">
		<c:forEach var="outboundbatch" items="${outboundbatches}">
		     <tr class="${outboundbatch.dashboardRowColor} outboundbatchRow" data-trigger="hover" data-toggle="popover" data-placement="top" data-html="true" title="File Status" data-content="${outboundbatch.statusValue}">
			<td scope="row" class="center-text">On Demand</td>
			<td scope="row" class="center-text"><fmt:formatDate value="${outboundbatch.dateCreated}" type="both" pattern="M/dd/yy H:mm" /></td>
			<td scope="row">
			    <a href="<c:url value='/administrator/processing-activity/outbound/${outboundbatch.utBatchName}' />" title="View Outbound Batch" class="dashboard-link" role="button">${outboundbatch.utBatchName}</a>
			</td>
			<td scope="row">
			    <c:choose>
				<c:when test="${outboundbatch.orgName == 'HMIS Internal'}">
				    HDR
				</c:when>
				<c:otherwise>${outboundbatch.orgName}</c:otherwise>
			    </c:choose>
			</td>
			<td scope="row">${outboundbatch.configName}</td>
			<td scope="row" class="center-text">
			     <c:choose>
				<c:when test="${outboundbatch.transportMethod == 'Internal HMIS'}">
				    File Drop
				</c:when>
				<c:otherwise>${outboundbatch.transportMethod}</c:otherwise>
			    </c:choose>
			</td>
			<td scope="row" class="center-text">
			    <fmt:formatNumber value = "${outboundbatch.totalRecordCount}" type = "number"/>
			    <c:if test="${fn:contains(outboundbatch.configName, 'Service Received')}">
				<a href="#" data-container="body" data-toggle="tooltip" data-placement="top" data-html="true" data-original-title="<p style='width:350px;'><p style='text-align:left'>This number is based on unique patients, multiple services received have been grouped togther under a single visit.</p><p style='text-align:left'>This only applies for service received message types.</p></p>">
				    <span class="glyphicon glyphicon-exclamation-sign" style="cursor:pointer;"></span>
				</a>
			    </c:if>
			</td>
			<td scope="row" class="center-text"><fmt:formatNumber value = "${outboundbatch.errorRecordCount}" type = "number"/></td>
			<td scope="row" class="center-text">${outboundbatch.threshold}%</td>
		    </tr>
		</c:forEach>     
	    </c:when>   
	    <c:otherwise>
		<tr><td colspan="9" class="center-text">There are currently no outbound files for the selected date range.</td></tr>
	    </c:otherwise>
	</c:choose>           
    </tbody>
</table>
    
<script>
    
    $(function () {
        $(".outboundbatchRow").popover();
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
    
    $('#outbounddataTable').dataTable({
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
	    { "sWidth": "10%" },
	    { "sWidth": "10%" },
	    { "sWidth": "15%" },
	    { "sWidth": "20%" },
	    { "sWidth": "20%" },
	    { "sWidth": "10%" },
	    { "sWidth": "5%" },
	    { "sWidth": "5%" },
	    { "sWidth": "5%" }
	],
       "aaSorting" : [[1, "desc"]]
    });
</script>