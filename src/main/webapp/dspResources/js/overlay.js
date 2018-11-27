define(['jquery', 'sprintf'], function ($) {

    // create a new constructor
    var Overlay = function (element, options) {
        this.options = options;
        this.$element = $(element);
        this.isShown = null;
    };

    Overlay.DEFAULTS = {
        message: 'Loading',
        overlayClass: 'overlay',
        overlayId: 'overlay-' + Math.floor((Math.random() * 100) + 1),
        classVal: '',
        glyphicon: '',
        show: true,
        template: '<div class="%s" id="%s"><div class="overlay-message"><div class="overlay-icon glyphicon glyphicon-%s"></div><span class="ovelay-text">%s</span></div></div>',
    };

    Overlay.prototype = {

        show: function () {

            if (!this.isShown)
            {
                // build the HTML
                this.options.template = sprintf(this.options.template, this.options.overlayClass, this.options.overlayId, this.options.glyphicon, this.options.message);

                // append to the container
                this.$element.append(this.options.template);

                // set the active jQuery Object
                var activeOverlay = $('#' + this.options.overlayId);

                // hide the icon container if there is none
                if (!this.options.glyphicon) {
                    activeOverlay.find('.glyphicon').css('display', 'none');
                }

                // show the overlay
                activeOverlay.show();

                this.isShown = true;
            }
        },

        hide: function () {
            $('#' + this.options.overlayId).hide().remove();
        }

    };

    $.fn.overlay = function (option) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('ut.overlay');
            var options = $.extend({}, Overlay.DEFAULTS, $this.data(), typeof option === 'object' && option);

            if (!data) {
                $this.data('ut.overlay', (data = new Overlay(this, options)));
            }

            if (typeof option === 'string') {
                data[option]();
            } else if (options.show) {
                data.show();
            }

        });
    };

    $.fn.overlay.Constructor = Overlay

});