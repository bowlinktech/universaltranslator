<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
                <c:choose>
                    <c:when test="${param['page'] == 'listOrgs'}">
                        <a href="javascript:void(0);" title="Organization List" class="unstyled-link">Organizations</a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgDetails'}">
                        <a href="javascript:void(0);" title="Organization Details" class="unstyled-link">
                            <c:choose>
                                <c:when test="${not empty id}">
                                    Edit Organization
                                </c:when>
                                <c:otherwise>
                                    Create New Organization
                                </c:otherwise>
                            </c:choose>
                        </a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgConfigs'}">
                        <a href="javascript:void(0);" title="Organization Configurations" class="unstyled-link">Organization Configurations</a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgUsers'}">
                        <a href="javascript:void(0);" title="Organization Users" class="unstyled-link">Organization Users</a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgProviders'}">
                        <a href="javascript:void(0);" title="Organization Providers" class="unstyled-link">Organization Providers</a>
                    </c:when>
                    <c:when test="${param['page'] == 'providerDetails'}">
                        <a href="javascript:void(0);" title="Organization Providers" class="unstyled-link">Edit Provider</a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgBrochures'}">
                        <a href="javascript:void(0);" title="Organization Brochures" class="unstyled-link">Organization Brochures</a>
                    </c:when>
                    <c:when test="${param['page'] == 'orgResources'}">
                        <a href="javascript:void(0);" title="Organization Resources" class="unstyled-link">Organization Resources</a>
                    </c:when>    
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions" role="menu">
            <c:choose>
                <c:when test="${param['page'] == 'orgDetails'}">
                    <li role="menuitem"><a href="javascript:void(0);" id="saveDetails" title="Save this Organization" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <li role="menuitem"><a href="javascript:void(0);" id="saveCloseDetails" title="Save &amp; Close" role="button"><span class="glyphicon glyphicon-floppy-disk icon-stacked"></span> Save &amp; Close</a></li>
                    <c:if test="${not empty id}"><li><a href="#confirmationOrgDelete" data-toggle="modal" rel="${id}" title="Delete this Organization"><span class="glyphicon glyphicon-remove icon-stacked" role="button"></span>Delete</a></li></c:if>
                    <li role="menuitem"><a href="<c:url value='/administrator/organizations/list' />" title="Cancel" role="button"><span class="glyphicon icon-stacked  glyphicon-ban-circle"></span>Cancel</a></li>
                    </c:when>
                    <c:when test="${param['page'] == 'providerDetails'}">
                    <li role="menuitem"><a href="javascript:void(0);" id="saveDetails" title="Save this Provider" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <li role="menuitem" ><a href="javascript:void(0);" id="saveCloseDetails" title="Save And Close" role="button"><span class="glyphicon glyphicon-floppy-disk icon-stacked"></span> Save &amp; Close</a></li>
                    <c:if test="${providerId > 0}"><li><a href="javascript:void(0);" id="deleteProvider" rel="${providerId}" title="Delete this Provider" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span>Delete</a></li></c:if>
                    <li role="menuitem"><a href="<c:url value='providers' />" title="Cancel" role="button"><span class="glyphicon icon-stacked glyphicon-ban-circle"></span>Cancel</a></li>
                    </c:when>
                    <c:when test="${param['page'] == 'listOrgs'}">
                    <li role="menuitem"><a href="create" title="Create New Organization" role="button"><span class="glyphicon icon-stacked glyphicon glyphicon-plus"></span>Create New</a></li>
                    </c:when>
                    <c:when test="${param['page'] == 'orgResources'}">
                    <li role="menuitem"><a href="javascript:void(0);" id="saveDetails" title="Save this Provider" role="button"><span class="glyphicon glyphicon-ok icon-stacked"></span> Save </a></li>
                    <c:if test="${providerId > 0}"><li><a href="javascript:void(0);" id="deleteProvider" rel="${providerId}" title="Delete this Provider" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span>Delete</a></li></c:if>
                    <li role="menuitem"><a href="<c:url value='/administrator/organizations/${organization.cleanURL}/' />" title="Cancel" role="button"><span class="glyphicon icon-stacked glyphicon-ban-circle"></span>Cancel</a></li>
                    </c:when>    
                </c:choose>
        </ul>
    </div>
</nav>
