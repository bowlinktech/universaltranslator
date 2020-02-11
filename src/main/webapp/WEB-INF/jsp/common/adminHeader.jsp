<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header id="header" class="header" role="banner">
    <!-- Primary Nav -->
    <!--role="navigation" used for accessibility -->
    <nav class="navbar primary-nav" role="navigation">
        <div class="contain">
            <div class="navbar-header">
                <a href="<c:url value='/administrator' />" class="navbar-brand" title="{company name}">
                   <span style="color:white; font-family: sans-serif; font-size: 20px">EAH UT</span>
                </a>
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>
            <div class="collapse navbar-collapse navbar-ex1-collapse">
                <ul class="nav navbar-nav" role="menu">
                    <c:if test="${userDetails.roleId == 1}">    
                        <li role="menuitem" class="${param['sect'] == 'dash' ? 'active' : 'none'}"><a href="<c:url value='/administrator' />" title="Dashboard">Dashboard</a><c:if test="${param['sect'] == 'dash'}"><span class="indicator-active arrow-up"></span></c:if></li>
                        <li role="menuitem" class="${param['sect'] == 'org' ? 'active' : 'none'}"><a href="<c:url value='/administrator/organizations/list' />" title="Organization Manager">Organizations</a><c:if test="${param['sect'] == 'org'}"><span class="indicator-active arrow-up"></span></c:if></li>
                        <!--<li role="menuitem" class="${param['sect'] == 'lib' ? 'active' : 'none'}"><a href="<c:url value='/administrator/library/list' />" title="Message Type Library Manager">Message Types</a><c:if test="${param['sect'] == 'lib'}"><span class="indicator-active arrow-up"></span></c:if></li>-->
                        <li role="menuitem" class="${param['sect'] == 'config' ? 'active' : 'none'}"><a href="<c:url value='/administrator/configurations/list' />" title="Configuration Manager">Configurations</a><c:if test="${param['sect'] == 'config'}"><span class="indicator-active arrow-up"></span></c:if></li>
                        <li role="menuitem" class="${param['sect'] == 'connect' ? 'active' : 'none'}"><a href="<c:url value='/administrator/configurations/connections' />" title="Configuration Connection Manager">Connections</a><c:if test="${param['sect'] == 'connect'}"><span class="indicator-active arrow-up"></span></c:if></li>
		    </c:if>
		    <c:if test="${userDetails.roleId == 3}">
                        <li role="menuitem" class="${param['sect'] == 'dash' ? 'active' : 'none'}"><a href="<c:url value='/administrator' />" title="Dashboard">Dashboard</a><c:if test="${param['sect'] == 'dash'}"><span class="indicator-active arrow-up"></span></c:if></li>
                    </c:if>
		    <li role="menuitem" class="${param['sect'] == 'activity' ? 'active' : 'none'}"><a href="<c:url value='/administrator/processing-activity/activityReport' />" title="Processing Activity">Processing Activity</a><c:if test="${param['sect'] == 'activity'}"><span class="indicator-active arrow-up"></span></c:if></li>
		    <c:if test="${userDetails.roleId == 1}">
                        <li role="menuitem" class="${param['sect'] == 'sysadmin' ? 'active' : 'none'}"><a href="<c:url value='/administrator/sysadmin/' />" title="System Administration">System Admin</a><c:if test="${param['sect'] == 'sysadmin'}"><span class="indicator-active arrow-up"></span></c:if></li>
		    </c:if>
		    <c:if test="${userDetails.roleId == 4}">
                        <li role="menuitem" class="${param['sect'] == 'org' ? 'active' : 'none'}"><a href="<c:url value='/administrator/organizations/list' />" title="Organization Manager">Organizations</a><c:if test="${param['sect'] == 'org'}"><span class="indicator-active arrow-up"></span></c:if></li>
		    </c:if>
                </ul>
                <ul class="nav navbar-nav navbar-right" id="secondary-nav">
                    <li><a class="logout" href="<c:url value='/userlogout' />">Log out</a></li>
                </ul>
            </div>
        </div>
    </nav>
    <!-- // End Primary Nav -->
</header>
