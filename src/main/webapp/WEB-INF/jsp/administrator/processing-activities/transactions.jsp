<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Batch Summary:</dt>
                    <dd><strong>Batch ID:</strong>
                        <c:choose>
                            <c:when test="${batchDetails.transportMethodId == 1 ||batchDetails.transportMethodId == 3 || batchDetails.transportMethodId == 5}">
                                <c:set var="text" value="${fn:split(batchDetails.originalFileName,'.')}" />
                                <c:set var="ext" value="${text[fn:length(text)-1]}" />
                                <a href="/FileDownload/downloadFile.do?filename=${batchDetails.utBatchName}.${ext}&foldername=archivesIn" title="View Original File">
                                    ${batchDetails.originalFileName}
                                </a>
                            </c:when>
                            <c:otherwise>
                                ${batchDetails.utBatchName}
                            </c:otherwise>
                        </c:choose></dd>
                    <dd><strong>Source Organization:</strong> ${batchDetails.orgName}</dd>
                    <dd><strong>Date Submitted:</strong> <fmt:formatDate value="${batchDetails.dateSubmitted}" type="date" pattern="M/dd/yyyy" />&nbsp@&nbsp;<fmt:formatDate value="${batchDetails.dateSubmitted}" type="time" pattern="h:mm:ss a" /></dd>
                    </dt>
                </div>
            </section>
        </div>
    </div>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-body">
                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty transactions}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Message Type</th>
                                    <th scope="col">Target Organization</th>
                                    <th scope="col">Reportable Field 1</th>
                                    <th scope="col">Reportable Field 2</th>
                                    <th scope="col">Reportable Field 3</th>
                                    <th scope="col">Reportable Field 4</th>
                                    <%--<th scope="col" class="center-text">Open/Closed</th>--%>
				<th scope="col" class="center-text">Status</th>
				<th scope="col"></th>
			    </tr>
			</thead>
			<tbody>
                            <c:choose>
                                <c:when test="${not empty transactions}">
                                    <c:forEach var="transaction" items="${transactions}">
                                        <tr>
                                            <td scope="row">${transaction.messageTypeName}</td>
                                            <td>
                                                ${transaction.targetOrgFields[0].fieldValue}
                                    <dd class="adr">
                                        <span class="street-address">${transaction.targetOrgFields[1].fieldValue}</span><br/>
                                        <c:if test="${not empty transaction.targetOrgFields[2].fieldValue and transaction.targetOrgFields[2].fieldValue != 'null'}"><span class="street-address">${transaction.targetOrgFields[2].fieldValue}</span><br/></c:if>
                                        <c:if test="${not empty transaction.targetOrgFields[4].fieldValue and transaction.targetOrgFields[4].fieldValue != 'null'}"><span class="region">${transaction.targetOrgFields[3].fieldValue}&nbsp;${transaction.targetOrgFields[4].fieldValue}</span>,&nbsp;</c:if> <span class="postal-code">${transaction.targetOrgFields[5].fieldValue}</span>
                                        </dd>
                                    <c:if test="${not empty transaction.targetOrgFields[6].fieldValue and transaction.targetOrgFields[6].fieldValue != 'null'}"><dd>phone: <span class="tel">${transaction.targetOrgFields[6].fieldValue}</span></dd></c:if>
                                    <c:if test="${not empty transaction.targetOrgFields[7].fieldValue and transaction.targetOrgFields[7].fieldValue != 'null'}"><dd>fax: <span class="tel">${transaction.targetOrgFields[7].fieldValue}</span></dd></c:if>
                                        </td>
                                        <td>
                                        <c:choose>
                                            <c:when test="${transaction.reportableField1 != null}">
                                                (${transaction.reportableFieldHeading1})
                                                <br />
                                                ${transaction.reportableField1}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${transaction.reportableField2 != null}">
                                                (${transaction.reportableFieldHeading2})
                                                <br />
                                                ${transaction.reportableField2}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${transaction.reportableField3 != null}">
                                                (${transaction.reportableFieldHeading3})
                                                <br />
                                                ${transaction.reportableField3}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${transaction.reportableField4 != null}">
                                                (${transaction.reportableFieldHeading4})
                                                <br />
                                                ${transaction.reportableField4}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <%--<td class="center-text">
                                        <c:choose>
                                            <c:when test="${transaction.messageStatus == 1}">
                                                Open
                                            </c:when>
                                            <c:otherwise>Closed</c:otherwise>
                                        </c:choose>
                                    </td>--%>
                                    <td class="center-text">
                                        <a href="#statusModal" data-toggle="modal" class="btn btn-link viewStatus" rel="${transaction.statusId}" title="View this Status">${transaction.statusValue}</a>
                                    </td>
                                    <td class="actions-col" style="width:50px;">
                                        <a href="#messageDetailsModal" data-toggle="modal" rel="${transaction.transactionRecordId}" rel2="${transaction.configId}" class="btn btn-link viewLink">
                                            <span class="glyphicon glyphicon-edit"></span>
                                            View
                                        </a>
                                    </td>
                                    </tr>
                                </c:forEach>     
                            </c:when>  
                            <c:when test="${not empty toomany}">
                                <tr>
                                    <td colspan="7">
                                        There were a total of ${size} transactions found, please enter a parameter to narrow down the results.
                                        <br />You can search by Message Type, Target Org or anything reportable field.
                                    </td>
                                </tr>
                            </c:when>
                            <c:when test="${not empty stilltoomany}">
                                <tr>
                                    <td colspan="7">T
                                        Your search produced ${size} results, please narrow down further.
                                    </td>
                                </tr>
                            </c:when>   
                            <c:otherwise>
                                <tr><td colspan="7" class="center-text">There are currently no transactions within this batch.</td></tr>
                            </c:otherwise>
                        </c:choose>           
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>
</div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>