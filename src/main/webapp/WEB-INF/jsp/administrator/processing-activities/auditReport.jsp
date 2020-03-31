<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    
    <div class="row-fluid">
        
        <div class="col-md-12">
            <c:if test="${not empty error}" >
                    <div class="alert alert-danger" role="alert">
                        The selected file was not found.
                    </div>
                </c:if>
            <section class="panel panel-default">
		<div class="panel-heading clearfix" style="height: 50px">
		    <h3 class="panel-title pull-left" style="padding-top: 7.5px;">Audit Summary</h3>
		    <c:if test="${showButtons}">
			<div class="pull-right">
			    <div class="dropdown">
				<button class="btn btn-sm btn-default dropdown-toggle" type="button" data-toggle="dropdown">
				    <i class="fa fa-cog"></i> Batch Controls
				</button>
				<ul class="dropdown-menu pull-right">
				    <c:if test="${canSend == true}">
					<c:choose>
					    <c:when test="${batchDownload && (batchDetails.statusId == 64 || batchDetails.statusId == 59)}">
						<li>
						    <a href="#!" id="release" class="releaseOutboundBatch" rel="releaseBatch" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-ok-sign"></span>
							<strong>Process Outbound Batch</strong>
						    </a>
						</li>
					    </c:when>
					    <c:otherwise>
						<li>
						    <a href="#!" id="release" class="releaseBatch" rel="releaseBatch" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-ok-sign"></span>
							<strong>Process Inbound Batch</strong>
						    </a>
						</li>
					    </c:otherwise>
					</c:choose>
					<li class="divider"></li> 
				    </c:if>
				    <c:if test="${canEdit == true}">
					<li>
					    <a href="#!" id="rejectMessage" class="rejectMessages">
						<span class="glyphicon glyphicon-stop"></span>
						<strong>Reject Entire Inbound Batch</strong>
					    </a>
					</li>
					<li class="divider"></li> 
				    </c:if>
				    <c:if test="${batchDetails.statusId == 2}">
					<li>
					    <a href="#!" id="processBatch" class="processBatch" rel="processBatch" rel2="${batchDetails.id}">
						<span class="glyphicon glyphicon-upload"></span>
						<strong>Load Submitted Inbound Batch</strong>
					    </a>
					</li>
					<li class="divider"></li> 
				    </c:if>    
				    <c:if test="${batchDetails.statusId == 2 || batchDetails.statusId == 36}">
					<li>
					    <a href="#!" id="processBatch" class="processBatch" rel="processBatch" rel2="${batchDetails.id}">
						<span class="glyphicon glyphicon-filter"></span>
						<strong>Process Submitted Inbound Batch</strong>
					    </a>
					</li>
					<li class="divider"></li> 
				    </c:if>   
				    <c:if test="${canCancel && batchDetails.statusId != 4 && batchDetails.statusId != 24}">
					<c:choose>
					    <c:when test="${batchDownload}">
						<li>
						    <a href="#!" id="cancel" class="cancelOutboundBatch" rel="cancel" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-remove"></span>
							<strong>Cancel Outbound Batch</strong>
						    </a>
						</li>
					    </c:when>
					    <c:otherwise>
						<li>
						    <a href="#!" id="cancel" class="cancelBatch" rel="cancel" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-remove"></span>
							<strong>Cancel Inbound Batch</strong>
						    </a>
						</li>
					    </c:otherwise>
					</c:choose>
				    </c:if> 
				    <c:if test="${canReset && batchDetails.statusId != 64 && batchDetails.statusId != 42 && batchDetails.statusId != 43}">
					<c:choose>
					    <c:when test="${batchDownload}">
						<li class="divider"></li> 
						<li>
						    <a href="#!" id="reset" class="resetOutboundBatch" rel="reset" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-refresh"></span>
							<strong>Reset Outbound Batch</strong>
						    </a>
						</li>
					    </c:when>
					    <c:otherwise>
						<li class="divider"></li> 
						<li>
						    <a href="#!" id="reset" class="resetBatch" rel="reset" rel2="${batchDetails.id}">
							<span class="glyphicon glyphicon-refresh"></span>
							<strong>Reset Inbound Batch</strong>
						    </a>
						</li>
					    </c:otherwise>
					</c:choose>
				    </c:if>
				    <c:if test="${!canRest && configDetails.messageTypeId == 2}">
					<li class="divider"></li> 
					<li>
					    <a href="#!">
						<span class="glyphicon glyphicon-refresh"></span>
						<strong>Please Reset batch from the family planning system.</strong>
					    </a>
					</li>
				    </c:if>
				    <c:if test="${!batchDownload && sessionScope.userDetails.roleId == 1 && canReset}">
					<li class="divider"></li>
					<li>
					    <a href="#!" rel="${batchDetails.utBatchName}" class="deleteTransactions" title="Delete Inbound Batch">
						<span class="glyphicon glyphicon-remove"></span>
						<strong>Delete Inbound Batch</strong>
					    </a>
					</li>
				    </c:if>	
				</ul>
			    </div>
			</div>
		    </c:if>
		</div>
                <div class="panel-body">
                    <div class="row">
			<div class="col-md-12">
			    <div class="${!batchDownload ? 'col-md-4' : 'col-md-6'}">
				<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Received">
				    <div class="panel-body">
					<span class="stat-number"><a href="javascript:void(0);"><c:choose><c:when test="${batchDetails.totalRecordCount >= 0}"><fmt:formatNumber value = "${batchDetails.totalRecordCount}" type = "number"/></c:when><c:otherwise>0</c:otherwise></c:choose></a></span>
					<h3>Total Transactions</h3>
				    </div>
				</section>
			    </div>
			    <div class="${!batchDownload ? 'col-md-4' : 'col-md-6'}">
				<section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Delivered">
				    <div class="panel-body">
					<span class="stat-number">
					    <a href="javascript:void(0);">
						<c:choose>
						    <c:when test="${!batchDownload && batchDetails.errorRecordCount >= 0}">
							<fmt:formatNumber value = "${batchDetails.errorRecordCount}" type = "number"/>
						    </c:when>
						    <c:when test="${batchDownload && batchDetails.totalErrorCount >= 0}">
							<fmt:formatNumber value = "${batchDetails.totalErrorCount}" type = "number"/>
						    </c:when>
						    <c:otherwise>0</c:otherwise>
						</c:choose>
					    </a>
					</span>
					<h3>Total Errors in File</h3>
				    </div>
				</section>
			    </div>
                            <c:if test="${!batchDownload}">
                                <div class="col-md-4">
                                    <section class="panel panel-default panel-stats" role="widget" aria-labelleby="Messages Delivered">
                                        <div class="panel-body">
                                            <span class="stat-number">
                                                <a href="javascript:void(0);">
                                                    <fmt:formatNumber value = "${totalErroredRows}" type = "number"/>
                                                </a>
                                            </span>
                                            <h3>Total Rows with Errors</h3>
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
				    <c:choose>
					<c:when test="${batchDetails.configId > 0}">
					    <a href="/administrator/configurations/details?i=${batchDetails.configId}">${batchDetails.configName}</a>
					</c:when>
					<c:otherwise>
					    Could not figure out the source configuration based on the file uploaded.
					</c:otherwise>
				    </c:choose>
				</p>
			    </div>
			    <div class="col-md-6">
				<c:choose>
				    <c:when test="${batchDownload}">
					<c:if test="${not empty batchDetails.utBatchName}">
					    <c:set var="text" value="${fn:split(batchDetails.outputFileName,'.')}" />
					    <c:set var="ext" value="${text[fn:length(text)-1]}" />
					    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=outboundAudit&filename=${batchDetails.utBatchName}.${ext}&foldername=archivesOut&utBatchId=${batchDetails.utBatchName}"/>
					    <p><strong>Generated Target File (Located in archivesOut):</strong>
					    <br />
					    <c:choose>
						<c:when test="${batchDetails.statusId == 28}">
						    <a href="${hrefLink}" title="View Generated Target File">${batchDetails.utBatchName}.${ext}</a>
						</c:when>
						<c:when test="${batchDetails.statusId == 64}">
						    Outbound batch is ready to be processed
						</c:when>   
						<c:when test="${batchDetails.statusId == 32}">
						    Outbound batch was cancelled
						</c:when>  
						<c:when test="${batchDetails.statusId == 59}">
						    Outbound batch is scheduled to be processed
						</c:when>      
						<c:otherwise>
						    Outbound batch is processing
						</c:otherwise>
					    </c:choose>
					    </p>
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
						<c:when test="${canSend}">
						    Batch is ready to be processed
						</c:when>
						<c:otherwise>
						    Could not generate target file(s) because of errors 
						</c:otherwise>
					    </c:choose>
					</p> 
					<c:if test="${not empty batchDetails.originalFileName}">
					    <c:set var="text" value="${fn:split(batchDetails.originalFileName,'.')}" />
					    <c:set var="ext" value="${text[fn:length(text)-1]}" />
                                            
                                            <c:if test="${configDetails.configurationType == 1 && (batchDetails.transportMethodId == 10 || batchDetails.transportMethodId == 13)}">
                                                <c:choose>
                                                    <c:when test="${fn:contains(transportMethod,'direct') || batchDetails.transportMethodId == 13}">
                                                        <c:set var="hreftranslateLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=${batchDetails.utBatchName}.txt&foldername=loadFiles&utBatchId=${batchDetails.utBatchName}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="hreftranslateLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=archive_${batchDetails.utBatchName}.${fn:toLowerCase(ext)}&foldername=archivesIn&utBatchId=${batchDetails.utBatchName}"/>
                                                    </c:otherwise>
                                                </c:choose>

                                            </c:if>
					    <c:choose>
						<c:when test="${batchDetails.transportMethodId == 9 || batchDetails.transportMethodId == 12}">
						     <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=${batchDetails.utBatchName}.${fn:toLowerCase(ext)}&foldername=archivesIn&utBatchId=${batchDetails.utBatchName}"/>
						</c:when>
                                                <c:when test="${batchDetails.transportMethodId == 6}">
						     <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=${batchDetails.utBatchName}_dec.${fn:toLowerCase(ext)}&foldername=archivesIn&utBatchId=${batchDetails.utBatchName}"/>
						</c:when>
                                                <c:when test="${batchDetails.transportMethodId == 13}">
						     <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=archive_${batchDetails.utBatchName}.${fn:toLowerCase(ext)}&foldername=archivesIn&orgId=${batchDetails.orgId}&utBatchId=${batchDetails.utBatchName}"/>
						</c:when>
						<c:otherwise>
						    <c:set var="hrefLink" value="/FileDownload/downloadFile.do?fromPage=inboundAudit&filename=encoded_${batchDetails.utBatchName}.${fn:toLowerCase(ext)}&foldername=input files&orgId=${batchDetails.orgId}&utBatchId=${batchDetails.utBatchName}"/>
						</c:otherwise>
					    </c:choose>
					    <p>
						<strong>Uploaded File:</strong><br />
						<a href="${hrefLink}" title="View Original File">${batchDetails.originalFileName}</a>
						<c:if test="${batchDetails.transportMethodId == 9 || batchDetails.transportMethodId == 13}">
						    <br /><a href="${hreftranslateLink}" title="View Translated File">Download Translated File</a></p>
						</c:if>
					    </p>
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
    <c:if test="${not empty batchDroppedValues}">
	<div class="row-fluid">
	    <div class="col-md-12">
		<section class="panel panel-default">
		    <div class="panel-heading">
			<h3 class="panel-title">Dropped Values</h3>
		    </div>
		    <div class="panel-body">
                        <div class="col-md-12">
                            <section class="panel panel-default">
                                <div class="panel-heading">
                                    <a data-toggle="collapse" rel="${batchDetails.id}" total="${fn:length(batchDroppedValues)}" rel3="${batchDownload ? 'outbound' : 'inbound'}" class="droppedValueCollapse" href="#collapse-droppedValues">
                                       <div class="clearfix">
                                            <div class="pull-left">
                                                <h3 class="panel-title">Error: Dropped Values</h3>
                                            </div>
                                            <div class="pull-right">
                                                <h3 class="panel-title" style="color:red">Total Found: <fmt:formatNumber value = "${fn:length(batchDroppedValues)}" type = "number"/></h3>
                                            </div>
                                        </div>
                                    </a>	
                                </div>
                                <div id="collapse-droppedValues" class="panel-collapse collapse">       
                                    <div class="panel-body clearfix" style="height:300px;">
                                        <div class="col-md-12 clearfix">
                                            <div class="col-md-6 col-md-offset-6 spinner-droppedValues">
                                                <i class="fa fa-spinner fa-spin fa-4x"></i>
                                            </div>
                                            <div class="errorList-droppedValues" style=" overflow: auto; height:250px;"></div>
                                        </div>
                                    </div>
                                </div>            
                            </section>
                        </div>
		    </div>
		</section>
	    </div>
	</div>
    </c:if>                        
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
                                        <a data-toggle="collapse" rel="${batchDetails.id}" error="${batchError.errorId}" total="${batchError.totalErrors}" rel2="${i.index}" rel3="${batchDownload ? 'outbound' : 'inbound'}" class="errorCollapse" href="#collapse-${i.index}">
                                           <div class="clearfix">
                                                <div class="pull-left">
                                                    <h3 class="panel-title">Error: ${batchError.errorDisplayText} <c:if test="${batchError.fromOutboundConfig}"><span class="text-info">(From Outbound Config)</span></c:if></h3>
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

<form action="../../editTransaction" id="editTransaction" method="post">
    <input type="hidden" id="transactionInId" name="transactionInId" value="" />
</form>

<div class="modal fade" id="statusModal" role="dialog" tabindex="-1" aria-labeledby="Status Details" aria-hidden="true" aria-describedby="Status Details"></div>
<div class="modal fade" id="messageDetailsModal" role="dialog" tabindex="-1" aria-labeledby="Message Details" aria-hidden="true" aria-describedby="Message Details"></div>
