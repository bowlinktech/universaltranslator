<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="main clearfix" role="main">
  <c:if test="${not empty param.msg}" >  
    <div class="row-fluid">
        <div class="col-md-12">
            <div class="alert alert-success" id="delMsg">
                        <strong>Success!</strong> 
                        The file path has been successfully removed!
                    </div>            
        </div>
    </div>
</c:if>
    <div class="row-fluid">
        <div class="col-md-12">
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Move File Paths</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container scrollable"><br />
                        <table class="table table-striped table-hover table-default" <c:if test="${not empty filePaths}">id="dataTable"</c:if>>
                                <thead>
                                    <tr>
                                        <th scope="col">File Path</th>
                                        <th scope="col" class="center-text">Start Date Time</th>
                                        <th scope="col" class="center-text">End Date Time</th>
                                        
                                        <th scope="col"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:choose>
                                    <c:when test="${not empty pathList}">
                                        <c:forEach items="${pathList}" var="filePath" varStatus="pStatus">
                                        	<tr>
                                                <td scope="row">
                                                    ${filePath.folderPath}
                                                </td>
                                                <td class="center-text"><fmt:formatDate value="${filePath.startDateTime}" type="date" pattern="M/dd/yyyy hh:mm:ss a" /></td>
                                                <td class="center-text"><fmt:formatDate value="${filePath.endDateTime}" type="date" pattern="M/dd/yyyy hh:mm:ss a" /></td>
                                                <td class="center-text">
                                                    <a href="#" data-toggle="modal" class="btn btn-link deleteFilePath" title="Delete  this file path" rel="${filePath.id}">
                                                        <span class="glyphicon glyphicon-edit"></span>
                                                        Delete
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr><td colspan="7" class="center-text">There are currently no file paths that are in errored status.</td></tr>
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
<div class="modal fade" id="filePathModal" role="dialog" tabindex="-1" aria-labeledby="Delete Path" aria-hidden="true" aria-describedby="Delete Path"></div>


