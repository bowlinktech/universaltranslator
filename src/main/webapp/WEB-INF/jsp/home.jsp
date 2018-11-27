<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<a name='productSuite'></a>
<div class="product-suite">
    <div class="product-suite-header">
        <div class="container">
            <%--<a href="http://www.youtube.com/watch?v=DMJKG6jyHYg" data-title="Product Demo" data-toggle="modal" data-target="#mediaModal" class="pull-right product-demo font-large media-modal hidden-xs">
                <span class="icon-play"></span>
                Play Product Demo
            </a>--%>
            <h2>
                <span class="icon-health-e-link"></span>
                Product Suite
            </h2>
        </div>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-md-4 col-sm-4 product-summary">
                <a href="<c:url value='/product-suite/careConnector'/>" title="Care Connector" class="icon-product icon-product icon-health-e-net"></a>
                <h3><a href="<c:url value='/product-suite/careConnector'/>" title="Care Connector">Care Connector</a></h3>
                <p>Process referrals and feedback reports electronically with all partners in your care network.</p>
                <a href="<c:url value='/product-suite/careConnector'/>" title="Care Connector" class="btn-secondary">Learn More</a>
            </div>
            <div class="col-md-4 col-sm-4 product-summary">
                <a href="<c:url value='/product-suite/universal-hie'/>" title="Universal HIE" class="icon-product icon-health-e-data"></a>
                <h3><a href="<c:url value='/product-suite/universal-hie'/>" title="Universal HIE">Universal HIE</a></h3>
                <p>Connect healthcare provider EMR systems with partner systems and exchange patient data seamlessly.</p>
                <a href="<c:url value='/product-suite/universal-hie'/>" title="Universal HIE" class="btn-secondary">Learn More</a>
            </div>
            <div class="col-md-4 col-sm-4 product-summary">
                <a href="<c:url value='/product-suite/clinical-data-warehouse'/>" title="Clinical Data Warehouse" class="icon-product icon-health-e-web"></a>
                <h3><a href="<c:url value='/product-suite/clinical-data-warehouse'/>" title="Clinical Data Warehouse">Clinical Data Warehouse</a></h3>
                <p>Securely and effectively manage patient data across multiple programs of care.</p>
                <a href="<c:url value='/product-suite/clinical-data-warehouse'/>" title="Clinical Data Warehouse" class="btn-secondary">Learn More</a>
            </div>
        </div>
        <%--<a href="http://www.youtube.com/watch?v=DMJKG6jyHYg" data-title="Product Demo" data-toggle="modal" data-target="#mediaModal" class="product-demo center-text font-large media-modal visible-xs">
            <span class="icon-play"></span>
            Play Product Demo
        </a>--%>
    </div>
</div>

<%--<div class="container main-container" role="main">
    <div class="page-content">
        <div class="row">
            <div class="col-md-6">
                <h3>Safer, Quality Care Starts with Sharing Patient Information</h3>

                <p>Improving the safety and efficiency of healthcare delivery in your 
                    community starts with having access to comprehensive patient health records and current clinical information. Partners In the healthcare 
                    community including physicians, hospitals, community clinics and payers face their own challenges in producing accurate and timely health records. Data Exchange is no longer one of them.</p>

            </div>
            <div class="col-md-6">
                <h3> The Solution to Healthcare Information Exchange</h3>
                <p>
                    Introducing the Universal Translator! A suite of data exchange and software 
                    solutions that solve the technical challenges associated with sharing and accessing up-to-date, relevant clinical data.
                </p>
                <p>
                    The Universal Translator is a suite of data exchange software solutions built around a common platform that allows your healthcare 
                    provider community to easily manage, share, analyze and report data across virtual boundaries and platforms, and ultimately 
                    improve quality of care. Read More.
                 </p>
            </div>
        </div>
    </div>
</div>--%>

<c:if test="${not empty newsArticles}">
    <header class="news" role="banner">
        <%-- News Ticker --%>
        <div class="news-ticker" id="home-news">
            <div class="container">
                <div class="row">
                    <div class="col-md-1 news-ticker-header">
                        <h2>News</h2>
                        <a href="<c:url value='/news/articles'/>"  title="View all News Articles"><small>View All</small></a>
                    </div>
                    <div id="news-carousel" class="col-md-10 news-ticker-content news-carousel slide" data-ride="carousel">
                        <ul class="carousel-inner" >
                            <c:forEach var="article" varStatus="loop" items="${newsArticles}">
                                <li ${loop.index == 0 ? 'class="item active"' : 'class="item"'}>
                                    <h3>
                                        <a href="/news/article/${fn:toLowerCase(fn:replace(article.title,' ','-'))}" title="${article.title}">${article.title}</a>
                                    </h3>
                                    <c:if test="${not empty article.shortDesc}">
                                        <p>${article.shortDesc}  <span style="padding-left:10px"><a href="/news/article/${fn:toLowerCase(fn:replace(article.title,' ','-'))}" title="Read ${article.title}">Read More</a></span></p>
                                    </c:if>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </header>
</c:if>

