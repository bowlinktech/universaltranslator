<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:choose>
                <c:when test="${not empty savedStatus}" >
                    <div class="alert alert-success">
                        <c:choose>
                            <c:when test="${savedStatus == 'updated'}">The configuration note has been successfully updated!</c:when>
                            <c:when test="${savedStatus == 'created'}">The configuration note has been successfully created!</c:when>
                            <c:when test="${savedStatus == 'deleted'}">The configuration note has been successfully removed!</c:when>
                        </c:choose>
                    </div>
                </c:when>
                <c:when test="${not empty param.msg}" >
                    <div class="alert alert-success">
                        <strong>Success!</strong> 
                        <c:choose>
                            <c:when test="${param.msg == 'updated'}">The configuration note has been successfully updated!</c:when>
                            <c:when test="${param.msg == 'created'}">The configuration note has been successfully added!</c:when>
                            <c:when test="${param.msg == 'deleted'}">The configuration note has been successfully removed!</c:when>
                        </c:choose>
                    </div>
                </c:when>
            </c:choose>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Configuration Summary:</dt>
			<dd><strong>Organization:</strong> ${configurationDetails.orgName}</dd>
			<dd><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></dd>
                    	<dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
			<dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
		    </dt>
		</div>
	    </section>   
	</div>
    </div>
                    
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <div class="pull-right">
                        <a href="#configNoteModal" data-toggle="modal" rel="${configurationDetails.id}" class="btn btn-primary btn-xs btn-action" id="createNewNote" title="Add New Configuration Note">Add New Configuration Note</a>
                    </div>
                    <h3 class="panel-title">Configuration Notes</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container scrollable">
                        <table class="table table-striped table-hover table-default" <c:if test="${not empty configurationNotes}">id="configurationnotes"</c:if>>
                        <thead>
                            <tr>
                                <th scope="col" style="width:70%">Note</th>
                                <th scope="col" style="width:10%">Created By</th>
                                <th scope="col" class="center-text" style="width:15%">Date Created</th>
                                <th scope="col" style="width:5%"></th>
                            </tr>
                        </thead>
                        <tbody>
                             <c:choose>
                                 <c:when test="${not empty configurationNotes}">
                                     <c:forEach var="note" items="${configurationNotes}">
                                         <tr>
                                             <td>
                                                 ${note.updateMade}
                                             </td>
                                             <td>
                                                ${note.usersName}
                                             </td>
                                             <td class="center-text">
                                                 <fmt:formatDate value="${note.dateCreated}" type="date" pattern="M/dd/yyyy h:mm a" />
                                             </td>
                                             <td>
                                                 <div class="dropdown pull-left">
                                                     <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
                                                         <i class="fa fa-cog"></i>
                                                     </button>
                                                     <ul class="dropdown-menu pull-right">
                                                         <li>
                                                              <a href="#configNoteModal" data-toggle="modal" class="editNote" rel="${note.id}" title="Edit this Note">
                                                                 <span class="glyphicon glyphicon-edit"></span>
                                                                 Edit
                                                             </a>
                                                         </li>
                                                         <li>
                                                             <a href="javascript:void(0);" class="deleteNote" rel="${note.id}" title="Delete this Note">
                                                                 <span class="glyphicon glyphicon-remove-circle"></span>
                                                                 Delete
                                                             </a>
                                                         </li>
                                                     </ul>
                                                 </div>
                                             </td>
                                         </tr>
                                     </c:forEach>
                                 </c:when>
                                 <c:otherwise>
                                     <tr><td colspan="4" class="center-text">There are currently no configuration notes.</td></tr>
                                 </c:otherwise>
                             </c:choose>
                         </tbody>
                     </table>
                   </div>
                </div>
            </section>
        </div>
    </div>
</div>
<div class="modal fade" id="configNoteModal" role="dialog" tabindex="-1" aria-labeledby="Configuration Note" aria-hidden="true" aria-describedby="Configuration Note"></div>
