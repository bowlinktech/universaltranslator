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
            <h3 class="panel-title">Upload Configuration Import File</h3>
        </div>
        <div class="modal-body">
            <div id="errorMsg" style="display:none;">
                <div class="alert alert-danger" id="importError">
                    An error occurred trying to upload the configuration file.
                </div>
            </div>
            <form id="importConfigFileForm"  enctype="multipart/form-data" method="post" role="form">
                <input type="hidden" id="expectedExt" value="${expectedExt}" />
                
                <p style="padding-bottom:20px;font-weight: bold" class="text-danger">
                    The importing of a configuration could take a few minutes. You will receive an email when the importing process has been completed and the 
                   configuration is available.
                </p>
                
                <div class="form-container">
                    <div id="importConfigFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                        <label class="control-label" for="importConfigFile">Configuration Import File *</label>
                        <input type="file" name="importConfigFile" id="importConfigFile" type="file"  />
                        <span id="importConfigFileMsg" class="control-label"></span>
                    </div>
                    <div class="form-group">
			<input type="button" id="submitImportFileButton" class="btn btn-primary" value="Upload Import File"/>
		    </div>
                </div>
            </form>
        </div>
    </div>
</div>


