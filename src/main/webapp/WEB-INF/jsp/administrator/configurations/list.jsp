<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">
	 <c:choose>
	    <c:when test="${not empty savedStatus}" >
		<div class="alert alert-success">
		    <strong>Success!</strong> 
		    <c:choose>
			<c:when test="${savedStatus == 'updated'}">The configuration has been successfully updated!</c:when>
			<c:when test="${savedStatus == 'created'}">The configuration has been successfully added!</c:when>
			<c:when test="${savedStatus == 'deleted'}">The configuration has been successfully removed!</c:when>
		    </c:choose>
		</div>
	    </c:when>
	    <c:when test="${not empty param.msg}" >
		<div class="alert alert-success">
		    <strong>Success!</strong> 
		    <c:choose>
			<c:when test="${param.msg == 'updated'}">The configuration has been successfully saved!</c:when>
			<c:when test="${param.msg == 'deleted'}">The configuration has been successfully removed!</c:when>
		    </c:choose>
		</div>
	    </c:when>
	</c:choose>
        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable"><br />
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty configurationList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
				    <th scope="col" class="center-text">Id</th>
                                    <th scope="col">Configuration Name</th>
                                    <th scope="col">Organization</th>
				    <th scope="col">Message Type</th>
                                    <th scope="col" class="center-text">Type</th>
                                    <th scope="col" class="center-text">Transport Method</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty configurationList}">
                                    <c:forEach var="config" items="${configurationList}">
                                        <tr>
					    <td scope="row" class="center-text">${config.id}</td>
                                            <td >
						<a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">${config.configName}</a>
                                                <br />
                                                (<c:choose><c:when test="${config.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)
                                            </td>
                                           <td>
                                                ${config.orgName}
                                            </td>
					     <td>
                                                ${config.messageTypeName}
                                            </td>
                                            <td class="center-text">
                                                <c:choose><c:when test="${config.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose>
                                           </td>
                                            <td class="center-text">
                                                <c:choose><c:when test="${config.transportMethod == 'File Upload'}"><c:choose><c:when test="${config.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${config.transportMethod}</c:otherwise></c:choose>
                                            </td>
                                            <td class="center-text">
						<fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy" />
					    </td>
                                            <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
							<c:if test="${config.type == 1 && config.status == true}">
							    <li>
								<a href="javascript:void(0);" class="copyConfig" rel="${config.id}" title="Copy this Configuration">
								    <span class="glyphicon glyphicon-transfer"></span>
								    Copy
								</a>
							    </li>
							    <li class="divider"></li>
							</c:if>
							<li>
							    <a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">
								<span class="glyphicon glyphicon-edit"></span>
								Edit
							    </a>
							</li>
							<li>
							    <a href="javascript:void(0);" class="deleteConfig" rel="${config.id}" title="Delete this configuration">
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
                                    <tr><td colspan="8" class="center-text">There are currently no configurations set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>		
</div>