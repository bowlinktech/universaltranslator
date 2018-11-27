<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose><c:when test="${savedStatus == 'updated'}">The message type has been successfully updated!</c:when><c:otherwise>The message type has been successfully created!</c:otherwise></c:choose>
                    </div>
        </c:if>

        <form:form id="messageType" commandName="messageTypeDetails" modelAttribute="messageTypeDetails" method="post" enctype="multipart/form-data" role="form">
            <input type="hidden" id="action" name="action" value="save" />
            <form:hidden path="id" id="id" />
            <form:hidden path="dateCreated" />

            <section class="panel panel-default">

                <div class="panel-heading">
                    <h3 class="panel-title">Details</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container">
                        <div class="form-group">
                            <label for="status">Status *</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="1"/>Active 
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="0"/>Inactive
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="2"/>Archived
                                </label>
                            </div>
                        </div>
                        <spring:bind path="name">
                            <div class="form-group ${status.error ? 'has-error' : '' } ${not empty existingType ? 'has-error' : ''}">
                                <label class="control-label" for="name">Name *</label>
                                <form:input path="name" id="name" class="form-control" type="text" maxLength="255" />
                                <form:errors path="name" cssClass="control-label" element="label" />
                                <c:if test="${not empty existingType}"><span class="control-label has-error">${existingType}</span></c:if>
                                </div>
                        </spring:bind>
                        <spring:bind path="dspName">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="dspName">Display Name *</label>
                                <form:input path="dspName" id="dspName" class="form-control" type="text" maxLength="255" />
                                <form:errors path="dspName" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>      
                        <c:choose>
                            <c:when test="${empty id}">
                                <spring:bind path="file">
                                    <div id="templateFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="templateFile">Template File * <span class="badge badge-help"  data-toggle="tooltip" data-placement="top" title="" data-original-title="Explain the file types and etc">?</span></label>
                                        <form:input path="file" id="templateFile" type="file" />
                                        <span id="templateFileMsg" class="control-label"></span>
                                    </div>
                                </spring:bind>
                            </c:when>
                            <c:otherwise>
                                <div id="templateFileDiv" class="form-group">
                                    <label class="control-label" for="templateFile">Uploaded Template File:</label>
                                    <form:input path="templateFile" id="templateFile" class="form-control" readonly="true" />
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </section>
        </form:form>
    </div>
</div>