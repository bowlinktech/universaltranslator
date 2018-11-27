<%-- 
    Document   : referralActivityExport
    Created on : Mar 20, 2015, 1:26:25 PM
    Author     : chadmccue
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="col-md-9">

        <div class="row-fluid contain basic-clearfix">
            <form:form class="form form-inline" id="searchForm" action="/administrator/processing-activity/referralActivityExport" method="post">
                <div class="form-group">
                    <input type="hidden" name="fromDate" id="fromDate" rel="<fmt:formatDate value="${fromDate}" type="date" pattern="MM/dd/yyyy" />" rel2="<fmt:formatDate value="${originalDate}" type="date" pattern="MM/dd/yyyy" />" value="${fromDate}" />
                    <input type="hidden" name="toDate" id="toDate" rel="<fmt:formatDate value="${toDate}" type="date" pattern="MM/dd/yyyy" />" value="${toDate}" />
                </div>
            </form:form>
            <div class="form-container scrollable ">
                <div class="date-range-picker-trigger form-control pull-left daterange" style="width:285px; margin-right: 10px;">
                    <i class="glyphicon glyphicon-calendar"></i>
                    <span class="date-label"><fmt:formatDate value="${fromDate}" type="date" pattern="MMMM dd, yyyy" /> - <fmt:formatDate value="${toDate}" type="date" pattern="MMMM dd, yyyy" /></span> <b class="caret"></b>
                </div>
                <div class="pull-left">
                    <input type="button" id="cancel" class="btn btn-primary createExport" value="Request Export"/>
                </div>
            </div>
        </div>

        <section class="panel panel-default" style="margin-top: 10px">
            <div class="panel-heading">
                <h3 class="panel-title">Exports</h3>
            </div>
            <div class="panel-body">
                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default"  <c:if test="${not empty exports}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Status</th>
                                    <th scope="col">Export Date</th>
                                    <th scope="col">Selected Date Range</th>
                                    <th scope="col" class="center-text">Created By</th>
                                    <th scope="col" class="center-text"></th>

                                </tr>
                            </thead>
                            <tbody>
                            <c:if test="${fn:length(exports) > 0}">
                                <c:forEach var="export" items="${exports}">
                                    <tr>
                                        <td scope="row">
                                            ${export.statusName}
                                        </td>
                                        <td scope="row">
                                            <fmt:formatDate value="${export.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm:ss a" />
                                        </td>
                                        <td>
                                            ${export.selDateRange}
                                        </td>
                                        <td class="center-text">
                                            ${export.createdByName}
                                        </td>
                                        <td>
                                            <c:if test="${(export.statusId == 3 ||  export.statusId == 4) && fn:length(export.fileName) != 0}">
                                                <span class="glyphicon glyphicon-edit"></span>
                                                <a href="dlExport?i=${export.encryptedId}&v=${export.encryptedSecret}"  title="View this export" role="button">
                                                    Download Export
                                                </a><br/>
                                            </c:if>
                                            <span class="glyphicon glyphicon-edit"></span>
                                            <a href="delExport?i=${export.encryptedId}&v=${export.encryptedSecret}" title="Download Export">
                                                Delete Export
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:if>
                            <c:if test="${fn:length(exports) < 1}">
                                <tr>
                                    <td colspan="4">
                                        No export has been generated.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>        

    </div>
</div>             