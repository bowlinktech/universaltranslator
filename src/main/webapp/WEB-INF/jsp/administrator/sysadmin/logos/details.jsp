<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success">
                <strong>Success!</strong> 
                <c:if test="${savedStatus == 'updated'}">Logo file(s) are updated!</c:if>
                </div>
        </c:if>
        <form:form id="logoForm" commandName="logoDetails" modelAttribute="logoDetails" 
                   method="post" enctype="multipart/form-data" role="form">
            <input type="hidden" id="action" name="action" value="save" />
            <form:hidden path="id" id="id" />
            <form:hidden path="frontEndLogoName" id="frontEndLogoName" />
            <form:hidden path="backEndLogoName" id="backEndLogoName" />
            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Logos</h3>
                </div>
                <div class="form-group">
                    <label class="control-label" for="templateImage">Current Front End Logo
                        <img src="../../dspResources/img/front-end/health-e-link/${logoDetails.frontEndLogoName}" alt="Health-e-link" border="0"/>
                    </label>

                </div>


                <div id="frontEndFileDiv" class="form-group">
                    <spring:bind path="frontEndFile">
                        <div id="frontEndFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="frontEndFile">New Front End Logo File 
                                <span class="badge badge-help"  data-toggle="tooltip" data-placement="top" title="" data-original-title="Only jpg, png and gif files are accepted">?</span></label>
                                <form:input path="frontEndFile" id="frontEndFile" type="file" />
                            <span id="frontEndFileMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                </div>
                <div class="form-group">
                    <label class="control-label" for="templateFile">Current Back End Logo</label>
                    <img src="../../dspResources/img/admin/health-e-link/${logoDetails.backEndLogoName}" alt="Health-e-link" border="0"/>
                </div>
                <div id="backEndFileDiv" class="form-group">
                    <spring:bind path="backEndFile">
                        <div id="backEndFileDiv" class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="backEndFile">New Back End Logo File <span class="badge badge-help"  data-toggle="tooltip" data-placement="top" title="" data-original-title="Only jpg, png and gif files are accepted">?</span></label>
                            <form:input path="backEndFile" id="backEndFile" type="file" />
                            <span id="backEndFileMsg" class="control-label"></span>
                        </div>
                    </spring:bind>
                </div>        


            </section>
        </form:form>
    </div>
</div>