<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${configurationNote.id > 0}">Edit</c:when><c:otherwise>Add</c:otherwise></c:choose> Configuration Note ${success}</h3>
        </div>
        <div class="modal-body">
           
            <form:form id="configurationNoteForm" commandName="configurationNote" modelAttribute="configurationNote" method="post" role="form">
                <form:hidden path="id" id="id" />
                <form:hidden path="dateCreated" />
                <form:hidden path="configId" />
                <form:hidden path="userId" />
                <div class="form-container">
                    <spring:bind path="updateMade">
                        <div id="updateMadeDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="updateMade">Note *</label>
                            <form:textarea path="updateMade" id="updateMade" class="form-control" rows="10" cols="10" />
                             <span id="updateMadeMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                    <div class="form-group">
                        <input type="button" id="submitConfigurationNote" class="btn btn-primary" value="Save Note"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>
