<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentBucket" value="0" />

<div class="main clearfix" role="main">
    <div class="row-fluid">
        <div class="col-md-12">
            <c:choose>
                <c:when test="${not empty savedStatus}" >
                    <c:choose>
                        <c:when test="${savedStatus == 'updated'}"><div class="alert alert-success"><strong>Success!</strong> The configuration pre-process macros have been successfully added!</div></c:when>
                    </c:choose>
                </c:when>
                <c:when test="${not empty param.msg}" >
                    <div class="alert alert-success">
                        <strong>Success!</strong> 
                        <c:choose>
                            <c:when test="${param.msg == 'updated'}">The pre-process macros has been successfully added!</c:when>
                        </c:choose>
                    </div>
                </c:when>
            </c:choose>
            <section class="panel panel-default">
                <div class="panel-body">
                    <dt>
                    <dt>Configuration Summary:</dt>
			<dd><strong>Organization:</strong> ${configurationDetails.orgName}</dd>
			<dd><strong>Configuration Type:</strong> <span id="configType" rel="${configurationDetails.type}"><c:choose><c:when test="${configurationDetails.type == 1}">Source</c:when><c:otherwise>Target</c:otherwise></c:choose></span></dd>
                    	<dd><strong>Configuration Name:</strong> ${configurationDetails.configName}</dd>
			<dd><strong>Transport Method:</strong> <c:choose><c:when test="${configurationDetails.transportMethod == 'File Upload'}"><c:choose><c:when test="${configurationDetails.type == 1}">File Upload</c:when><c:otherwise>File Download</c:otherwise></c:choose></c:when><c:otherwise>${configurationDetails.transportMethod}</c:otherwise></c:choose></dd>
		    </dt>
		</div>
	    </section>   
	</div>
    </div>
    <div class="row-fluid">
	<div class="col-md-6">
	    <section class="panel panel-default">
		<div class="panel-heading">
		    <h3 class="panel-title">New Pre-Process Macros</h3>
		</div>
		<div class="panel-body">
		    <div class="form-container">
			<div id="fieldDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldNumber">Field</label>
                            <select id="field" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${fields}" var="field" varStatus="fStatus">
				    <option value="${fields[fStatus.index].id}" rel="${fields[fStatus.index].fieldNo}" id="o${fields[fStatus.index].id}">${fields[fStatus.index].fieldDesc} - F${fields[fStatus.index].fieldNo} </option>
				</c:forEach>
                            </select>
                            <span id="fieldMsg" class="control-label"></span>
                        </div>
                        <div id="macroDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for=""macro"">Macro <a href="#macroModal" data-toggle="modal" class="macroDefinitions">(View Macro Definitions)</a></label>
                            <select id="macro" class="form-control half">
                                <option value="">- Select -</option>
                                <c:forEach items="${macros}" var="macro" varStatus="mStatus">
                                    <option value="${macro.id}">${macro.macroName}</option>
                                </c:forEach>
                            </select>
                            <span id="macroMsg" class="control-label"></span>
                        </div>
                        <div class="form-group">
                            <label class="control-label" for="passclear">Pass/Clear Error</label>
                            <div>
                                <label class="radio-inline">
                                    <input type="radio" name="passClear" class="passclear" value="1" checked />Pass Error 
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="passClear" class="passclear" value="2" />Clear Error
                                </label>
                            </div>
                        </div>
                        <div id="fieldADiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldA">Field A</label>
                            <input path="fieldA" id="fieldA" class="form-control" type="text" maxLength="45" />
                            <span id="fieldAMsg" class="control-label"></span>
                        </div>
                        <div id="fieldBDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="fieldB">Field B</label>
                            <input path="fieldB" id="fieldB" class="form-control" type="text" maxLength="45" />
                            <span id="fieldBMsg" class="control-label"></span>
                        </div>
                        <div id="constant1Div" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="constant1">Constant 1</label>
                            <input path="constant1" id="constant1" class="form-control" type="text" maxLength="45" />
                            <span id="constant1Msg" class="control-label"></span>
                        </div>
                        <div id="constant2Div" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="constant2">Constant 2</label>
                            <input path="constant2" id="constant2" class="form-control" type="text" maxLength="45" />
                            <span id="constant2Msg" class="control-label"></span>
                        </div>
                        <div class="form-group">
                            <input type="button" id="submitTranslationButton"  class="btn btn-primary" value="Add Translation"/>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</div>

<div class="main clearfix" role="main">	
    <%-- Existing Translations --%>
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Existing Pre-Process Macros</h3>
            </div>
            <div class="panel-body">
                <div id="translationMsgDiv"  rel="${id}" class="alert alert-danger" style="display:none;">
                    <strong>You must click SAVE above to submit the macros listed below!</strong>
                </div>
                <div class="form-container scrollable" id="existingTranslations"></div>
            </div>
        </section>
    </div>
</div>
<input type="hidden" id="orgId" value="${orgId}" />
<input type="hidden" id="macroLookUpList" value="${macroLookUpList}" />

<%-- Provider Address modal --%>
<div class="modal fade" id="macroModal" role="dialog" tabindex="-1" aria-labeledby="Macro Details" aria-hidden="true" aria-describedby="Macro Details"></div>