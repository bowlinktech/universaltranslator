<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main" rel="dataForTable">
    <div class="col-md-12">
        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success" role="alert">
                <strong>Success!</strong> 
                <c:choose><c:when test="${savedStatus == 'updated'}">The news article has been successfully updated!</c:when><c:otherwise>The news article has been successfully created!</c:otherwise></c:choose>
                    </div>
        </c:if>
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">News Articles</h3>
            </div>
            <div class="panel-body">

                <div class="form-container scrollable">
                    <table class="table table-striped table-hover table-default" <c:if test="${not empty newsArticles}">id="dataTable"</c:if>>
                            <thead>
                                <tr>
                                    <th scope="col">Title</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">Date Created</th>
                                    <th scope="col"></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty newsArticles}">
                                    <c:forEach var="article" items="${newsArticles}">
                                        <tr id="dataRow">
                                            <td scope="row">
                                                <a href="news/edit?i=${article.id}" title="Edit this article">${article.title}</a>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${article.status == true}">
                                                        Active
                                                    </c:when>
                                                    <c:otherwise>Inactive</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${article.dateCreated}" type="date" pattern="M/dd/yyyy" />
                                            </td>
                                            <td class="actions-col">
                                                <a href="news/edit?i=${article.id}" title="Edit this article">
                                                    <span class="glyphicon glyphicon-edit"></span>
                                                    Edit	
                                                </a>
                                                <a href="javascript:void(0);" rel="${article.id}" class="btn btn-link articleDelete" title="Delete this article">
                                                    <span class="glyphicon glyphicon-remove"></span>
                                                    Delete
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr><td colspan="4" class="center-text">There where no news articles in the system.</td></tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
        </section>
    </div>		
</div>	



