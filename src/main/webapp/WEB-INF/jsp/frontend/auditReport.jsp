<%--
    Document   : auditReport
    Author     : gchan
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page buffer="1024kb" autoFlush="true" %>

<c:set var="transactionCounter" value="1"/>
<c:set var="newTransId" value="0"/>
<c:set var="idList" value=""/>

<div class="container main-container" role="main">
    <div class="row">
        <div class="col-md-12 page-content">

            <div style="display:none;" class="alert alert-danger" role="alert"></div>

            <c:choose>
                <c:when test="${hasPermission}">
                    <div class="row-fluid">
			    <div class="col-md-12">
				<section class="panel panel-default">
				    <div class="panel-heading">
					<h3 class="panel-title">Audit Summary</h3>
				    </div>
				    <div class="panel-body">
					<div class="row">
					    <div class="col-md-12">
						<div class="${showButtons ? 'col-md-4' : 'col-md-6'}">
						    <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Received">
							<div class="panel-body">
							    <span class="stat-number"><a href="javascript:void(0);"><c:choose><c:when test="${batchDetails.totalRecordCount >= 0}"><fmt:formatNumber value = "${batchDetails.totalRecordCount}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
							    <h3>Total Transactions</h3>
							</div>
						    </section>
						</div>
						<div class="${showButtons ? 'col-md-4' : 'col-md-6'}">
						    <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Delivered">
							<div class="panel-body">
							    <span class="stat-number"><a href="javascript:void(0);"><c:choose><c:when test="${batchDetails.errorRecordCount >= 0}"><fmt:formatNumber value = "${batchDetails.errorRecordCount}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
							    <h3>Rejected Transactions</h3>
							</div>
						    </section>
						</div>
						<c:if test="${showButtons}">
						    <div class="col-xs-4">
							<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Delivered">
							    <div class="panel-body">
								<c:if test="${canSend}">
								    <input type="button" id="release" class="btn btn-success btn-small releaseBatch" rel="releaseBatch" rel2="${batchDetails.id}"  value="Release" />
								</c:if>
								<c:if test="${canEdit}">
								    <input type="button" id="rejectAll" class="btn btn-danger btn-small rejectMessages" value="Reject All Errors" />
								</c:if>
								<c:if test="${canCancel}">
								    <input type="button" id="cancel" class="btn btn-danger btn-small cancelBatch" rel="cancel"  rel2="${batchDetails.id}" value="Cancel" />
								</c:if>
								<c:if test="${canReset}">
								    <input type="button" id="reset" class="btn btn-warning btn-small resetBatch" rel="reset"  rel2="${batchDetails.id}" value="Reset" />
								</c:if>	    
							    </div>
							</section>
						    </div>
						</c:if>
					    </div>	
					    <div class="col-md-12">
						<div class="col-md-6">
						    <p><strong>Sending Organization:</strong><br />${batchDetails.orgName}</p>
						    <p><strong>Date Uploaded:</strong><br /><fmt:formatDate value="${batchDetails.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm:ss a" /></p>
						    <p><strong>Configuration:</strong><br />${batchDetails.configName}</p>
						</div>
						<div class="col-md-6">
						    <c:set var="text" value="${fn:split(batchDetails.originalFileName,'.')}" />
						    <c:set var="ext" value="${text[fn:length(text)-1]}" />
						    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?filename=${batchDetails.utBatchName}.${ext}&foldername=archivesIn"/>
						    <p><strong>Target Organization:</strong><br />${batchDetails.tgtorgName}</p>
						    <p><strong>Uploaded File:</strong><br /><a href="${hrefLink}" title="View Original File">${batchDetails.originalFileName}</a></p>
						    <p><strong>Batch Id:</strong><br />${batchDetails.utBatchName}</p>
						</div>
					     </div>   
					</div>
				    </div>
				</section>
			    </div>
			</div>
			<c:if test="${batchDetails.errorRecordCount > 0 and not empty batchErrorSummary}">
			    <div class="row-fluid">
				<div class="col-md-12">
				    <section class="panel panel-default">
					<div class="panel-heading">
					    <h3 class="panel-title">Transaction Errors</h3>
					</div>
					<div class="panel-body">
					    <c:forEach varStatus="i" var="batchError" items="${batchErrorSummary}">
						<div class="col-md-12">
						    <section class="panel panel-default">
							<div class="panel-heading">
							    <a data-toggle="collapse" rel="${batchDetails.id}" error="${batchError.errorId}" total="${batchError.totalErrors}" rel2="${i.index}" class="errorCollapse" href="#collapse-${i.index}">
								<div class="clearfix">
								    <div class="pull-left">
									<h3 class="panel-title">Error: ${batchError.errorDisplayText}</h3>
								    </div>
								    <div class="pull-right">
									<h3 class="panel-title" style="color:red">Total Found: <fmt:formatNumber value = "${batchError.totalErrors}" type = "number"/></h3>
								    </div>
								</div>
							    </a>	
						       </div>     
							<div id="collapse-${i.index}" class="panel-collapse collapse">       
							    <div class="panel-body clearfix" style="height:300px;">
								<div class="col-md-12 clearfix">
								    <div class="col-md-6 col-md-offset-6 spinner-${i.index}">
									<i class="fa fa-spinner fa-spin fa-4x"></i>
								    </div>
								    <div class="errorList-${i.index}" style=" overflow: auto; height:250px;"></div>
								</div>
							    </div>
							</div>
						    </section>
						</div>
					    </c:forEach>
					</div>
				    </section>
				</div>
			    </div>
			</c:if>
                </c:when>
                <c:otherwise>
                    You do not have permission to view this audit report.  Your request has been logged.
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</div>


<form action="batchOptions" id="batchOptions" method="post">
    <input type="hidden" id="idList" name="idList" value="${fn:substring(idList,1,fn:length(idList))}" />
    <input type="hidden" name="batchId" id="batchId" value="${batch.id}"/>
    <input type="hidden" name="batchOption" id="batchOption" value=""/>
</form>
<form action="auditReport" id="viewBatchAuditReport" method="post">
    <input type="hidden" id="auditbatchId" name="batchId" value="${batch.id}" />
</form>

<div class="modal fade" id="uploadFile" role="dialog" tabindex="-1"
     aria-labeledby="Upload New File" aria-hidden="true"
     aria-describedby="Upload New File"></div>
<div class="modal fade" id="statusModal" role="dialog" tabindex="-1"
     aria-labeledby="Status Details" aria-hidden="true"
     aria-describedby="Status Details"></div>