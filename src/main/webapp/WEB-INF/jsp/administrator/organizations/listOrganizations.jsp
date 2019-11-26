<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>

<div class="main clearfix full-width" role="main">
    <div class="col-md-12">
        <c:if test="${not empty savedStatus}" >
            <div class="alert alert-success" role="alert">
                <strong>Success!</strong> 
                <c:choose>
                    <c:when test="${savedStatus == 'updated'}">The organization has been successfully updated!</c:when>
                    <c:when test="${savedStatus == 'created'}">The organization has been successfully added!</c:when>
                    <c:when test="${savedStatus == 'deleted'}">The organization has been successfully removed!</c:when>
                </c:choose>
            </div>
        </c:if>

        <section class="panel panel-default">
	    <div class="panel-body">
		<div class="row-fluid contain basic-clearfix">
		    <div class="col-md-12">
		       <section class="panel panel-info container-fluid">
			    <div class="panel-heading">
				<h3 class="panel-title">Source Organizations</h3>
			    </div>
			   <div class="panel-body">
			       <div class="form-container scrollable">
				    <table class="table table-striped table-hover table-default" id="organization-table" style="cursor:pointer" >
					<thead>
					     <tr>
						<th scope="col" class="center-text">Id</th>
						<th scope="col">Organization Name ${result}</th>
						<th scope="col">Organization Type</th>
						<th scope="col">Contact Information</th>
						<th scope="col" class="center-text">Health-e-Link Registry</th>
						<th scope="col" class="center-text">Date Created</th>
						<th scope="col"></th>
					    </tr>
					</thead>
				    </table>
			       </div>
			   </div>
		       </section>
		    </div>
		</div>
	    </div> 
        </section>
    </div>
</div>