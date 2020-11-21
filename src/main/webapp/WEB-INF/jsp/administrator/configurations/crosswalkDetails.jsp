<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${crosswalkDetails.id > 0}">View</c:when><c:when test="${btnValue == 'Create'}">Add</c:when></c:choose> Crosswalk ${success}</h3>
        </div>
        <div class="modal-body">
            <div class="alert alert-danger uploadError" role="alert" style="display:none;">
                The uploaded file did not have the delimiter you selected.
            </div>
            <div class="alert alert-success uploadSuccess" role="alert" style="display:none;">
                The crosswalk file was successfully uploaded.
            </div>

            <form:form id="crosswalkdetailsform" commandName="crosswalkDetails" modelAttribute="crosswalkDetails" enctype="multipart/form-data" method="post" role="form">
                <form:hidden path="id" id="id" />
                <form:hidden path="dateCreated" />
                <input type="hidden" name="orgId" value="${orgId}" />
                <div class="form-container">
                    <spring:bind path="name">
                        <div id="crosswalkNameDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="name">Crosswalk Name *</label>
                            <c:choose>
                                <c:when test="${crosswalkDetails.id > 0 }">
                                    <form:hidden path="name" />
                                    <br />${crosswalkDetails.name}
                                </c:when>
                                <c:otherwise>
                                    <form:input path="name" id="name" class="form-control" type="text" maxLength="45" />
                                    <span id="crosswalkNameMsg" class="control-label"></span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </spring:bind>
                    <spring:bind path="fileDelimiter">
                        <div id="crosswalkDelimDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="name">Delimiter *</label>
                            <form:select path="fileDelimiter" id="delimiter" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${delimiters}" var="cwalk" varStatus="dStatus">
                                    <option value="${delimiters[dStatus.index][0]}" <c:if test="${crosswalkDetails.fileDelimiter == delimiters[dStatus.index][0]}">selected</c:if>>${delimiters[dStatus.index][1]} </option>
                                </c:forEach>
                            </form:select>
                            <span id="crosswalkDelimMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <c:if test="${crosswalkDetails.id > 0 }">
                        <c:choose>
                            <c:when test="${not empty cleanOrgURL}">
                                <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=config&filename=${crosswalkDetails.fileName}&foldername=${cleanOrgURL}"/>
                                <div class="form-group">
                                    <label class="control-label" >Existing Crosswalk File</label>
                                    <p>${crosswalkDetails.fileName}</p>
                                    <p><a href="${hrefLink}" title="Download Crosswalk File">Click here to download the file.</a></p>
                                </div>
                            </c:when>
                            <c:when test="${crosswalkDetails.orgId == 0}">
                                <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=crosswalks&filename=${crosswalkDetails.fileName}&foldername=libraryFiles"/>
                                <div class="form-group">
                                    <label class="control-label" >Existing Crosswalk File</label>
                                    <p><a href="${hrefLink}" title="Download Crosswalk File">${crosswalkDetails.fileName}</a></p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="form-group">
                                    <label class="control-label" >Existing Crosswalk File</label>
                                    <p>${crosswalkDetails.fileName}</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    <spring:bind path="file">
                        <div id="crosswalkFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="crosswalkFile"><c:if test="${crosswalkDetails.id > 0 }">New </c:if>Crosswalk File *</label>
                            <form:input path="file" id="crosswalkFile" type="file"  />
                            <span id="crosswalkFileMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitCrosswalkButton" rel="${actionValue}" class="btn btn-primary" value="${btnValue}"/>
                    </div>

                    <c:if test="${crosswalkDetails.id > 0}">
                        <div id="crosswalkNameDiv" class="form-group">
                            <label class="control-label" for="data">Crosswalk Data</label>
                            <br />
                            <div style="height:350px; width: 550px;">
                                <div style="overflow-x: scroll; height: 100%; display: flex; display: -webkit-flex; flex-direction: column; -webkit-flex-direction: column;">
                                    <table style="border-bottom:1px solid;margin: 0px; padding:0px;">
                                        <thead>
                                            <tr>
                                                <th scope="col" style="width: 250px">Source Value</th>
                                                <th scope="col" style="width: 150px;">Target Value</th>
                                                <th scope="col" style="width: 150px;">Description Value</th>
                                            </tr>
                                        </thead>
                                    </table>
                                    <div style="overflow-y: scroll;">
                                        <table class="table-hover" style="border-collapse: collapse;">
                                             <tbody>
                                                <c:forEach items="${crosswalkData}" var="cwalkData" varStatus="dStatus">
                                                    <tr>
                                                        <td valign="top" scope="row" style="width: 250px;border-bottom:1px dashed; border-right:1px dashed;">${crosswalkData[dStatus.index][0]}</td>
                                                        <td valign="top" style="width: 150px;border-bottom:1px dashed; border-right:1px dashed;">${crosswalkData[dStatus.index][1]}</td>
                                                        <td valign="top" style="width: 150px;border-bottom:1px dashed;">${crosswalkData[dStatus.index][2]}</td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>

                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");


    });

</script>