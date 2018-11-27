<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table table-striped table-hover responsive">
    <thead>
        <tr>
            <th scope="col">Field</th>
            <th scope="col">Crosswalk</th>
            <th scope="col">Process Order</th>
            <th scope="col" class="center-text"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${dataTranslations.size() > 0}">
                <c:forEach items="${dataTranslations}" var="trans" varStatus="tStatus">
                    <tr>
                        <td scope="row">
                            ${dataTranslations[tStatus.index].fieldName} 
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].crosswalkName} 
                        </td>
                        <td>
                            <select rel="${dataTranslations[tStatus.index].processOrder}" name="processOrder" class="processOrder">
                                <option value="">- Select -</option>
                                <c:forEach begin="1" end="${dataTranslations.size()}" var="i">
                                    <option value="${i}" <c:if test="${dataTranslations[tStatus.index].processOrder  == i}">selected</c:if>>${i}</option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="center-text">
                            <a href="javascript:void(0);" class="btn btn-link removeTranslation" rel2="${dataTranslations[tStatus.index].processOrder}" rel="${dataTranslations[tStatus.index].fieldId}" title="Remove this field translation.">
                                <span class="glyphicon glyphicon-edit"></span>
                                Remove
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise><tr><td scope="row" colspan="4" style="text-align:center">No Existing Data Translations Found</td></c:otherwise>
            </c:choose>
    </tbody>
</table>


