<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table table-striped table-hover responsive">
    <thead>
        <tr>
            <th scope="col">Field</th>
            <th scope="col">Macro</th>
            <th scope="col">Crosswalk</th>
            <th scope="col" class="center-text">Pass/Clear Error</th>
            <th scope="col">Field A</th>
            <th scope="col">Field B</th>
            <th scope="col">Constant 1</th>
            <th scope="col">Constant 2</th>
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
                            ${dataTranslations[tStatus.index].fieldName} <c:if test="${dataTranslations[tStatus.index].fieldNo > 0}">- F${dataTranslations[tStatus.index].fieldNo}</c:if>
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].macroName} 
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].crosswalkName}
                            <c:if test="${dataTranslations[tStatus.index].crosswalkName != null}">
                                <select rel="${dataTranslations[tStatus.index].id}" name="defaultValue" class="setDefaultValue form-control half">
                                    <option value="">- Select Default Value -</option>
                                    <c:forEach var="defaultValue" items="${dataTranslations[tStatus.index].defaultValues}">
                                        <option value="${defaultValue['key']}" <c:if test="${dataTranslations[tStatus.index].defaultValue == defaultValue['key']}">selected="true"</c:if>>${defaultValue['value']}</option>
                                    </c:forEach>
                                </select>
                            </c:if>
                        </td>
                        <td class="center-text">
                            <c:choose><c:when test="${dataTranslations[tStatus.index].passClear == 1}">Pass</c:when><c:otherwise>Clear</c:otherwise></c:choose>
			</td>
			<td>
                            ${dataTranslations[tStatus.index].fieldA} 
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].fieldB} 
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].constant1} 
                        </td>
                        <td>
                            ${dataTranslations[tStatus.index].constant2} 
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
            <c:otherwise><tr><td scope="row" colspan="10" class="center-text">No Existing Translations Found</td></c:otherwise>
            </c:choose>
    </tbody>
</table>


