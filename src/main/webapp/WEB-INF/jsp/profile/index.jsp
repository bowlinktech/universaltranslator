<%-- 
    Document   : index
    Created on : Nov 27, 2013, 10:33:49 AM
    Author     : chadmccue
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="product-suite" role="main">
    <div class="page-content container">
        <div class="row">
            <div class="col-md-12 product-summary">
                <div class="media">
                    <a href="" title="" class="icon-product icon-product icon-health-e-net pull-left media-object hidden-xs" ></a>
                    <div class="media-body">
			<h2>Welcome <c:out value="${userDetails.firstName}" />!</h2>
			<p>
			    Listed below are your configured methods for exchanging data with your eReferral partners.
			</p>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<div class="container main-container">
    <div class="row">
	<div class="col-md-9 col-md-offset-0 col-sm-8 col-sm-offset-1 page-content">
	    <h3>
		<c:choose>
		    <c:when test="${userDetails.orgType == 4}">
			<a href="<c:url value='/frontend/download'/>" title="File Exchange">File Exchange</a>
		    </c:when>
		    <c:otherwise>
			<a href="<c:url value='/frontend/upload'/>" title="File Exchange">File Exchange</a>
		    </c:otherwise>
		</c:choose>
	    </h3>
	    <p class="text">
		Exchanging data between the plethora of different host platforms and sources is key to building the essential infrastructure necessary to support high-quality and more cost 
		effective care delivery. Health-e-Link Exchange solution is the one web-enabled exchange portal that allows regional healthcare partners in your community to cost-effectively 
		send, receive, and exchange patient healthcare information leveraging your existing business and clinical applications, and your current IT resources. 
	    </p>
            <h3><a href="#settingsModal" id="settings" data-toggle="modal" title="Account Settings" class="settings">Account Settings</a></h3>
	    <p class="text">
		Update your account settings.
	    </p>

	</div>
    </div>
</div>