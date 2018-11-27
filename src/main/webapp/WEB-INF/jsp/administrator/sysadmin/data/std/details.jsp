<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title"><c:choose><c:when test="${btnValue == 'Update'}">Update</c:when><c:when test="${btnValue == 'Create'}">Add</c:when></c:choose> Look Up Data for <span id="tableNameDisplay"></span>${success}</h3>
                </div>
                <div class="modal-body">
            <form:form id="${formId}" commandName="${objectType}" modelAttribute="tableDataDetails"  method="post" role="form">
                <form:hidden path="id" id="id" />
                <c:if test="${not empty stdForm}">
                    <form:hidden path="urlId" id="urlId"/>
                </c:if>
                <div class="form-container">
                    <div class="form-group">
                        <label for="status">Status *</label>
                        <div>
                            <label class="radio-inline">
                                <form:radiobutton id="status" path="status" value="true" /> Active
                            </label>
                            <label class="radio-inline">
                                <form:radiobutton id="status" path="status" value="false" /> Inactive
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="custom">Viewable *</label>
                        <div>
                            <label class="radio-inline">
                                <form:radiobutton id="custom" path="custom" value="false"/>Public
                            </label>
                            <label class="radio-inline">
                                <form:radiobutton id="custom" path="custom" value="true"/>Private 
                            </label>
                        </div>
                    </div>
                    <c:if test='${objectType=="lu_Tests"}'>
                        <spring:bind path="category">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="category">Category *</label>
                                <form:select id="category" path="category" cssClass="form-control half">
                                    <option value="" label=" - Select - " ></option>
                                    <form:options items="${categoryList}"/>
                                </form:select>
                                <form:errors path="category" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>


                    <c:if test='${objectType=="lu_ProcessStatus"}'>
                        <spring:bind path="category">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="category">Category *</label>
                                <form:select id="state" path="category" cssClass="form-control half">
                                    <option value="" label=" - Select - " ></option>
                                    <form:options items="${categoryList}"/>
                                </form:select>
                                <form:errors path="category" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="displayCode">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="displayCode">Display Code *</label>
                                <form:input path="displayCode" id="displayCode" class="form-control" type="text" maxLength="45" />
                                <form:errors path="displayCode" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>
                    <spring:bind path="displayText">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="displayText">Display Text *</label>
                            <form:input path="displayText" id="displayText" class="form-control" type="text" maxLength="45" />
                            <form:errors path="displayText" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <spring:bind path="description">
                        <div class="form-group ${status.error ? 'has-error' : '' }">
                            <label class="control-label" for="description">Description</label>
                            <form:input path="description" id="description" class="form-control" type="text" maxLength="255" />
                            <form:errors path="description" cssClass="control-label" element="label" />
                        </div>
                    </spring:bind>
                    <c:if test='${objectType=="lu_ProcessStatus"}'>
                        <spring:bind path="endUserDisplayCode">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="endUserDisplayCode">End User Display Code</label>
                                <form:input path="endUserDisplayCode" id="endUserDisplayCode" class="form-control" type="text" maxLength="45" />
                                <form:errors path="endUserDisplayCode" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="endUserDisplayText">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="displayText">End User Display Text</label>
                                <form:input path="endUserDisplayText" id="endUserDisplayText" class="form-control" type="text" maxLength="45" />
                                <form:errors path="endUserDisplayText" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="endUserDescription">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="endUserDescription">Description</label>
                                <form:input path="endUserDescription" id="endUserDescription" class="form-control" type="text" maxLength="255" />
                                <form:errors path="endUserDescription" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>
                    <c:if test='${objectType=="lu_Counties"}'>
                        <spring:bind path="state">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="state">State *</label>
                                <form:select id="state" path="state" cssClass="form-control half">
                                    <option value="" label=" - Select - " ></option>
                                    <form:options items="${stateList}"/>
                                </form:select>
                                <form:errors path="state" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>

                    </c:if>
                    <c:if test='${objectType=="lu_Tests"}'>
                        <div class="form-group">
                            <label for="vitalSign">Is this a vital sign?</label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="vitalSign" path="vitalSign" value="true"/>Yes
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="vitalSign" path="vitalSign" value="false"/>No
                                </label>
                            </div>
                        </div>
                        <spring:bind path="normalRange">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="normalRange">Please enter the acceptable range for this test.</label>
                                <form:input path="normalRange" id="normalRange" class="form-control" type="text" maxLength="45" />
                                <form:errors path="normalRange" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="normalRangeUnit">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="normalRangeUnit">Please enter the unit used for this test.</label>
                                <form:input path="normalRangeUnit" id="normalRangeUnit" class="form-control" type="text" maxLength="45" />
                                <form:errors path="normalRangeUnit" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>

                    <c:if test='${objectType=="lu_GeneralHealths" || objectType=="lu_Immunizations" || objectType=="lu_MedicalConditions" || objectType=="lu_Procedures" || objectType=="lu_Tests"}'>
                        <spring:bind path="codeValue">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="codeValue">Code Value</label>
                                <form:input path="codeValue" id="codeValue" class="form-control" type="text" maxLength="45" />
                                <form:errors path="codeValue" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="codeSystem">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="codeSystem">Code System</label>
                                <form:input path="codeSystem" id="codeSystem" class="form-control" type="text" maxLength="45" />
                                <form:errors path="codeSystem" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="codeVersion">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="codeVersion">Code Version</label>
                                <form:input path="codeVersion" id="codeVersion" class="form-control" type="text" maxLength="45" />
                                <form:errors path="codeVersion" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>
                    <c:if test='${objectType=="lu_GeneralHealthStatuses"}'>
                        <spring:bind path="category">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="category">Category</label>
                                <form:input path="category" id="category" class="form-control" type="text" maxLength="45" />
                                <form:errors path="category" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                    </c:if>
                    <c:if test='${objectType=="lu_Manufacturers"}'>
                        <%@ include file="lu_Manufacturers.jsp" %>
                    </c:if>
                    <c:if test='${objectType=="lu_Medications"}'>
                        <%@ include file="lu_Medications.jsp" %>
                    </c:if>

                    <div class="form-group">
                        <input type="button" id="submitButton" rel="${btnValue}" class="btn btn-primary" value="${submitBtnValue}"/>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        $("#tableNameDisplay").text($('#addLUDataModal').attr('rel'));
        $("input:text,form").attr("autocomplete", "off");
    });
</script>
