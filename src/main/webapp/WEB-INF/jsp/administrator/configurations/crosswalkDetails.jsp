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
                        <div class="form-group">
                            <label class="control-label" >Existing Crosswalk File</label>
                            <p>${crosswalkDetails.fileName}</p>
                        </div>
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
                            <table class="table table-striped table-hover table-default">
                                <thead>
                                    <tr>
                                        <th scope="col">Source Value</th>
                                        <th scope="col" class="center-text">Target Value</th>
                                        <th scope="col">Description Value</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${crosswalkData}" var="cwalkData" varStatus="dStatus">
                                        <tr>
                                            <td scope="row">${crosswalkData[dStatus.index][0]}</td>
                                            <td class="center-text">${crosswalkData[dStatus.index][1]}</td>
                                            <td>${crosswalkData[dStatus.index][2]}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>

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