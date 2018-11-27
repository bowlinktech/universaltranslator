<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'updated'}">The scheduled watch entry has been successfully updated!</c:when>
                    <c:when test="${savedStatus == 'created'}">The scheduled watch entry has been successfully added!</c:when>
                    <c:when test="${savedStatus == 'deleted'}">The scheduled watch entry has been successfully removed!</c:when>
                </c:choose>
            </div>
        </c:if>

        <section class="panel panel-default">
            <div class="panel-body">

                <div class="form-container scrollable"><br />
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty watchList}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
				    <td scope="col">Source</th>
				    <th scope="col">Message Type</th>
                                    <th scope="col">Interface</th>
				    <th scope="col">Message</th>
                                    <th scope="col" class="center-text">Repeated</th>
                                    <th scope="col" class="center-text">Start alert on</th>
                                    <th scope="col" class="center-text">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty watchList}">
                                    <c:forEach var="watchEntry" items="${watchList}">
                                        <tr>
                                            <td scope="row">
						<c:choose>
						    <c:when test="${not empty watchEntry.orgName}">
							${watchEntry.orgName}
						    </c:when>
						    <c:otherwise>
							N/A
						    </c:otherwise>
						</c:choose>
                                            </td>
                                            <td>
						<c:choose>
						    <c:when test="${not empty watchEntry.messageTypeName}">
							${watchEntry.messageTypeName}
						    </c:when>
						    <c:otherwise>
							N/A
						    </c:otherwise>
						</c:choose>
                                            </td>
                                            <td>
						<c:choose>
						    <c:when test="${not empty watchEntry.transportMethod}">
							${watchEntry.transportMethod}
						    </c:when>
						    <c:otherwise>
							N/A
						    </c:otherwise>
						</c:choose>
                                            </td>
					    <td>
						<c:choose>
						    <c:when test="${not empty watchEntry.entryMessage}">
							${watchEntry.entryMessage}
						    </c:when>
						    <c:otherwise>
							N/A
						    </c:otherwise>
						</c:choose>
                                            </td>
                                            <td class="center-text">
                                                ${watchEntry.expected}
                                            </td>
                                            <td class="center-text"> 
                                                ${watchEntry.expectFirstFile}
						<c:if test="${not empty watchEntry.expectFirstFileTime}"> @ ${watchEntry.expectFirstFileTime}</c:if>
                                            </td>
                                            <td class="center-text"><fmt:formatDate value="${watchEntry.dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                                           <td>
						<div class="dropdown pull-left">
						    <button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
							<i class="fa fa-cog"></i>
						    </button>
						    <ul class="dropdown-menu pull-right">
							<li>
							    <a href="#watchEntryModal" data-toggle="modal" rel="${watchEntry.id}" class="watchEntryEdit" title="Edit this watch entry">
								<span class="glyphicon glyphicon-edit"></span>
								Edit
							    </a>
							</li>   
							<li class="divider"></li>
							<li>
							    <a href="javascript:void(0);" rel="${watchEntry.id}" class="deleteWatchEntry" title="Delete this watch entry">
								<span class="glyphicon glyphicon-remove"></span>
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
                                    <tr><td colspan="8" class="center-text">There are currently no watch list entries set up.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </section>
    </div>		
</div>		    
			    
<!-- Watch Entry modal -->
<div class="modal fade" id="watchEntryModal" role="dialog" tabindex="-1" aria-labeledby="Add Dashboard Watch Entry" aria-hidden="true" aria-describedby="Add Dashboard Watch Entry"></div>