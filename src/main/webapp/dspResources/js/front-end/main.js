require.config({
    urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        'jquery': '../vendor/jquery-1.12.2',
        'bootstrap': '../vendor/bootstrap',
        'responsive-tables': '../vendor/responsive-tables',
        'mediaModal': '../mediaModal',
        'overlay': '../overlay',
        'sprintf': '../vendor/sprintf',
        'fixed-header': '../fixed-header',
        'moment': '../vendor/moment',
        'daterangepicker': '../vendor/daterangepicker',
        'dataTables': '../vendor/jquery.dataTables.min',
        'maps': '../vendor/mapmarker.jquery',
        'ajaxupload': 'ajaxupload.min',
        'printElement' : 'jquery.printElement.min'
    },
    shim: {
        'bootstrap': ['jquery'],
        'responsive-tables': ['jquery'],
        'daterangepicker': ['jquery', 'bootstrap'],
        'dataTables': ['jquery'],
        'maps': ['jquery'],
        'ajaxupload' : ['jquery'],
        'printElement': ['jquery']
    }
});

define(['jquery', 'fixed-header', 'moment', 'bootstrap', 'responsive-tables', 'mediaModal', 'overlay', 'daterangepicker', 'dataTables', 'ajaxupload', 'printElement'], function($, fixedHeader, moment) {

    var primaryNav = $('.primary-nav');
    var primaryNavHeight = primaryNav.outerHeight();

    $.ajaxSetup({
        cache: false
    });

    // left nav fixed nav bar
    $('.fixed-region').affix({
        offset: {
            top: function() {
                return $('.main-container').position().top + primaryNavHeight;
            },
            bottom: function() {
                // calculate how far down the content section goes down the page
                var bottomNum = $('.wrap').outerHeight() - ($('.main-container').position().top + $('.main-container').outerHeight());
                return bottomNum;
            }
        }
    });

    // tooltip demo
    $(document).tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });

    // popover demo
    $(document).popover({
        selector: "[data-toggle=popover]",
        container: "body"
    });

    // overwrite scrollspy to get rid of activating parent list items
    $.fn.scrollspy.Constructor.prototype.activate = function(target) {
        this.activeTarget = target;

        $(this.selector)
                .parents('.active')
                .removeClass('active');

        var selector = this.selector + '[data-target="' + target + '"],' + this.selector + '[href="' + target + '"]';

        var active = $(selector)
                .parent() //EDIT//
                .addClass('active');

        if (active.parent('.dropdown-menu').length) {
            active = active
                    .closest('li.dropdown')
                    .addClass('active');
        }

        active.trigger('activate');
    };

    $('.date-range-picker-trigger').daterangepicker(
            {
                ranges: {
                    'See All': [$('#fromDate').attr('rel2'), moment()],
                    'Today': [moment(), moment()],
                    'Yesterday': [moment().subtract('days', 1), moment().subtract('days', 1)],
                    'Last 7 Days': [moment().subtract('days', 6), moment()],
                    'Last 30 Days': [moment().subtract('days', 29), moment()],
                    'This Month': [moment().startOf('month'), moment().endOf('month')],
                    'Last Month': [moment().subtract('month', 1).startOf('month'), moment().subtract('month', 1).endOf('month')]
                },
                startDate: $('#fromDate').attr('rel'),
                endDate: $('#toDate').attr('rel')
            },
    function(start, end) {
        $('.daterange span').html(start.format('MMM DD, YYYY') + ' - ' + end.format('MMM DD, YYYY'));
        $('.daterange span').attr('rel', start.format('MMM DD 00:00:00 YYYY'));
        $('.daterange span').attr('rel2', end.format('MMM DD 23:59:59 YYYY'));
        searchByDateRange();
    }
    );


    // left nav scrollspy
    $('body').scrollspy({target: '#active-page-nav'});

    // initialized fixed header
    fixedHeader.init();
    

    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });

    /* API method to get paging information */
    $.fn.dataTableExt.oApi.fnPagingInfo = function(oSettings)
    {
        return {
            "iStart":oSettings._iDisplayStart,
            "iEnd":oSettings.fnDisplayEnd(),
            "iLength":oSettings._iDisplayLength,
            "iTotal":oSettings.fnRecordsTotal(),
            "iFilteredTotal": oSettings.fnRecordsDisplay(),
            "iPage":Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
            "iTotalPages":Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
        };
    }

    /* Bootstrap style pagination control */
    $.extend($.fn.dataTableExt.oPagination, {
        "bootstrap": {
            "fnInit": function(oSettings, nPaging, fnDraw) {
                var oLang = oSettings.oLanguage.oPaginate;
                var fnClickHandler = function(e) {
                    e.preventDefault();
                    if (oSettings.oApi._fnPageChange(oSettings, e.data.action)) {
                        fnDraw(oSettings);
                    }
                };

                $(nPaging).append(
                        '<ul class="pagination pull-right">' +
                        '<li class="prev disabled"><a href="#">&laquo;</a></li>' +
                        '<li class="next disabled"><a href="#">&raquo;</a></li>' +
                        '</ul>'
                        );
                var els = $('a', nPaging);
                $(els[0]).bind('click.DT', {action: "previous"}, fnClickHandler);
                $(els[1]).bind('click.DT', {action: "next"}, fnClickHandler);
            },
            "fnUpdate": function(oSettings, fnDraw) {
                var iListLength = 5;
                var oPaging = oSettings.oInstance.fnPagingInfo();
                var an = oSettings.aanFeatures.p;
                var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

                if (oPaging.iTotalPages < iListLength) {
                    iStart = 1;
                    iEnd = oPaging.iTotalPages;
                }
                else if (oPaging.iPage <= iHalf) {
                    iStart = 1;
                    iEnd = iListLength;
                } else if (oPaging.iPage >= (oPaging.iTotalPages - iHalf)) {
                    iStart = oPaging.iTotalPages - iListLength + 1;
                    iEnd = oPaging.iTotalPages;
                } else {
                    iStart = oPaging.iPage - iHalf + 1;
                    iEnd = iStart + iListLength - 1;
                }

                for (i = 0, iLen = an.length; i < iLen; i++) {
                    // Remove the middle elements
                    $('li:gt(0)', an[i]).filter(':not(:last)').remove();

                    // Add the new list items and their event handlers
                    for (j = iStart; j <= iEnd; j++) {
                        sClass = (j == oPaging.iPage + 1) ? 'class="active"' : '';
                        $('<li ' + sClass + '><a href="#">' + j + '</a></li>')
                                .insertBefore($('li:last', an[i])[0])
                                .bind('click', function(e) {
                                    e.preventDefault();
                                    oSettings._iDisplayStart = (parseInt($('a', this).text(), 10) - 1) * oPaging.iLength;
                                    fnDraw(oSettings);
                                });
                    }

                    // Add / remove disabled classes from the static elements
                    if (oPaging.iPage === 0) {
                        $('li:first', an[i]).addClass('disabled');
                    } else {
                        $('li:first', an[i]).removeClass('disabled');
                    }

                    if (oPaging.iPage === oPaging.iTotalPages - 1 || oPaging.iTotalPages === 0) {
                        $('li:last', an[i]).addClass('disabled');
                    } else {
                        $('li:last', an[i]).removeClass('disabled');
                    }
                }
            }
        }
    });

});