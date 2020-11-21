<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-default actions-nav" role="navigation">
    <div class="contain">
        <div class="navbar-header">
            <h1 class="section-title navbar-brand">
               <c:choose>
                    <c:when test="${param['page'] == 'waiting'}">
                        <a href="javascript:void(0);" title="Transactions Waiting to be Processed" class="unstyled-link">Transactions Waiting to be Processed</a>
                    </c:when>
                    <c:when test="${param['page'] == 'inbound' || page == 'inbound'}">
                        <a href="javascript:void(0);" title="Inbound Batches" class="unstyled-link">Inbound Batches</a>
                    </c:when>
                    <c:when test="${param['page'] == 'auditReport' && page == 'rejected'}">
                        <a href="javascript:void(0);" title="Inbound Batches with Rejected Transactions / Audit Report" class="unstyled-link">Inbound Batches with Rejected Transactions / Audit Report</a>
                    </c:when>       
                    <c:when test="${param['page'] == 'rejected' || page == 'rejected'}">
                        <a href="javascript:void(0);" title="Inbound Batches with Rejected Transactions" class="unstyled-link">Inbound Batches with Rejected Transactions</a>
                    </c:when>    
                    <c:when test="${param['page'] == 'outbound' || page == 'outbound'}">
                        <a href="javascript:void(0);" title="Outbound Batches" class="unstyled-link">Outbound Batches</a>
                    </c:when>
                    <c:when test="${param['page'] == 'edit'}">
                        <a href="javascript:void(0);" title="Edit Batch Transaction" class="unstyled-link">Edit Batch Transaction</a>
                    </c:when>
                    <c:when test="${param['page'] == 'refActivityExport'}">
                        <a href="javascript:void(0);" title="Referral Activity Export" class="unstyled-link">Referral Activity Export</a>
                    </c:when>  
                    <c:when test="${param['page'] == 'auditReport' && page == 'invalid'}">
                        <a href="javascript:void(0);" title="Invalid Inbound Batches / Audit Report" class="unstyled-link">Invalid Inbound Batches / Audit Report</a>
                    </c:when>    
		    <c:when test="${param['page'] == 'auditReport'}">
                        <a href="javascript:void(0);" title="Inbound Batches / Audit Report" class="unstyled-link">Inbound Batches / Audit Report</a>
                    </c:when>
                    <c:when test="${param['page'] == 'auditReportOutbound'}">
                        <a href="javascript:void(0);" title="Outbound Batches / Audit Report" class="unstyled-link">Outbound Batches / Audit Report</a>
                    </c:when>    
		    <c:when test="${param['page'] == 'wsmessage'}">
                        <a href="javascript:void(0);" title="Web Service Messages" class="unstyled-link">Web Service Messages</a>
                    </c:when>
		    <c:when test="${param['page'] == 'apimessages'}">
                        <a href="javascript:void(0);" title="Rest API Messages In" class="unstyled-link">Rest API Messages In</a>
                    </c:when>
		   <c:when test="${param['page'] == 'apimessagesout'}">
                        <a href="javascript:void(0);" title="Rest API Messages Out" class="unstyled-link">Rest API Messages Out</a>
                    </c:when>
		    <c:when test="${param['page'] == 'report'}">
                        <a href="javascript:void(0);" title="Activity Report" class="unstyled-link">Activity Report</a>
                    </c:when>
		    <c:when test="${param['page'] == 'invalidin' || page == 'invalidIn'}">
                        <a href="javascript:void(0);" title="Invalid Inbound Batches" class="unstyled-link">Invalid Inbound Batches</a>
                    </c:when>
		    <c:when test="${param['page'] == 'invalidout'}">
                        <a href="javascript:void(0);" title="Invalid Outbound Batches" class="unstyled-link">Invalid Outbound Batches</a>
                    </c:when>
		    <c:when test="${param['page'] == 'directmessages'}">
                        <a href="javascript:void(0);" title="Direct Messages In" class="unstyled-link">Direct Messages In</a>
                    </c:when>
		   <c:when test="${param['page'] == 'directmessagesout'}">
                        <a href="javascript:void(0);" title="Direct Messages Out" class="unstyled-link">Direct Messages Out</a>
                    </c:when>
                </c:choose>
            </h1>
        </div>
        <ul class="nav navbar-nav navbar-right navbar-actions" role="menu">
            <c:choose>
                <c:when test="${param['page'] == 'edit'}">
                    <li role="menuitem"><a href="javascript:void(0);" id="saveCloseDetails" class="submitMessage" title="Save &amp; Close" role="button"><span class="glyphicon glyphicon-floppy-disk icon-stacked"></span> Save &amp; Close</a></li>
                </c:when>
                <c:when test="${param['page'] == 'auditReport' && page == 'invalid'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/invalidIn" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>  
                <c:when test="${param['page'] == 'auditReport' && page == 'rejected'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/rejected" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>      
		<c:when test="${param['page'] == 'auditReport'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/inbound" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>  
                <c:when test="${param['page'] == 'auditReportOutbound'}">
                    <li role="menuitem"><a href="/administrator/processing-activity/outbound" class="submitMessage" title="Close" role="button"><span class="glyphicon glyphicon-remove icon-stacked"></span> Close</a></li>
                </c:when>     
            </c:choose>
        </ul>
    </div>
</nav>
