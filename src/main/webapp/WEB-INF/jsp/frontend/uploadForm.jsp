<%-- 
    Document   : uploadForm
    Created on : Jan 21, 2014, 11:15:37 AM
    Author     : chadmccue
--%>

<head>
    <meta http-equiv="Content-Language" content="en">
</head>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Upload File</h3>
        </div>
        <div class="modal-body">
            <form:form id="fileUploadForm" action="submitFileUpload" enctype="multipart/form-data" method="post" role="form">
                <div id="configIdsDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label for="configIds">Select the message types that will be in the file *</label>
                    <select id="configIds" name="configId" class="form-control half">
                        <option  value="">- Message Types - </option>
                        <c:forEach items="${configurations}" var="config">
                            <option value="${config.key}">${config.value}</option>
                        </c:forEach>
                        <c:if test="${allowmultipleMessageTypes == true}"><option value="0">Multiple Message Types</option>  </c:if>  
                    </select>
                    <span id="configIdsMsg" class="control-label"></span>
                </div>
                <div id="uploadedFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label for="uploadedFile">File *</label>
                    <input id="uploadedFile" name="uploadedFile" class="form-control" type="file" />
                    <span id="uploadedFileMsg" class="control-label"></span>
                </div>

                <div class="form-group">
                    <input class="btn btn-primary btn-sm" id="submitButton" type="submit" value="Upload"/>
                    <a data-dismiss="modal" class="btn btn-secondary btn-sm" data-toggle="tab">
                        Cancel
                    </a>
                </div>
           </form:form>
        </div>
    </div>
</div>
