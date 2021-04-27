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
            <h3 class="panel-title">Generate Crosswalk File</h3>
        </div>
        <div class="modal-body">
	    <div id="successMsg" style="display:none;"><p class="text-success">The Crosswalk Excel file has successfully downloaded (Check your downloads folder)</p></div>
	    <div id="errorMsg" style="display:none;"><p class="text-danger">No crosswalks were found.</p></div>
            <div class="form-container">
                <p>
                    The generated file will contain a list of all crosswalks associated to the selected configuration. If you choose to generate an Excel file each crosswalk
                    would be on its own tab. If you choose to generate a TXT file all the crosswalks would be listed on a single page.
                </p>
                <div class="form-group">
                    <label for="type">File Type *</label>
                    <div>
                        <label class="radio-inline">
                            <input type="radio" id="fileType" name="fileType" value="1" class="fileType" checked /> Excel (XLSX) File
                        </label>
                        <label class="radio-inline">
                            <input type="radio" id="fileType" name="fileType" value="2" class="fileType" /> TXT File
                        </label>
                    </div>
                </div>
		<div class="form-group ${status.error ? 'has-error' : '' }" id="cwnameDiv">
		    <label class="control-label" for="name">Crosswalk File Name *</label>
		    <input type="text" id="fileName" class="form-control" type="text" value="${fileName}" readonly="" maxLength="55" />
		    <div class="control-label has-error" id="nameLabelDiv"></div>
		</div>
		<div class="form-group">
		    <input type="button" id="generateCWButton" rel="${configId}" role="button" class="btn btn-primary" value="Generate Crosswalk Excel File"/>
		</div>
            </div>
        </div>
    </div>
</div>


