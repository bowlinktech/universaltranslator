
require.config({
    urlArgs: "bust=" + (new Date()).getTime(),
    paths: {
        'jquery': '../vendor/jquery-2.0.0.min',
        'bootstrap': '../vendor/bootstrap',
        'mediaModal': '../mediaModal',
        'overlay': '../overlay',
        'sprintf': '../vendor/sprintf',
        'moment': '../vendor/moment',
        'daterangepicker': '../vendor/daterangepicker',
        'dataTables': '../vendor/datatables.min'
    },
    shim: {
        'bootstrap': ['jquery'],
        'daterangepicker': ['jquery', 'bootstrap'],
        'dataTables': ['jquery']
    },
    waitSeconds: 0
});

var datatable;

define(['jquery', 'moment', 'bootstrap', 'mediaModal', 'overlay', 'daterangepicker', 'dataTables'], function ($, moment) {
    
    $.ajaxSetup({
        cache: false
    });
    
    // tooltip demo
    $(document).tooltip({
        selector: "[data-toggle=tooltip]",
        container: "body"
    });

    // modify bootstrap modal to handle spacing for scroll bars more elegantly
    $(document).on('show.bs.modal', '.modal', function () {
        var windowHeight = $(window).height();
        var contentHeight = $('.wrap').outerHeight();

        // if the window is NOT scrollable, remove the class "modal-open".
        // This gets rid of the right margin added to the page.
        if (windowHeight >= contentHeight) {
            $(document.body).removeClass('modal-open');
        }
    });

    // initalize popovers
    $('[data-toggle=popover]').popover();

    // initalize tooltips
    $('.badge-help').tooltip();

    // Log out confirmation
    $('.logout').on('click', function (e) {
        var confirmed = confirm('Are you sure you want to log out?');
        if (confirmed) {
            return true;
        } else
        {
            e.preventDefault();
            return false;
        }
    });

    // overlay example:
    // show overlay:
    /*
     $('body').overlay({
     glyphicon : 'floppy-disk',
     message : 'Saving...'
     });
     */

    // hide overlay:
    // $('body').overlay('hide');

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
            function (start, end) {
                $('.daterange span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                $('.daterange span').attr('rel', start.format('MMM DD 00:00:00 YYYY'));
                $('.daterange span').attr('rel2', end.format('MMM DD 23:59:59 YYYY'));
                searchByDateRange();
            }
    );


    $.extend($.fn.dataTableExt.oStdClasses, {
        "sSortAsc": "tableheader headerSortDown",
        "sSortDesc": "tableheader headerSortUp",
        "sSortable": "tableheader"
    });

    /* API method to get paging information */
    $.fn.dataTableExt.oApi.fnPagingInfo = function (oSettings)
    {
        return {
            "iStart": oSettings._iDisplayStart,
            "iEnd": oSettings.fnDisplayEnd(),
            "iLength": oSettings._iDisplayLength,
            "iTotal": oSettings.fnRecordsTotal(),
            "iFilteredTotal": oSettings.fnRecordsDisplay(),
            "iPage": Math.ceil(oSettings._iDisplayStart / oSettings._iDisplayLength),
            "iTotalPages": Math.ceil(oSettings.fnRecordsDisplay() / oSettings._iDisplayLength)
        };
    }

    /* Bootstrap style pagination control */
    $.extend($.fn.dataTableExt.oPagination, {
        "bootstrap": {
            "fnInit": function (oSettings, nPaging, fnDraw) {
                var oLang = oSettings.oLanguage.oPaginate;
                var fnClickHandler = function (e) {
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
            "fnUpdate": function (oSettings, fnDraw) {
                var iListLength = 5;
                var oPaging = oSettings.oInstance.fnPagingInfo();
                var an = oSettings.aanFeatures.p;
                var i, j, sClass, iStart, iEnd, iHalf = Math.floor(iListLength / 2);

                if (oPaging.iTotalPages < iListLength) {
                    iStart = 1;
                    iEnd = oPaging.iTotalPages;
                } else if (oPaging.iPage <= iHalf) {
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
                                .bind('click', function (e) {
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
    
    /* Table initialisation */
    datatable = $('#dataTable').dataTable({
	responsive: true,
	"bStateSave": true,
        "iCookieDuration": 60,
        "sPaginationType": "bootstrap",
        "oLanguage": {
            "sSearch": "_INPUT_",
            "sLengthMenu": '<select class="form-control" style="width:150px">' +
                    '<option value="10">10 Records</option>' +
                    '<option value="20">20 Records</option>' +
                    '<option value="30">30 Records</option>' +
                    '<option value="40">40 Records</option>' +
                    '<option value="50">50 Records</option>' +
                    '<option value="-1">All</option>' +
                    '</select>'
        }
    });
    

});