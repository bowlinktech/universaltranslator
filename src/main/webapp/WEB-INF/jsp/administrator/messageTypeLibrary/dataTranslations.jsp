<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentBucket" value="0" />

<div class="main clearfix" role="main">

    <div class="col-md-12">
        <c:choose>
            <c:when test="${not empty savedStatus}" >
                <c:choose>
                    <c:when test="${savedStatus == 'updated'}"><div class="alert alert-success"><strong>Success!</strong> The message type field specs have been successfully updated!</div></c:when>
                    <c:when test="${savedStatus == 'created'}"><div class="alert alert-success"><strong>Success!</strong> The crosswalk has been successfully created!</div></c:when>
                    <c:when test="${savedStatus == 'error'}"><div class="alert alert-danger"><strong>Error!</strong> The uploaded crosswalk did not have the correct delimiter!</div></c:when>
                </c:choose>
            </c:when>
            <c:when test="${not empty param.msg}" >
                <div class="alert alert-success">
                    <strong>Success!</strong> 
                    <c:choose>
                        <c:when test="${param.msg == 'updated'}">The field specs have been successfully updated!</c:when>
                        <c:when test="${param.msg == 'created'}">The crosswalk has been successfully added!</c:when>
                    </c:choose>
                </div>
            </c:when>
        </c:choose>
    </div>

    <div class="col-md-6">

        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">New Field Spec</h3>
            </div>
            <div class="panel-body">
                <div class="form-container">
                    <div class="form-group">
                        <label class="control-label" for="fieldNumber">Field</label>
                        <select id="field" class="form-control half">
                            <option value="">- Select -</option>
                            <c:forEach items="${fields}" var="field" varStatus="fStatus">
                                <option value="${fields[fStatus.index].id}">${fields[fStatus.index].fieldDesc} </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="control-label" for="fieldNumber">Crosswalk</label>
                        <select id="crosswalk" class="form-control half">
                            <option value="">- Select -</option>
                            <c:forEach items="${crosswalks}" var="cwalk" varStatus="cStatus">
                                <option value="${crosswalks[cStatus.index].id}">${crosswalks[cStatus.index].name} </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- Existing Crosswalks -->
    <div class="col-md-6">
        <section class="panel panel-default">
            <div class="panel-heading">
                <div class="pull-right">
                    <a href="#crosswalkModal" data-toggle="modal" class="btn btn-primary btn-xs btn-action" id="createNewCrosswalk" title="Add New Crosswalk">Add New Crosswalk</a>
                </div>
                <h3 class="panel-title">Available Crosswalks</h3>
            </div>
            <div class="panel-body">
                <div class="form-container scrollable">
                    <div id="crosswalksTable"></div>
                </div>
            </div>
        </section>
    </div>

</div>

<div class="main clearfix" role="main">	
    <!-- Existing Translations -->
    <div class="col-md-12">
        <section class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">Existing Field Specs</h3>
            </div>
            <div class="panel-body">
                <div id="translationMsgDiv"  rel="${id}" class="alert alert-danger" style="display:none;">
                    <strong>You must click SAVE above to submit the field specs listed below!</strong>
                </div>
                <div class="form-container scrollable" id="existingTranslations"></div>
            </div>
        </section>
    </div>

</div>

<!-- Crosswalk Details modal -->
<div class="modal fade" id="crosswalkModal" role="dialog" tabindex="-1" aria-labeledby="Message Crosswalks" aria-hidden="true" aria-describedby="Message Crosswalks"></div>