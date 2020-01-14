<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-dialog modal-dialog-big">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 class="panel-title">Macro Definitions</h3>
	</div>
	<div class="modal-body" style="max-height:800px; overflow: auto">
	    <table class="table table-striped table-hover table-default">
		<thead>
		    <tr>
			<th scope="col">Macro Name</th>
			<th scope="col"">Macro Details</th>
		    </tr>
		</thead>
		<tbody>
		    <c:forEach items="${macros}" var="macro">
			<tr>
			    <td scope="row">${macro.macroName}</td>
			    <td>${macro.macroDesc}</td>
			    <td style=" vertical-align: middle">
				<a href="#!" class="btn btn-primary btn-xs useMacro"  data-dismiss="modal" rel="${macro.id}" title="Use This Macro">Use This Macro</a>
			    </td>
			</tr>
		    </c:forEach>
		</tbody>
	    </table>
	</div>
    </div>
</div>
