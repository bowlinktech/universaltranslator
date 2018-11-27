<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header id="header" class="header" role="banner">
    <div class="header-inner">
        <nav class="navbar primary-nav" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <a href="<c:url value='/profile'/>" class="navbar-brand" title="">
                        <!--
                                <img src="img/health-e-link/img-health-e-link-logo.png" class="logo" alt="Health-e-Link Logo"/>
                                Required logo specs:
                                logo width: 125px
                                logo height: 30px

                                Plain text can be used without image logo

                                sprite can be used with class="logo":
                        -->
                        <span class="identity logo" alt="" title=""></span>
                    </a>
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                </div>
                <div class="collapse navbar-collapse navbar-ex1-collapse">
                    <ul class="nav navbar-nav navbar-right">
                        <li ${param['page'] == 'about' ? 'class="active"' : ''}><a href="<c:url value='/about'/>" title="About">About</a></li>
                        <li ${param['page'] == 'contact' ? 'class="active"' : ''}><a href="<c:url value='/contact'/>" title="Contact">Contact</a></li>
                        <li ${param['page-id'] == 'profile' ? 'class="active"': ''}>
                            <c:choose>
                                <c:when test="${not empty pageContext.request.userPrincipal.name}">
                                    <a href="javascript:void(0);" title="My Account" data-toggle="dropdown">My Account <b class="caret"></b></a>
                                    <ul class="dropdown-menu" role="menu" aria-labelledby="My account dropdown">
                                        <li><a href="<c:url value='/profile'/>" title="My Account">My Account</a></li>
                                        <li><a href="#settingsModal" id="settings" data-toggle="modal" title="Account Settings" class="settings">Account Settings</a></li>
                                        <li>
					    <a href="<c:url value='/frontend/upload'/>" title="File Exchange">File Exchange</a>
					</li>
                                        <li><a title="log out" href="<c:url value='/logout' />">Log out</a></li>
                                    </ul> 
                                </c:when>
                                <c:otherwise>
                                    <a href="<c:url value='login' />" title="Log In">Log In</a>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
	<div class="container">
	    <c:choose>
		<c:when test="${param['page-section'] == 'frontend'}"><h1 class="page-title"><span class="page-title-icon pull-left"></span>File Exchange</h1></c:when>
		<c:otherwise><h1 class="page-title">${pageTitle}</h1></c:otherwise>
	    </c:choose>
	</div>   
        <%-- Section Nav --%>
        <c:choose>
            <c:when test="${param['page-section'] == 'frontend'}">
                <nav class="navbar navbar-default actions-nav" role="navigation">
                    <div class="container">
                        <ul class="nav navbar-nav navbar-actions">
                           <c:if test="${userDetails.uploadFiles}"><li ${param['page'] == 'upload' ? 'class="active"' : ''}><a href="<c:url value='/frontend/upload'/>" title="Upload New File" class="btn btn-link"><span class="glyphicon glyphicon-upload"></span>&nbsp; Upload Files</a><span class="indicator-active arrow-up"></span></li></c:if>
                           <c:if test="${userDetails.downloadFiles}"><li ${param['page'] == 'download' ? 'class="active"' : ''}><a href="<c:url value='/frontend/download'/>" title="Download Files" class="btn btn-link"><span class="glyphicon glyphicon-download"></span>&nbsp; Download Files</a><span class="indicator-active arrow-up"></span></li></c:if>
                           <li ${param['page'] == 'audit' ? 'class="active"' : ''}><a href="<c:url value='/frontend/auditReports'/>" title="View Audit Reports" class="btn btn-link"><span class="glyphicon glyphicon-calendar"></span>&nbsp; Audit Reports</a><span class="indicator-active arrow-up"></span></li>
			   <li ${param['page'] == 'activityReport' ? 'class="active"' : ''}><a href="<c:url value='/frontend/activityReport'/>" title="Activity Report" class="btn btn-link"><span class="glyphicon glyphicon-calendar"></span>&nbsp; Activity Report</a><span class="indicator-active arrow-up"></span></li>
                        </ul>
                        
                        <c:if test="${userDetails.createAuthority == true && hasConfigurations == true}">
                             <ul class="nav navbar-nav navbar-right navbar-actions">
                                <li>
                                    <a href="#uploadFile" title="Upload File" data-toggle="modal" class="uploadFile">Upload New File</a>
                                    <span class="indicator-active arrow-up"></span>
                                </li>
                            </ul>
                         </c:if>
                    </div>
                </nav>
            </c:when>
        </c:choose>
    </div>
</header>

<script type="text/javascript">
//This function will launch the overlay for settings
require(['./main'], function () {
    require(['jquery'], function($) {
  
$(document).on('click', '.settings', function() {
	$.ajax({
        url: '/settings',
        type: "GET",
        success: function(data) {
            $("#settingsModal").html(data);
        }
    });
});
    });
});
</script>