<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table responsive table-bordered table-hover">
    <thead>
        <tr style="background-color:#f5f5f5">
            <th scope="col">Field</th>
            <th scope="col">Macro Name</th>
            <th scope="col">Crosswalk Name</th>
            <th scope="col" class="center-text">Pass/Clear</th>
            <th scope="col" class="center-text">Field A</th>
            <th scope="col" class="center-text">Field B</th>
            <th scope="col" class="center-text">Constant 1</th>
            <th scope="col" class="center-text">Constant 2</th>
            <th scope="col" class="center-text">Process Order</th>
            <th scope="col" class="center-text"></th>
        </tr>
    </thead>
    <tbody>
        <c:choose>
            <c:when test="${dataTranslations.size() > 0}">
                <c:forEach items="${dataTranslations}" var="trans" varStatus="tStatus">
                    <tr>
                        <td scope="row">
                            ${dataTranslations[tStatus.index].fieldName} <c:if test="${dataTranslations[tStatus.index].fieldNo > 0}">- F${dataTranslations[tStatus.index].fieldNo}</c:if>
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].macroName}
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].crosswalkName}
                        </td>
                        <td class="center-text">
                            <c:choose><c:when test="${dataTranslations[tStatus.index].passClear == 1}">Pass</c:when><c:otherwise>Clear</c:otherwise></c:choose>
			</td>
			<td class="center-text">
                            ${dataTranslations[tStatus.index].fieldA} 
                        </td>
                        <td class="center-text">
                            ${dataTranslations[tStatus.index].fieldB} 
                        </td>
                        <td class="center-text">
                            ${dataTranslations[tStatus.index].constant1} 
                        </td>
                        <td class="center-text">
                            ${dataTranslations[tStatus.index].constant2} 
                        </td>
                        <td class="center-text">
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
            <c:otherwise><tr><td scope="row" colspan="10" class="center-text">No Existing Translations Found</td></c:otherwise>
            </c:choose>
    </tbody>
</table>


