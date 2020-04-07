<%-- 
    Document   : frontEndLayout
    Created on : Nov 27, 2013, 8:51:51 AM
    Author     : chadmccue
--%>
<!DOCTYPE html>
<html class="" lang="en">
    <%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <jsp:text><![CDATA[<!--[if lte IE 7]>]]></jsp:text><html class="no-js lt-ie9 lt-ie8 lt-ie7"><jsp:text><![CDATA[<![endif]-->]]></jsp:text>
    <jsp:text><![CDATA[<!--[if IE 7]>]]></jsp:text><html class="no-js lt-ie9 lt-ie8"><jsp:text><![CDATA[<![endif]-->]]></jsp:text>
    <jsp:text><![CDATA[<!--[if IE 8]>]]></jsp:text><html class="no-js lt-ie9"><jsp:text><![CDATA[<![endif]-->]]></jsp:text>
    <jsp:text><![CDATA[<!--[if gt IE 8]>]]></jsp:text><html class="no-js"><jsp:text><![CDATA[<![endif]-->]]></jsp:text>
        <head>
            <meta charset="utf-8">	
            <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
            <meta name="description" content="<tiles:insertAttribute name="description" ignore="true" />">
                  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
            <meta http-equiv="Content-Language" content="en">
            <meta http-equiv="Accept-Language" content="en">
            <title><tiles:insertAttribute name="title" /></title>
            <!%-- main css compiled from main.less --%>
                <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/front-end/main.css?v=2">
	<link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/font-awesome.min.css">

        <!%-- Health-e-link theme --%>
        <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/front-end/themes/theme-health-e-link.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/datepicker.min.css">
        <jsp:text><![CDATA[<!--[if lte IE 9]>]]></jsp:text>
                <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/ie.css">
        <jsp:text><![CDATA[<![endif]-->]]></jsp:text>
            <!%-- moderizer: for ie8 compatibility --%>
            <script type="text/javascript" src="<%=request.getContextPath()%>/dspResources/js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
        <script data-main="<%=request.getContextPath()%>/dspResources/js/front-end/main" src="<%=request.getContextPath()%>/dspResources/js/vendor/require.js"></script>
       

    </head>
    <body id="<tiles:insertAttribute name='page-id' ignore='true' />" class="<tiles:insertAttribute name='page-section' ignore='true' /> " >
        <div class="wrap">
            <tiles:insertAttribute name="header" /> 
            <tiles:insertAttribute name="body" />
            <tiles:insertAttribute name="footer" />
        </div>

        <!-- media modal skeleton -->
        <div class="modal fade modal-media" id="mediaModal" role="dialog" tabindex="-1" aria-labeledby="" aria-hidden="true" aria-describedby="">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h3 class="panel-title"></h3>
                    </div>
                    <div class="modal-body">
                    </div>
                </div>
            </div>
        </div>
    </body>
    <tiles:importAttribute name="jscript" toName="script" ignore="true" />
    <c:if test="${not empty script}">
        <script type="text/javascript">
	    require(["<%=request.getContextPath()%><tiles:getAsString name='jscript' ignore='true' />"]);
        </script>
    </c:if>
    <tiles:importAttribute name="otherscript" toName="otherscript" ignore="true" />
    <c:if test="${not empty otherscript}">
        <script type="text/javascript">
	    require(["<%=request.getContextPath()%><tiles:getAsString name='otherscript' ignore='true' />"]);
        </script>
    </c:if>
</html>

