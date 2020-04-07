<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<aside class="secondary">
    <nav class="secondary-nav" role="navigation">
        <ul class="nav nav-pills nav-stacked" role="menu">
            <li role="menuitem" ${param['page'] == 'report' ? 'class="active"' : ''}><a href="/administrator/processing-activity/activityReport" title="Activity Report">Activity Report</a></li>
            <%--<li role="menuitem" ${param['page'] == 'waiting' ? 'class="active"' : ''}><a href="/administrator/processing-activity/pending" title="Pending Delivery">Pending Delivery</a></li>--%>
            <li role="menuitem" ${param['page'] == 'inbound' || page == 'inbound' || param['page'] == 'auditReport'  ? 'class="active"' : ''}><a href="/administrator/processing-activity/inbound" title="Inbound Batches">Inbound Batches</a></li>
	    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                <li role="menuitem" ${param['page'] == 'invalidin' ? 'class="active"' : ''}><a href="/administrator/processing-activity/invalidIn" title="Invalid Inbound Batches">Invalid Inbound Batches</a></li>
            </sec:authorize>  
            <li role="menuitem" ${param['page'] == 'rejected' || page == 'rejected' ? 'class="active"' : ''}><a href="/administrator/processing-activity/rejected" title="Batches with Rejected Transactions">Inbound Batches w/ Rejected Transactions</a></li>
            <li role="menuitem" ${param['page'] == 'outbound' || param['page'] == 'auditReportOutbound' ? 'class="active"' : ''}><a href="/administrator/processing-activity/outbound" title="Outbound Processing Activities">Outbound Batches</a></li>
            <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
		<li role="menuitem" ${param['page'] == 'invalidout' ? 'class="active"' : ''}><a href="/administrator/processing-activity/invalidOut" title="Invalid Outbound Batches">Invalid Outbound Batches</a></li>
                <%--<li role="menuitem" ${param['page'] == 'wsmessage' ? 'class="active"' : ''}><a href="/administrator/processing-activity/wsmessage" title="Web Service Messages">Web Service Messages</a></li>
		<li role="menuitem" ${param['page'] == 'apimessages' ? 'class="active"' : ''}><a href="/administrator/processing-activity/apimessages" title="Rest API Messages In">Rest API Messages In</a></li>
		<li role="menuitem" ${param['page'] == 'apimessagesout' ? 'class="active"' : ''}><a href="/administrator/processing-activity/apimessagesOut" title="Rest API Messages Out">Rest API Messages Out</a></li>
		<li role="menuitem" ${param['page'] == 'directmessages' ? 'class="active"' : ''}><a href="/administrator/processing-activity/directmessages" title="Direct Messages In">Direct Messages In</a></li>
		<li role="menuitem" ${param['page'] == 'directmessagesout' ? 'class="active"' : ''}><a href="/administrator/processing-activity/directmessagesOut" title="Direct Messages Out">Direct Messages Out</a></li>--%>
            </sec:authorize>  
            <%--<li role="menuitem" ${param['page'] == 'refActivityExport' ? 'class="active"' : ''}><a href="/administrator/processing-activity/referralActivityExport" title="Referral Activity Export">Referral Activity Export</a></li>--%>
        </ul>
    </nav>
</aside>
