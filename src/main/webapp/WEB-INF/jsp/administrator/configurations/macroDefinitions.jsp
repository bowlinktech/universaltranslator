<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog modal-dialog-big">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Macro Definitions</h3>
	</div>
	<div class="modal-body" style="max-height:800px; overflow: auto">
	    <table>
		<thead>
		    <tr>
			<th scope="col">Macro Name</th>
                        <th scope="col">Macro Details</th>
                        <th scope="col"></th>
		    </tr>
		</thead>
		<tbody>
		    <c:choose>
			<c:when test="${not empty macros}">
			    <c:forEach items="${macros}" var="macro">
				<tr style="border-top: 1px solid #dddddd; border-right: 1px solid #dddddd; border-left: 1px solid #dddddd; border-bottom: 1px solid #dddddd;">
                                    <td scope="row" style="vertical-align:top;"><strong>${macro.macroName}</strong></td>
				   <td>
                                       ${macro.macroDesc}
                                       <p style="padding-top:10px;">
                                           <button type="button" class="btn btn-info btn-xs showMore" rel="${macro.id}" title="Show More Details">Show More Details</button>
                                       </p>
                                   </td>
                                    <td style="vertical-align: middle">
                                           <button type="button" class="btn btn-primary btn-xs useMacro"  data-dismiss="modal" rel="${macro.id}" title="Use This Macros">Use This Macro</button>
				    </td>
				</tr>
                                <tr id="macro-${macro.id}" style="display:none;">
                                    <td colspan="3" style="background-color:#f5f5f5; border: 1px solid black;">
                                        <div class="col-md-12">
                                            <div class="col-md-6">
                                                <p style="height:75px; max-height:750px;"><strong>Error Condition: </strong><br />
                                                <c:choose><c:when test="${empty macro.errorCondition}">N/A</c:when><c:otherwise>${macro.errorCondition}</c:otherwise></c:choose></p>
                                                <p style="padding-top:10px;"><strong>Pass/Clear Logic: </strong><br />
                                                <c:choose><c:when test="${empty macro.passClearLogic}">N/A</c:when><c:otherwise>${macro.passClearLogic}</c:otherwise></c:choose></p>
                                            </div>
                                            <div class="col-md-6">
                                                <p style="height:75px; max-height:750px;"><strong>Dropped Value Logging: </strong><br />
                                                <c:choose><c:when test="${empty macro.droppedValueLogging}">N/A</c:when><c:otherwise>${macro.droppedValueLogging}</c:otherwise></c:choose></p>
                                                <p style="padding-top:10px;"><strong>Reject File/ Reject Record: </strong><br />
                                                <c:choose><c:when test="${empty macro.rejectRecordFile}">N/A</c:when><c:otherwise>${macro.rejectRecordFile}</c:otherwise></c:choose></p>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
			    </c:forEach>
			</c:when>
			<c:otherwise>
			    <tr><td colspan="2">There are currently no macros found for this section.</td></tr>
			</c:otherwise>
		    </c:choose>
		</tbody>
	    </table>
	</div>
    </div>
</div>
