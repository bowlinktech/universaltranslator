<%-- 
    Document   : macroDetails
    Created on : Nov 26, 2013, 11:21:11 AM
    Author     : chadmccue
--%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Macro Details</h3>
        </div>
        <div class="modal-body">
            <c:if test="${not empty fieldA_Question}">
                <div id="fieldADiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="fieldAQuestion">${fieldA_Question}</label>
                    <c:choose>
                        <c:when test="${fieldAContainsCW == true && not empty crosswalks}">
                            <input type="hidden" id="fieldAQuestionSelect" value="" />
                            <select class="form-control" id="fieldAQuestion">
                                <option value="">-Select Crosswalk -</option>
                                <c:forEach items="${crosswalks}" var="cw">
                                    <option value="${cw.id}">${cw.name}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                           <input type="text" id="fieldAQuestion" class="form-control" type="text" maxLength="255" />
                        </c:otherwise>
                    </c:choose>
                    <span id="fieldAMsg" class="control-label"></span>
                </div>
                <c:if test="${populateFieldA}">
                    <script>
                        populateFieldA();
                    </script>
                </c:if>
            </c:if>
            <c:if test="${not empty fieldB_Question}">
                <div id="fieldBDiv" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="fieldBQuestion">${fieldB_Question}</label>
                    <c:choose>
                        <c:when test="${fieldBContainsCW == true && not empty crosswalks}">
                            <input type="hidden" id="fieldBQuestionSelect" value="" />
                            <select class="form-control" id="fieldBQuestion">
                                <option value="">-Select Crosswalk -</option>
                                <c:forEach items="${crosswalks}" var="cw">
                                    <option value="${cw.id}">${cw.name}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                           <input type="text" id="fieldBQuestion" class="form-control" type="text" maxLength="255" />
                        </c:otherwise>
                    </c:choose>
                    <span id="fieldBMsg" class="control-label"></span>
                </div>
            </c:if>
            <c:if test="${not empty Con1_Question}">
                <div id="Con1Div" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="Con1Question">${Con1_Question}</label>
                    <c:choose>
                        <c:when test="${con1ContainsCW == true && not empty crosswalks}">
                            <input type="hidden" id="Con1QuestionSelect" value="" />
                            <select class="form-control" id="Con1Question">
                                <option value="">-Select Crosswalk -</option>
                                <c:forEach items="${crosswalks}" var="cw">
                                    <option value="${cw.id}">${cw.name}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                            <input type="text" id="Con1Question" class="form-control" type="text" maxLength="255" />
                        </c:otherwise>
                    </c:choose>
                    <span id="Con1Msg" class="control-label"></span>
                </div>
            </c:if>
            <c:if test="${not empty Con2_Question}">
                <div id="Con2Div" class="form-group ${status.error ? 'has-error' : '' }">
                    <label class="control-label" for="Con2Question">${Con2_Question}</label>
                    <c:choose>
                        <c:when test="${con2ContainsCW == true && not empty crosswalks}">
                            <input type="hidden" id="Con2QuestionSelect" value="" />
                            <select class="form-control" id="Con2Question">
                                <option value="">-Select Crosswalk -</option>
                                <c:forEach items="${crosswalks}" var="cw">
                                    <option value="${cw.id}">${cw.name}</option>
                                </c:forEach>
                            </select>
                        </c:when>
                        <c:otherwise>
                           <input type="text" id="Con2Question" class="form-control" type="text" maxLength="255" />
                        </c:otherwise>
                    </c:choose>
                    <span id="Con2Msg" class="control-label"></span>
                </div>
            </c:if>
            <div class="form-group">
                <input type="button" class="btn btn-primary submitMacroDetailsButton" value="Save"/>
            </div>
        </div>
    </div>
</div>
