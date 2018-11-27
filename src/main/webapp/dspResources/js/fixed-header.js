define(['jquery'], function ($) {

    var actionsNav = $('.actions-nav');
    var primaryNav = $('.primary-nav');
    var primaryNavHeight = primaryNav.outerHeight();
    var scrollPosition = $(this).scrollTop();
    var isFixed = false;

    // Fixed Headers
    // TODO[Implement] : Create jQuery plugin.

    if (actionsNav.length)
    {
        var actionsNavPosition = actionsNav.offset().top;
    }


    var fixedHeader = {

        updateHeaderPosition: function () {

            primaryNavHeight = primaryNav.outerHeight();

            scrollPosition = $(this).scrollTop();

            // if scroll top is greater than the header height, fix the position of primary nav and actions nav
            if (scrollPosition > primaryNavHeight) {

                if ($('.placeholder').length === 0)
                {
                    $('.header-inner').prepend('<div class="placeholder" style="height:' + primaryNavHeight + 'px; position:relative; width:100%"></div>');
                } else
                {
                    $('.placeholder').height(primaryNavHeight).show();
                }

                primaryNav.addClass('fixed');

                isFixed = true;
            } else
            {
                isFixed = false;
            }

            if (scrollPosition === 0)
            {
                fixedHeader.resetPrimaryNavPosition();

                if (actionsNav.hasClass('fixed'))
                {
                    fixedHeader.resetActionNavPosition();
                }
            }

            if (actionsNav.length && $(window).width() > 767)
            {
                // position actions nav;
                if (scrollPosition > (actionsNavPosition - primaryNav.outerHeight())) {
                    actionsNav.css('top', primaryNavHeight).addClass('fixed');
                }

                if ((scrollPosition <= (actionsNavPosition - primaryNav.outerHeight())) && isFixed)
                {
                    fixedHeader.resetActionNavPosition();
                }
            }
        },

        resetPrimaryNavPosition: function () {
            if ($('.placeholder').length)
            {
                $('.placeholder').hide();
            }
            primaryNav.removeClass('fixed');
        },

        resetActionNavPosition: function () {
            actionsNav.removeClass('fixed').css('top', '');
        },

        allowFixedHeader: function () {
            if ($(window).width() >= 768)
            {
                return true;
            }
            return false;
        },

        init: function () {
            var _self = this;
            this.updateHeaderPosition();
            $(window).on('scroll', _self.updateHeaderPosition);
        }
    };

    return fixedHeader;

});