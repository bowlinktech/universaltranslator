<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog  modal-dialog-big">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${btnValue == 'Update'}">Update</c:when><c:when test="${btnValue == 'Create'}">Add</c:when></c:choose> Macro ${success}</h3>
	</div>
	<div class="modal-body">
	    <form:form id="macroform" commandName="Macros" modelAttribute="macroDetails"  method="post" role="form">
		<form:hidden path="id" id="id" />
		<div class="form-container">
		    <div class="row col-lg-12">
			<div class="col-lg-6">
			    <div class="form-group ${status.error ? 'has-error' : '' }">
				<label class="control-label" for="categoryId">Category</label>
				<form:select class="form-control" id="categoryId" path="categoryId">
				    <option value="1" <c:if test="${macroDetails.categoryId == 1}">selected</c:if>>Data Manipulation</option>
				    <option value="2" <c:if test="${macroDetails.categoryId == 2}">selected</c:if>>Processing</option> 
				</form:select>
				<form:errors path="categoryId" cssClass="control-label" element="label" />
			    </div>
			    <spring:bind path="macroShortName">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="macroShortName">Macro Short Name*</label>
				    <form:input path="macroShortName" id="macroShortName" class="form-control" type="text" maxLength="50" />
				    <form:errors path="macroShortName" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>
			    <spring:bind path="macroName">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="macroName">Macro Name*</label>
				    <form:input path="macroName" id="macroName" class="form-control" type="text" maxLength="100" />
				    <form:errors path="macroName" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>
			    <spring:bind path="formula">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="formula">Stored Procedure Name*</label>
				    <form:input path="formula" id="formula" class="form-control" type="text" maxLength="255" />
				    <form:errors path="formula" cssClass="control-label" element="label" />
				</div>
			    </spring:bind> 
			    <div class="form-group">
				<label for="status">Pre-Populate Field A with Source Field No.</label>
				<div>
				    <label class="radio-inline">
					<form:radiobutton id="populateFieldA" path="populateFieldA" value="true" /> Yes
				    </label>
				    <label class="radio-inline">
					<form:radiobutton id="populateFieldA" path="populateFieldA" value="false" /> No
				    </label>
				</div>         
				<br/> 
			    </div>     
			</div>
			<div class="col-lg-6">
			    <spring:bind path="fieldAQuestion">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="fieldAQuestion">Field A Question</label>
				    <form:input path="fieldAQuestion" id="fieldAQuestion" class="form-control" type="text" maxLength="255" />
				    <form:errors path="fieldAQuestion" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>
			    <spring:bind path="fieldBQuestion">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="fieldBQuestion">Field B Question</label>
				    <form:input path="fieldBQuestion" id="fieldBQuestion" class="form-control" type="text" maxLength="255" />
				    <form:errors path="fieldBQuestion" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>      
			    <spring:bind path="con1Question">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="con1Question">Constant 1 Question</label>
				    <form:input path="con1Question" id="con1Question" class="form-control" type="text" maxLength="255" />
				    <form:errors path="con1Question" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>
			    <spring:bind path="con2Question">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="con2Question">Constant 2 Question</label>
				    <form:input path="con2Question" id="con2Question" class="form-control" type="text" maxLength="255" />
				    <form:errors path="con2Question" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>  
			</div>
		    </div>
		    <div class="row col-lg-12">
			<div class="col-lg-12">
			     <spring:bind path="macroDesc">
				<div class="form-group ${status.error ? 'has-error' : '' }">
				    <label class="control-label" for="macroDesc">Macro Description</label>
				    <form:textarea path="macroDesc" id="macroDesc" class="form-control" rows="10" />
				    <form:errors path="macroDesc" cssClass="control-label" element="label" />
				</div>
			    </spring:bind>    
			</div>
		    </div>
		</div>
	    </form:form>
        </div>
	<div class="modal-footer">
	    <div class="form-group">
		<input type="button" id="submitButton" rel="${btnValue}" class="btn btn-primary" value="${btnValue}"/>
	    </div>
	</div>
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $("input:text,form").attr("autocomplete", "off");
    });
</script>
