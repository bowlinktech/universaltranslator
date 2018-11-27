<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<aside class="secondary">
    <nav class="secondary-nav" role="navigation">
        <ul class="nav nav-pills nav-stacked" role="menu">
            <li role="menuitem" ${param['page'] == 'report' ? 'class="active"' : ''}><a href="/administrator/processing-activity/activityReport" title="Activity Report">Activity Report</a></li>
            <%--<li role="menuitem" ${param['page'] == 'waiting' ? 'class="active"' : ''}><a href="/administrator/processing-activity/pending" title="Pending Delivery">Pending Delivery</a></li>--%>
            <li role="menuitem" ${param['page'] == 'inbound' || page == 'inbound'  ? 'class="active"' : ''}><a href="/administrator/processing-activity/inbound" title="Inbound Processing Activities">Inbound Batches</a></li>
            <li role="menuitem" ${param['page'] == 'rejected' || page == 'rejected' ? 'class="active"' : ''}><a href="/administrator/processing-activity/rejected" title="Batches with Rejected Transactions">Batches w/ Rejected Transactions</a></li>
            <li role="menuitem" ${param['page'] == 'outbound' ? 'class="active"' : ''}><a href="/administrator/processing-activity/outbound" title="Outbound Processing Activities">Outbound Batches</a></li>
            <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                <li role="menuitem" ${param['page'] == 'invalid' ? 'class="active"' : ''}><a href="/administrator/processing-activity/invalidIn" title="Invalid Batches">Invalid Batches</a></li>
                 <%--<li role="menuitem" ${param['page'] == 'wsmessage' ? 'class="active"' : ''}><a href="/administrator/processing-activity/wsmessage" title="Web Service Messages">Web Service Messages</a></li>--%>
		<li role="menuitem" ${param['page'] == 'apimessages' ? 'class="active"' : ''}><a href="/administrator/processing-activity/apimessages" title="Rest API Messages">Rest API Messages</a></li>
            </sec:authorize>  
            <%--<li role="menuitem" ${param['page'] == 'refActivityExport' ? 'class="active"' : ''}><a href="/administrator/processing-activity/referralActivityExport" title="Referral Activity Export">Referral Activity Export</a></li>--%>
        </ul>
    </nav>
</aside>
