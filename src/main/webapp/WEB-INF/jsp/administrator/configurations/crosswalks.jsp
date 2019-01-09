<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<table class="table table-striped table-hover responsive">
    <thead>
        <tr>
            <th scope="col">Name</th>
            <th scope="col" class="center-text">Date Created</th>
            <th scope="col"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${availableCrosswalks.size() > 0}">
                <c:forEach items="${availableCrosswalks}" var="crosswalk" varStatus="pStatus">
                    <tr>
                        <td scope="row">
                            ${availableCrosswalks[pStatus.index].name} <c:choose><c:when test="${availableCrosswalks[pStatus.index].orgId == 0}"> (generic)</c:when><c:otherwise> (Org Specific)</c:otherwise></c:choose>
                                </td>
                                    <td class="center-text"><fmt:formatDate value="${availableCrosswalks[pStatus.index].dateCreated}" type="date" pattern="M/dd/yyyy" /></td>
                        <td class="center-text">
                            <a href="#crosswalkModal" data-toggle="modal" class="btn btn-link viewCrosswalk" rel="?i=${availableCrosswalks[pStatus.index].id}" title="View this Crosswalk">
                                <span class="glyphicon glyphicon-edit"></span>
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise><tr><td scope="row" colspan="3" style="text-align:center">No Crosswalks Found</td></c:otherwise>
            </c:choose>
    </tbody>
</table>
<ul class="pagination pull-right" role="navigation" aria-labelledby="Paging ">
    <c:if test="${currentPage > 1}"><li><a href="javascript:void(0);" class="nextPage" rel="${currentPage-1}">&laquo;</a></li></c:if>
        <c:forEach var="i" begin="1" end="${totalPages}">
        <li><a href="javascript:void(0);" class="nextPage" rel="${i}">${i}</a></li>
        </c:forEach>
        <c:if test="${currentPage < totalPages}"><li><a href="javascript:void(0);" class="nextPage" rel="${currentPage+1}">&raquo;</a></li></c:if>
</ul>