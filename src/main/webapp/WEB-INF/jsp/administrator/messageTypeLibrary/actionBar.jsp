<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
                <c:choose>
                    <c:when test="${param['page'] == 'list'}">
                        <a href="javascript:void(0);" title="Message Type Library Manager" class="unstyled-link">Message Type Library Manager</a>
                    </c:when>
                    <c:when test="${param['page'] == 'details'}">
                        <a href="javascript:void(0);" title="Message Type Details" class="unstyled-link">
                            <c:choose>
                                <c:when test="${not empty id}">
                                    Edit Message Type
                                </c:when>
                                <c:otherwise>
                                    Create New Message Type
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </c:when>
                    <c:when test="${param['page'] == 'mappings'}">
                        <a href="javascript:void(0);" title="Organization Users" class="unstyled-link">Field Mappings</a>
                    </c:when>
                    <c:when test="${param['page'] == 'translations'}">
                        <a href="javascript:void(0);" title="Organization Providers" class="unstyled-link">Field Specs</a>
                    </c:when>
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions">
            <c:choose>
                <c:when test="${param['page'] == 'details' or param['page'] == 'mappings' or param['page'] == 'translations'}">
                    <li><a href="javascript:void(0);" id="saveDetails" title="Save this Message Type"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <c:if test="${empty id}"><li><a href="javascript:void(0);" id="next" title="Save and Proceed to the Next Step"><span class="glyphicon glyphicon-forward icon-stacked"></span>Next Step</a></li></c:if>
                    <!--<c:if test="${not empty id}"><li><a href="#confirmationOrgDelete" data-toggle="modal" rel="${id}" title="Delete this Organization"><span class="glyphicon glyphicon-remove icon-stacked"></span>Delete</a></li></c:if>-->
                    <li><a href="<c:url value='/administrator/library/list' />" title="Cancel"><span class="glyphicon icon-stacked custom-icon glyphicon-ban-circle"></span>Cancel</a></li>
                    </c:when>
                    <c:when test="${param['page'] == 'list'}">
                    <li><a href="create" title="Create New Message Type"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                    </c:when>
                </c:choose>
        </ul>
    </div>
</nav>

