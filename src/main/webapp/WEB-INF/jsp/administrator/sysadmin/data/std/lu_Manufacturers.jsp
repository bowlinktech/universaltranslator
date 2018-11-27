<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<spring:bind path="contactName">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="contactName">Contact Name</label>
        <form:input path="contactName" id="contactName" class="form-control" type="text" maxLength="45" />
        <form:errors path="contactName" cssClass="control-label"
                     element="label" />
    </div>
</spring:bind>

<spring:bind path="addressType">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="addressType">Address Type</label>
        <form:input path="addressType" id="addressType" class="form-control" type="text" maxLength="45" />
        <form:errors path="addressType" cssClass="control-label"
                     element="label" />
    </div>
</spring:bind>

<spring:bind path="line1">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="line1">Address Line 1</label>
        <form:input path="line1" id="line1" class="form-control" type="text" maxLength="45" />
        <form:errors path="line1" cssClass="control-label" element="label" />
    </div>
</spring:bind>

<spring:bind path="line2">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="line2">Line 2</label>
        <form:input path="line2" id="line2" class="form-control" type="text" maxLength="45" />
        <form:errors path="line2" cssClass="control-label" element="label" />
    </div>
</spring:bind>

<spring:bind path="city">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="city">City</label>
        <form:input path="city" id="city" class="form-control" type="text" maxLength="45" />
        <form:errors path="city" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="state">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="state">State</label>
        <form:select id="state" path="state" cssClass="form-control half">
            <option value="" label=" - Select - "></option>
            <form:options items="${stateList}" />
        </form:select>
        <form:errors path="state" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="zip">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="zip">Zip</label>
        <form:input path="zip" id="zip" class="form-control" type="text" maxLength="15" />
        <form:errors path="zip" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="country">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="country">Country</label>
        <form:input path="country" id="country" class="form-control" type="text" maxLength="25" />
        <form:errors path="country" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="phone1Type">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="phone1Type">Main Phone Type</label>
        <form:input path="phone1Type" id="phone1Type" class="form-control" type="text" maxLength="45" />
        <form:errors path="phone1Type" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="phone1">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="phone1">Main Phone</label>
        <form:input path="phone1" id="phone1" class="form-control" type="text" maxLength="45" />
        <form:errors path="phone1" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="phone2Type">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="phone2Type">Alt. Phone Type</label>
        <form:input path="phone2Type" id="phone2Type" class="form-control" type="text" maxLength="45" />
        <form:errors path="phone2Type" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="phone2">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="phone2">Alt. Phone Number</label>
        <form:input path="phone2" id="phone2" class="form-control" type="text" maxLength="45" />
        <form:errors path="phone2" cssClass="control-label" element="label" />
    </div>
</spring:bind>
<spring:bind path="fax">
    <div class="form-group ${status.error ? 'has-error' : '' }">
        <label class="control-label" for="fax">Fax</label>
        <form:input path="fax" id="fax" class="form-control" type="text" maxLength="45" />
        <form:errors path="fax" cssClass="control-label" element="label" />
    </div>
</spring:bind>

