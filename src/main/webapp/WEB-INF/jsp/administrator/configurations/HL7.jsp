<%-- 
    Document   : HL7
    Created on : Feb 21, 2014, 12:43:10 PM
    Author     : chadmccue
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="currentBucket" value="0" />
<div class="main clearfix" role="main">

    <div class="col-md-12">

        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success" role="alert">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'savedSegment'}">The new segment has been successfully saved!</c:when>
                    <c:when test="${savedStatus == 'savedElement'}">The new element has been successfully saved!</c:when>
                    <c:when test="${savedStatus == 'savedComponent'}">The new component has been successfully saved!</c:when>
                    <c:otherwise>The HL7 Details have been saved</c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${not empty HL7Specs}">
                <section class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><strong>Choose an Existing HL7 Spec</strong></h3>
                    </div>
                    <div class="panel-body">
                        <div id="HL7SpecDiv" class="form-container scrollable">
                            <label class="control-label" for="HL7Spec">HL7 Specs *</label>
                            <select id="HL7Spec" rel="${id}"  class="form-control">
                                <option value="">- Select an HL7 Spec -</option>
                                <c:forEach items="${HL7Specs}" var="specs" varStatus="spec">
                                    <option value="${specs.id}">${specs.name}</option>
                                </c:forEach>
                            </select>
                            <span id="HL7SpecMsg" class="control-label"></span>
                        </div>
                    </div>
                </section>
            </c:when>
            <c:otherwise>
                <form:form id="HL7Details" modelAttribute="HL7Details" method="post" role="form">
                    <form:hidden id="hl7Id" path="id" />
                    <form:hidden path="configId" /> 
                    <section class="panel panel-default">
                        <div class="panel-heading">
                            <c:if test="${HL7Details.id > 0}">
                                <div class="pull-right">
                                    <a href="#newSegmentModal" data-toggle="modal" class="btn btn-primary btn-xs btn-action" id="addNewSegment" title="Add new segment">Add New Segment</a>
                                </div>
                            </c:if>
                            <h3 class="panel-title"><strong>HL7 Details</strong></h3>
                        </div>
                        <div class="panel-body">
                            <div class="form-container scrollable">
                                <spring:bind path="fieldSeparator">
                                    <div id="fieldSeparatorDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="fieldSeparator">Field Separator *</label>
                                        <form:input path="fieldSeparator" id="fieldSeparator" class="form-control" type="text" maxLength="1" />
                                        <span id="fieldSeparatorMsg" class="control-label"></span>
                                    </div>
                                </spring:bind>  
                            </div>
                            <div class="form-container scrollable">
                                <spring:bind path="componentSeparator">
                                    <div id="componentSeparatorDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="componentSeparator">Component Separator *</label>
                                        <form:input path="componentSeparator" id="componentSeparator" class="form-control" type="text" maxLength="1" />
                                        <span id="componentSeparatorMsg" class="control-label"></span>
                                    </div>
                                </spring:bind>  
                            </div>
                            <div class="form-container scrollable">
                                <spring:bind path="EscapeChar">
                                    <div id="EscapeCharDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="EscapeChar">Escape Character *</label>
                                        <form:input path="EscapeChar" id="EscapeChar" class="form-control" type="text" maxLength="1" />
                                        <span id="EscapeCharMsg" class="control-label"></span>
                                    </div>
                                </spring:bind>  
                            </div>
                        </div>
                    </section>

                    <c:forEach items="${HL7Details.HL7Segments}" var="segments" varStatus="segment">
                        <input type="hidden" name="HL7Segments[${segment.index}].id" value="${segments.id}" />
                        <input type="hidden" name="HL7Segments[${segment.index}].hl7Id" value="${segments.hl7Id}" />
                        <input type="hidden" class="segmentPos" name="HL7Segments[${segment.index}].displayPos" value="${segments.displayPos}" />
                        <section class="panel panel-default"  id="segmentrow_${segments.id}">
                            <div class="panel-heading">
                                <div class="pull-right">
                                    <a href="#newSegmentElement" data-toggle="modal" rel="${segments.hl7Id}" rel2="${segments.id}" class="btn btn-primary btn-xs btn-action addNewElement" title="Add new Element">Add New Element</a>
                                    <a href="javascript:void(0);" rel="${segments.id}" class="btn btn-primary btn-xs btn-action deleteSegment" title="Delete Segment">Delete Segment</a>
                                </div>
                                <h3 class="panel-title">
                                    <a data-toggle="collapse" href="#collapse-${segments.id}">
                                        <strong>Segment ${segments.displayPos} (${segments.segmentName})</strong>
                                    </a>
                                </h3>
                            </div>
                            <div id="collapse-${segments.id}" class="panel-collapse ${segment.index+1 != HL7Details.HL7Segments.size() ? 'collapse' : ''}">       
                                <div class="panel-body">
                                    <div id="segmentName_${segment.index}" class="form-group ${status.error ? 'has-error' : '' }">
                                        <label class="control-label" for="segmentName">Segment Name *</label>
                                        <input type="text" id="segmentName" name="HL7Segments[${segment.index}].segmentName" maxlength="3" value="${segments.segmentName}" rel="${segment.index}" class="form-control segmentName" />
                                        <span id="segmentNameMsg_${segment.index}" class="control-label"></span>
                                    </div>
                                    <br />

                                    <!-- Loop through the segment elements -->
                                    <c:forEach items="${segments.HL7Elements}" var="elements" varStatus="element">
                                        <input type="hidden" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].id" value="${elements.id}" />
                                        <input type="hidden" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].hl7Id" value="${elements.hl7Id}" />
                                        <input type="hidden" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].segmentId" value="${elements.segmentId}" />
                                        <input type="hidden" class="displayPos_${elements.segmentId}" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].displayPos" value="${elements.displayPos}" />
                                        <section class="panel panel-default" id="elementrow_${elements.id}">
                                            <div class="panel-heading" style="background-color: lightgoldenrodyellow">
                                                <div class="pull-right">
                                                    <a href="#newSegmentElement" data-toggle="modal" rel="${elements.id}" class="btn btn-primary btn-xs btn-action addNewComponent" title="Add new Component">Add New Component</a>
                                                    <a href="javascript:void(0);" rel="${elements.id}" class="btn btn-primary btn-xs btn-action deleteElement" title="Delete Element">Delete Element</a>
                                                </div>
                                                <h3 class="panel-title"><strong>Element ${elements.displayPos}</strong></h3>
                                            </div>
                                            <div class="panel-body">
                                                <div class="form-container scrollable">
                                                    <div class="row">
                                                        <div id="elementName_${segment.index}${element.index}" class="form-group col-md-6">
                                                            <label class="control-label" for="elementName">Name *</label>
                                                            <input type="text" id="elementName" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementName" value="${elements.elementName}" rel="${segment.index}${element.index}" maxLength="255" class="form-control elementName" />
                                                            <span id="elementNameMsg_${segment.index}${element.index}" class="control-label"></span>
                                                        </div>
                                                        <div id="elementdefaultValue_${segment.index}${element.index}" class="form-group col-md-6">
                                                            <label class="control-label" for="defaultValue">Default Value</label>
                                                            <input type="text" id="defaultValue" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].defaultValue" value="${elements.defaultValue}" maxLength="45" class="form-control elementdefaultValue" />
                                                            <span id="elementdefaultValueMsg_${segment.index}${element.index}" class="control-label"></span>
                                                        </div>
                                                    </div>
                                                </div>

                                                <!-- Need to loop through any components -->
                                                <c:if test="${elements.elementComponents.size() > 0}">
                                                    <br /><div style="padding-bottom:10px;"><strong style="text-decoration:underline">Components</strong></div>
                                                    <c:forEach items="${elements.elementComponents}" var="elementComponents" varStatus="component">
                                                        <input type="hidden" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].id" value="${elementComponents.id}" />
                                                        <input type="hidden" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].elementId" value="${elementComponents.elementId}" />
                                                        <input type="hidden" class="displayPos_${elementComponents.elementId}" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].displayPos" value="${elementComponents.displayPos}" />
                                                        <div class="form-container scrollable">
                                                            <div class="row" id="componentrow_${elementComponents.id}">
                                                                <div class="form-group col-md-6">
                                                                    <label class="control-label" for="fieldDescriptor">Descriptor</label>
                                                                    <input type="text" id="fieldDescriptor" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].fieldDescriptor" value="${elementComponents.fieldDescriptor}" maxLength="255" class="form-control fieldLabel" />
                                                                </div>
                                                                <div class="form-group col-md-6">
                                                                    <label class="control-label" for="defaultValue">Default Value</label>
                                                                    <input type="text" id="defaultValue" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].defaultValue" value="${elementComponents.defaultValue}" maxLength="45" class="form-control fieldLabel" />
                                                                </div>
                                                                <div class="form-group col-md-6">
                                                                    <label class="control-label" for="fieldAppendText">Append Text</label>
                                                                    <input type="text" id="fieldAppendText" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].fieldAppendText" value="${elementComponents.fieldAppendText}" maxLength="255" class="form-control fieldLabel" />
                                                                </div>
                                                                <div class="form-group col-md-6">
                                                                    <label class="control-label" for="componentField">Field</label>
                                                                    <select id="componentField" name="HL7Segments[${segment.index}].HL7Elements[${element.index}].elementComponents[${component.index}].fieldValue" class="form-control half">
                                                                        <option value="">- Select -</option>
                                                                        <c:forEach items="${fields}" var="field" varStatus="fStatus">
                                                                            <c:if test="${currentBucket != fields[fStatus.index].bucketNo}">
                                                                                <c:if test="${currentBucket > 0}"></optgroup></c:if>
                                                                                <c:set var="currentBucket" value="${fields[fStatus.index].bucketNo}" />
                                                                                <c:choose>
                                                                                    <c:when test="${currentBucket == 1}">
                                                                                        <optgroup label="(Sender Organiation Information)">
                                                                                        </c:when>
                                                                                        <c:when test="${currentBucket == 2}">
                                                                                        <optgroup label="(Sender Provider Information)">
                                                                                        </c:when>     
                                                                                        <c:when test="${currentBucket == 3}">
                                                                                        <optgroup label="(Recipient Organization Information)">
                                                                                        </c:when>
                                                                                        <c:when test="${currentBucket == 4}">
                                                                                        <optgroup label="(Recipient Provider Information)">
                                                                                        </c:when>
                                                                                        <c:when test="${currentBucket == 5}">
                                                                                        <optgroup label="(Patient Information)">
                                                                                        </c:when>
                                                                                        <c:when test="${currentBucket == 6}">
                                                                                        <optgroup label="(Details)">
                                                                                        </c:when>
                                                                                    </c:choose>
                                                                                </c:if>
                                                                                <option value="${fields[fStatus.index].fieldNo}" <c:if test="${fields[fStatus.index].fieldNo == elementComponents.fieldValue}">selected</c:if>>${fields[fStatus.index].fieldLabel} </option>
                                                                            </c:forEach>
                                                                    </select>
                                                                    <br />
                                                                    <a href="javascript:void(0);" rel="${elementComponents.id}" class="btn btn-primary btn-xs btn-action deleteComponent" title="Delete Component">Delete Component</a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </c:if> 
                                            </div>
                                        </section>
                                    </c:forEach> 

                                </div>
                            </div>
                        </section>
                    </c:forEach>
                </form:form>
            </c:otherwise>
        </c:choose>

    </div>
</div>
<!-- New Segment Modal -->
<div class="modal fade" id="newSegmentModal" role="dialog" tabindex="-1" aria-labeledby="Add HL7 Segment" aria-hidden="true" aria-describedby="Add HL7 Segment"></div>

<!-- New Element Modal -->
<div class="modal fade" id="newSegmentElement" role="dialog" tabindex="-1" aria-labeledby="Add HL7 Segment Element" aria-hidden="true" aria-describedby="Add HL7 Segment Element"></div>