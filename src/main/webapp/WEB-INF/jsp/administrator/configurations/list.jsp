<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">
        <div class="alert alert-danger" id="exportErrorMsg" style="display:none;">
            <strong>Export Error!</strong> 
            An error occurred while trying to export the configuration.
        </div>
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
	
	<ul class="nav nav-pills" style="margin-bottom:20px;">
	    <li class="active">
		<a data-toggle="tab" href="#source-config">Source Configurations</a>
	    </li>
	    <li>
		<a data-toggle="tab" href="#target-config">Target Configurations</a>
	    </li>
	</ul>

	<div class="tab-content clearfix">
	    <div class="tab-pane active" id="source-config">
		 <section class="panel panel-info container-fluid">
		    <div class="panel-heading">
			<h3 class="panel-title">Source Configurations</h3>
		    </div>
		   <div class="panel-body">
		       <div class="form-container scrollable">
			    <table class="table table-striped table-hover table-default" <c:if test="${not empty sourceconfigurations}">id="sourceconfigdatatable"</c:if>>
			    <thead>
				<tr>
				    <th scope="col" class="center-text" style="width:5%">Id</th>
				    <th scope="col" style="width:20%">Organization</th>
				    <th scope="col" style="width:30%">Configuration Name</th>
				    <th scope="col" class="center-text"style="width:10%">Transport Method</th>
				    <th scope="col" class="center-text" style="width:15%">Date Created</th>
                                    <th scope="col" class="center-text" style="width:15%">Date Last Updated</th>
				    <th scope="col" style="width:5%"></th>
				</tr>
			    </thead>
			    <tbody>
				 <c:choose>
				     <c:when test="${not empty sourceconfigurations}">
					 <c:forEach var="config" items="${sourceconfigurations}">
					     <tr>
						 <td scope="row" class="center-text">${config.id}</td>
						 <td>
						     ${config.orgName}
						 </td>
						 <td >
						     <a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">${config.configName}</a>
						     <br />
						     (<c:choose><c:when test="${config.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)
						 </td>
						 <td class="center-text">
						     <c:choose><c:when test="${config.transportMethod == 'File Upload'}"><c:choose><c:when test="${config.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${config.transportMethod}</c:otherwise></c:choose>
						 </td>
						 <td class="center-text">
						     <fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy h:mm a" />
						 </td>
                                                 <td class="center-text">
                                                     <c:choose>
                                                         <c:when test="${empty config.dateUpdated}">
                                                             <fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy h:mm a" />
                                                         </c:when>
                                                         <c:otherwise>
                                                             <fmt:formatDate value="${config.dateUpdated}" type="date" pattern="M/dd/yyy h:mm a" />
                                                         </c:otherwise>
                                                     </c:choose>
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
                                                                 <c:if test="${not empty config.fileDropLocation && config.messageTypeId != 2}">
                                                                    <li>
                                                                        <a href="#configFileUploadModal" data-toggle="modal" class="uploadFile" rel="${config.id}" rel2="${config.fileDropLocation}" title="Upload File">
                                                                            <span class="glyphicon glyphicon-upload"></span>
                                                                            Upload File
                                                                        </a>
                                                                    </li>
                                                                </c:if>
                                                                    <li class="divider"></li>
							     </c:if>
                                                             <li>
								 <a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">
								     <span class="glyphicon glyphicon-edit"></span>
								     Edit
								 </a>
							     </li>       
                                                             <li>
                                                                <a href="javascript:void(0);" class="printConfig" rel="${config.id}" title="Print this Configuration">
                                                                    <span class="glyphicon glyphicon-print"></span>
                                                                    Print
                                                                </a>
                                                             </li>
                                                             <c:if test="${config.allowExport}">
                                                                <li>
                                                                    <a href="javascript:void(0);" class="exportConfig" rel="${config.id}" title="Export this Configuration">
                                                                        <span class="glyphicon glyphicon-export"></span>
                                                                        Export
                                                                    </a>
                                                                 </li> 
                                                             </c:if>
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
					 <tr><td colspan="6" class="center-text">There are currently no source configurations set up.</td></tr>
				     </c:otherwise>
				 </c:choose>
			     </tbody>
			 </table>
		       </div>
		   </div>
		 </section>
	    </div>
	    <div class="tab-pane" id="target-config">
		<section class="panel panel-info container-fluid">
		    <div class="panel-heading">
			<h3 class="panel-title">Target Configurations</h3>
		    </div>
		   <div class="panel-body">
		       <div class="form-container scrollable">
			   <table class="table table-striped table-hover table-default" <c:if test="${not empty targetconfigurations}">id="targetconfigdatatable"</c:if>>
				<thead>
                                    <tr>
                                        <th scope="col" class="center-text" style="width:5%">Id</th>
                                        <th scope="col" style="width:20%">Organization</th>
                                        <th scope="col" style="width:30%">Configuration Name</th>
                                        <th scope="col" class="center-text"style="width:10%">Transport Method</th>
                                        <th scope="col" class="center-text" style="width:15%">Date Created</th>
                                        <th scope="col" class="center-text" style="width:15%">Date Last Updated</th>
                                        <th scope="col" style="width:5%"></th>
                                    </tr>
				</thead>
				<tbody>
				    <c:choose>
				    <c:when test="${not empty targetconfigurations}">
					<c:forEach var="config" items="${targetconfigurations}">
					    <tr>
						<td scope="row" class="center-text">${config.id}</td>
						<td>${config.orgName}</td>
						<td >
						    <a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">${config.configName}</a>
						    <br />
						    (<c:choose><c:when test="${config.status == true}">active</c:when><c:otherwise>inactive</c:otherwise></c:choose>)
						</td>
						<td class="center-text">
						    <c:choose><c:when test="${config.transportMethod == 'File Upload'}"><c:choose><c:when test="${config.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${config.transportMethod}</c:otherwise></c:choose>
						</td>
						<td class="center-text">
						     <fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy h:mm a" />
						 </td>
                                                 <td class="center-text">
                                                     <c:choose>
                                                         <c:when test="${empty config.dateUpdated}">
                                                             <fmt:formatDate value="${config.dateCreated}" type="date" pattern="M/dd/yyyy h:mm a" />
                                                         </c:when>
                                                         <c:otherwise>
                                                             <fmt:formatDate value="${config.dateUpdated}" type="date" pattern="M/dd/yyy h:mm a" />
                                                         </c:otherwise>
                                                     </c:choose>
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
                                                                <a href="javascript:void(0);" class="printConfig" rel="${config.id}" title="Print this Configuration">
                                                                    <span class="glyphicon glyphicon-print"></span>
                                                                    Print
                                                                </a>
                                                             </li>    
							    <li>
								<a href="javascript:void(0);" class="editConfig" rel="${config.id}" title="Edit this configuration">
								    <span class="glyphicon glyphicon-edit"></span>
								    Edit
								</a>
							    </li>
                                                            <c:if test="${config.allowExport}">
                                                                <li>
                                                                    <a href="javascript:void(0);" class="exportConfig" rel="${config.id}" title="Export this Configuration">
                                                                        <span class="glyphicon glyphicon-export"></span>
                                                                        Export
                                                                    </a>
                                                                 </li> 
                                                             </c:if>
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
					<tr><td colspan="6" class="center-text">There are currently no target configurations set up.</td></tr>
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
</div>
<div class="modal fade" id="configFileUploadModal" role="dialog" tabindex="-1" aria-labeledby="Configuration File Upload" aria-hidden="true" aria-describedby="Configuration File Upload"></div>
