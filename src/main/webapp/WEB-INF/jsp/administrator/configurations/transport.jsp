<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:if test="${not empty savedStatus}" >
                <div class="alert alert-success">
                    <strong>Success!</strong> 
                    <c:choose><c:when test="${savedStatus == 'updated'}">The configuration transport details have been successfully updated!</c:when><c:otherwise>The configuration initial setup has been saved!</c:otherwise></c:choose>
                </div>
            </c:if>
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Configuration Summary</h3>
                </div>
                <div class="panel-body">
                    <p><strong>Organization:</strong> ${configurationDetails.orgName}</p>
                    <p><strong>Configuration Name:</strong> ${configurationDetails.configName}</p>
                    <p><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></p>
                    <c:if test="${not empty lastConfigUpdate}"><p><strong>Template file last updated on:</strong> <fmt:formatDate value="${lastConfigUpdate}" type="date" pattern="M/dd/yyyy h:mm a" /></p></c:if>
                </div>
            </section>
            <c:if test="${not empty error}" >
                <div class="alert alert-danger" role="alert">
                    The selected file was not found.
                </div>
            </c:if>
        </div>
    </div>                       
    <div class="row-fluid">                    
       <div class="col-md-12">
	    <c:choose>
		<c:when test="${configurationDetails.type == 1}">
		    <jsp:include page="inbound/transport.jsp" />
		</c:when>
		<c:otherwise>
		    <jsp:include page="outbound/transport.jsp" />
		</c:otherwise>
	    </c:choose>
        </div>     
    </div>
</div>

<div class="modal fade" id="domainModal" role="dialog" tabindex="-1" aria-labeledby="Add / Edit Domains" aria-hidden="true" aria-describedby="Add / Edit Domains"></div>
