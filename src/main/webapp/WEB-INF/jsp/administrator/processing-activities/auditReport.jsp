<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
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
					    <div class="btn-group-vertical">
						<c:if test="${canSend == true}">
						    <input type="button" id="release" class="btn btn-success btn-small releaseBatch" rel="releaseBatch" rel2="${batchDetails.id}"  value="Release" />
						</c:if>
						<c:if test="${canEdit}">
						    <input type="button" id="rejectMessages" class="btn btn-danger btn-small rejectMessages" value="Reject All Errors" />
						</c:if>
						<c:if test="${batchDetails.statusId == 2}">
						    <input type="button" id="processBatch" class="btn btn-success btn-small processBatch" rel="processBatch" rel2="${batchDetails.id}" value="Load Batch" />
						</c:if>
						<c:if test="${batchDetails.statusId == 3 || batchDetails.statusId == 36}">
						    <input type="button" id="processBatch" class="btn btn-success btn-small processBatch" rel="processBatch" rel2="${batchDetails.id}" value="Process Batch" />
						</c:if>
						<c:if test="${canCancel && batchDetails.statusId != 4}">
						    <c:choose>
							<c:when test="${batchDownload}">
							     <input type="button" id="cancel" class="btn btn-danger btn-small cancelOutboundBatch" rel="cancel"  rel2="${batchDetails.id}" value="Cancel" />
							</c:when>
							<c:otherwise>
							     <input type="button" id="cancel" class="btn btn-danger btn-small cancelBatch" rel="cancel"  rel2="${batchDetails.id}" value="Cancel" />
							</c:otherwise>
						    </c:choose>
						</c:if>
						<c:if test="${canReset}">
						    <c:choose>
							<c:when test="${batchDownload}">
							     <input type="button" id="reset" class="btn btn-warning btn-small resetOutboundBatch" rel="reset"  rel2="${batchDetails.id}" value="Reset" />
							</c:when>
							<c:otherwise>
							     <input type="button" id="reset" class="btn btn-warning btn-small resetBatch" rel="reset"  rel2="${batchDetails.id}" value="Reset" />
							</c:otherwise>
						    </c:choose>
						</c:if>	
					    </div>
					</div>
				    </section>
				</div>
			    </c:if>
			</div>	
			<div class="col-md-12 printHeading">
			    <div class="col-md-6">
				<p><strong>Sending Organization:</strong><br />${batchDetails.orgName}</p>
				<p>
				    <c:choose>
					<c:when test="${batchDownload}">
					    <strong>Date Created:</strong><br /><fmt:formatDate value="${batchDetails.dateCreated}" type="both" pattern="M/dd/yyyy h:mm:ss a" />
					</c:when>
					<c:otherwise>
					    <strong>Date Uploaded:</strong><br /><fmt:formatDate value="${batchDetails.dateSubmitted}" type="both" pattern="M/dd/yyyy h:mm:ss a" />
					</c:otherwise> 
				    </c:choose>
				</p>
				<p>
				    <strong>Configuration:</strong>
				    <br />
				    <a href="/administrator/configurations/details?i=${batchDetails.configId}">${batchDetails.configName}</a>
				</p>
			    </div>
			    <div class="col-md-6">
				<c:choose>
				    <c:when test="${batchDownload}">
					<c:if test="${not empty batchDetails.utBatchName}">
					    <c:set var="text" value="${fn:split(batchDetails.outputFIleName,'.')}" />
					    <c:set var="ext" value="${text[fn:length(text)-1]}" />
					    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?filename=${batchDetails.utBatchName}.${ext}&foldername=archivesOut"/>
					    <p><strong>Generated Target File (Located in archivesOut):</strong><br /><a href="${hrefLink}" title="View Generated Target File">${batchDetails.utBatchName}.${ext}</a></p>
					</c:if>
				    </c:when>
				    <c:otherwise>
					<p>
					    <strong>Related Outbound Batches</strong><br />
					    <c:choose>
						<c:when test="${not empty batchDetails.relatedBatchDownloadIds}">
						    <c:forEach items="${batchDetails.relatedBatchDownloadIds}" var="batchDownloadId">
							<a href="/administrator/processing-activity/outbound/auditReport/${batchDownloadId}">${batchDownloadId}</a><br />
						    </c:forEach>
						</c:when>
					    </c:choose>
					</p> 
					<c:if test="${not empty batchDetails.originalFileName}">
					    <c:set var="text" value="${fn:split(batchDetails.originalFileName,'.')}" />
					    <c:set var="ext" value="${text[fn:length(text)-1]}" />
					    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?filename=archive_${batchDetails.utBatchName}.${ext}&foldername=archivesIn"/>
					    <p><strong>Uploaded File:</strong><br /><a href="${hrefLink}" title="View Original File">${batchDetails.originalFileName}</a></p>
					</c:if>
				    </c:otherwise>
				</c:choose>
				
				<p><strong>Batch Id:</strong><br />${batchDetails.utBatchName}</p>
			    </div>
			 </div>   
		    </div>
                </div>
            </section>
        </div>
    </div>
    <c:if test="${not empty batchErrorSummary}">
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
					<a data-toggle="collapse" rel="${batchDetails.id}" error="${batchError.errorId}" total="${batchError.totalErrors}" rel2="${i.index}" rel3="${OutboundBatch ? 'outbound' : 'inbound'}" class="errorCollapse" href="#collapse-${i.index}">
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
</div>

<form action="../../rejectMessages" id="massReject" method="post">
    <input type="hidden" id="idList" name="idList" value="${fn:substring(idList,1,fn:length(idList))}" />
    <input type="hidden" name="batchId" id="batchId" value="${batchDetails.id}"/>
</form>

<form action="../../editTransaction" id="editTransaction" method="post">
    <input type="hidden" id="transactionInId" name="transactionInId" value="" />
</form>

<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>
