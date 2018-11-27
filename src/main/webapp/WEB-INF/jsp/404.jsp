<%@ page session="false" %>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

        <!-- main css compiled from main.less -->
        <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/admin/main.css?v=2">

        <!-- default theme
        <link rel="stylesheet" href="css/themes/theme-default.css">
        -->

        <!-- Health-e-link theme -->
        <link rel="stylesheet" href="<%=request.getContextPath()%>/dspResources/css/admin/themes/theme-health-e-link.css">


        <!--[if lt IE 9]>
                <link rel="stylesheet" href="css/ie.css">
        <![endif]-->

        <script type="text/javascript" src="js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
        <script data-main="js/front-end/main" src="js/vendor/require.js"></script>
    </head>
    <body class="theme-default" id="notFound" >
        <!--[if lt IE 7]>
            <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
        <![endif]-->

        <div class="wrap">
            <div class="login-container notFound">
                <div class="login">
                    <header class="login-header">
                        <div class="login-header-content"><span class="logo ir" alt="{Company Name Logo}">Company Name</span></div>
                    </header>
                    <h1>404 Page Not Found</h1>
                    <p>Sorry, the page you have requested was not found.</p>
                </div>
            </div>
        </div>

        <script>
            var backBtn = document.getElementById('back-btn');
            backBtn.addEventListener('click', function () {
                window.history.back();
            }, true)
        </script>

        <script data-main="js/main" src="js/vendor/require.js"></script>
    </body>
</html>