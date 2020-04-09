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
            <h3 class="panel-title">Upload File</h3>
        </div>
        <div class="modal-body">
            <div id="successMsg" style="display:none;">
                <div class="alert alert-success">
                    Your uploaded file has been submitted for processing. <br /> Click <a href="/administrator/processing-activity/inbound">here</a> to go to view processing activities.
                </div>
            </div>
            <div id="errorMsg" style="display:none;">
                <div class="alert alert-danger">
                    An error occurred trying to upload the file for this configuration.
                </div>
            </div>
            <form id="configFileForm"  enctype="multipart/form-data" method="post" role="form">
                <input type="hidden" name="fileDropLocation" value="${fileDropLocation}" />
                <input type="hidden" id="expectedExt" value="${expectedExt}" />
                
                <div class="form-container">
                    <div style="margin-bottom:10px;">
                        Upload the file for this configuration to process.
                    </div>
                    <div id="configFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                        <label class="control-label" for="configFile">File to Process *</label>
                        <input type="file" name="configFile" id="configFile" type="file"  />
                        <span id="configFileMsg" class="control-label"></span>
                    </div>
                    <div class="form-group">
			<input type="button" id="submitFileButton" class="btn btn-primary" value="Upload File"/>
		    </div>
                </div>
            </form>
        </div>
    </div>
</div>


