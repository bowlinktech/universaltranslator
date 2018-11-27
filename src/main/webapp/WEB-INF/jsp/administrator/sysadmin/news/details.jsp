<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="main clearfix" role="main">

    <div class="col-md-12">

        <form:form commandName="newsArticle" id="articleForm"  method="post" role="form" enctype="multipart/form-data">
            <input type="hidden" id="action" name="action" value="save" />
            <form:hidden path="id" id="articleId" />
            <form:hidden path="dateCreated" />

            <section class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Details</h3>
                </div>
                <div class="panel-body">
                    <div class="form-container">
                        <div class="form-group">
                            <label for="status">Status * </label>
                            <div>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="true"/>Active 
                                </label>
                                <label class="radio-inline">
                                    <form:radiobutton id="status" path="status" value="false"/>Inactive
                                </label>
                            </div>
                        </div>
                        <spring:bind path="title">
                            <div id="articleTitleDiv" class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="title">Title *</label>
                                <form:input path="title" id="title" class="form-control" type="text" maxLength="255" />
                                <form:errors path="title" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="shortDesc">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="shortDesc">Short Desc</label>
                                <form:textarea path="shortDesc" id="shortDesc" class="form-control" style="height: 150px" />
                                <form:errors path="shortDesc" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>
                        <spring:bind path="longDesc">
                            <div class="form-group ${status.error ? 'has-error' : '' }">
                                <label class="control-label" for="longDesc">Article Body *</label>
                                <form:textarea path="longDesc" id="longDesc" class="form-control" placeholder="Enter text ..." style="height: 500px" />
                                <form:errors path="longDesc" cssClass="control-label" element="label" />
                            </div>
                        </spring:bind>   
                    </div>
                </div>
            </section>
        </form:form>
    </div>
</div>



