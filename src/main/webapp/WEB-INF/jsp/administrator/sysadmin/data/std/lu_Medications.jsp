<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<spring:bind path="form">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="form">Medication Form (e.g. pill,liquid)</label>
        <form:input path="form" id="form" class="form-control" type="text" maxLength="45" />
        <form:errors path="form" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="concentration">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="concentration">Concentration</label>
        <form:input path="concentration" id="concentration" class="form-control" type="text" maxLength="15" />
        <form:errors path="concentration" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="concentrationUnit">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="concentrationUnit">Concentration Unit (e.g. mg, oz)</label>
        <form:input path="concentrationUnit" id="concentration" class="form-control" type="text" maxLength="15" />
        <form:errors path="concentrationUnit" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="medSize">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="medSize">Medication Size (e.g. round, small)</label>
        <form:input path="medSize" id="medSize" class="form-control" type="text" maxLength="45" />
        <form:errors path="medSize" cssClass="control-label" element="label" />
    </div>
</spring:bind>
