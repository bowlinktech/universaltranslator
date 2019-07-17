<%-- 
    Document   : newjspentityCWForm
    Created on : May 7, 2018, 8:30:25 AM
    Author     : chadmccue
--%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Generate Data Translations CSV File</h3>
        </div>
        <div class="modal-body">
	    <div id="successMsg" style="display:none;"><p class="text-success">The Data Translation CSV file has successfully downloaded (Check your downloads folder)</p></div>
	    <div id="errorMsg" style="display:none;"><p class="text-danger">No data translations were found.</p></div>
            <div class="form-container">
		<div class="form-group ${status.error ? 'has-error' : '' }" id="dtnameDiv">
		    <label class="control-label" for="name">Data Translations File Name *</label>
		    <input type="text" id="fileName" class="form-control" type="text" value="${fileName}" readonly="" maxLength="55" />
		    <div class="control-label has-error" id="nameLabelDiv"></div>
		</div>
		<div class="form-group">
		    <input type="button" id="generateDTButton" rel="${configId}" role="button" class="btn btn-primary" value="Generate Data Translations CSV File"/>
		</div>
            </div>
        </div>
    </div>
</div>


