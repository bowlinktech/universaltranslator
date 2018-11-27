<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date" />

<div class="modal fade" id="settingsModal" role="dialog" tabindex="-1" aria-labeledby="Settings" aria-hidden="true" aria-describedby="Settings"></div>

<footer class="footer">
    <div class="container">
        <nav>
            <ul class="nav-inline">
                <li><a href="<c:url value='/'/>" title="Home">Home</a></li>
                <li><a href="<c:url value='/about'/>" title="About">About</a></li>
                <li><a href="<c:url value='/contact'/>" title="Contact">Contact</a></li>
                <c:if test="${not empty pageContext.request.userPrincipal.name}"><li><a href="<c:url value='/profile'/>" title="My Account">My Account</a></li></c:if>
                <c:if test="${not empty pageContext.request.userPrincipal.name}"><li><a href="#settingsModal" id="settings" data-toggle="modal" title="Account Settings" class="settings">Account Settings</a></li></c:if>
            </ul>
        </nav>

        <p class="vcard">
            <span class="fn">Massachusetts Department of Public Health</span> |
            <span class="adr"><span class="post-office-box">250 Washington Street</span>,
                <span class="region">Boston, MA</span>
                <span class="postal-code">02108</span></span> |
            Phone: <span class="tel">(617) 994-9869</span> |
        </p>
        <p>
            Copyright <fmt:formatDate value="${date}" pattern="yyyy" /> Massachusetts Department of Public Health All rights reserved</p>
        </p>
    </div>
</footer>