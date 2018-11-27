define(['jquery', 'bootstrap'], function ($) {

    // save the original function object
    var _super = $.fn.modal.Constructor;

    // add custom defaults
    // $.extend( _super.DEFAULTS, {
    // });

    // create a new constructor
    var Modal = function (element, options) {

        //get the things we need to setup the modal
        this.fileUrl = options.remote;

        if (this.fileUrl)
        {
            // http://stackoverflow.com/questions/12447099/validate-youtube-url-using-jquery-javascript
            var youtubePattern = /^(?:https?:\/\/)?(?:www\.)?youtube\.com\/watch\?(?=.*v=((\w|-){11}))(?:\S+)?$/;
            var youtubeVideoId = (this.fileUrl.match(youtubePattern)) ? RegExp.$1 : false;

            if (youtubeVideoId)
            {
                this.modalType = 'video';
                this.videoType = 'youtube';
                this.videoId = youtubeVideoId;
                options.remote = false;
            } else
            {
                var acceptableImageExtensions = ['jpg', 'png', 'gif'];
                var acceptableFileExtensions = ['pdf'];

                // check the file extension. if it'd an image or a PDF, we ned to set things up diffenerntly
                this.fileExtension = this.fileUrl.split('.').pop();

                if ($.inArray(this.fileExtension, acceptableImageExtensions) >= 0)
                {
                    this.modalType = 'image';
                    // set remote to false to avoid the default behavior
                    options.remote = false;
                } else if ($.inArray(this.fileExtension, acceptableFileExtensions) >= 0)
                {
                    this.modalType = 'file';
                    options.remote = false;
                }
            }

        }

        // call the original constructor
        _super.apply(this, arguments);
    };

    // extend prototypes and add a super function
    Modal.prototype = $.extend({}, _super.prototype, {
        constructor: Modal,

        _super: function () {
            var args = $.makeArray(arguments);
            _super.prototype[args.shift()].apply(this, args);
        },

        sizeModal: function (modal, mediaWidth, mediaHeight) {
            var windowWidth = $(window).width();
            var windowHeight = $(window).height();
            var imageProportion = mediaWidth / mediaHeight;
            var modalMedia;

            // TODO [Implement] Make the 150 Dynamic
            // 150 = 
            // modal-dialog padding-top + padding-bottom;
            // modal-body padding-top + padding-bototm;
            // modal header outerHeight()
            var maxImageHeight = windowHeight - 150;

            // TODO [Implement] Make the 60 Dynamic
            // 60 = 
            // modal-dialog padding-left + padding-right;
            // modal-body padding-left + padding-right;
            var maxImageWidth = windowWidth - 60;

            if (this.modalType === 'image') {
                modalMedia = $('.modal-image', modal);

                if (mediaWidth > maxImageWidth) {
                    modalMedia.width(maxImageWidth);
                    //modalMedia.height(maxImageWidth * imageProportion);
                } else if (mediaHeight > maxImageHeight) {
                    modalMedia.height(maxImageHeight);
                    modalMedia.width(maxImageHeight * imageProportion);
                } else {
                    modalMedia.width(mediaWidth);
                    modalMedia.height(mediaHeight);
                }

            } else if (this.modalType === 'video') {
                modalMedia = $('.modal-video', modal);

                // TODO [Implement]: Modify the size of the video similar to images
                modalMedia.width(mediaWidth);
                modalMedia.height(mediaHeight);
            }

            $('.modal-body', modal).width(modalMedia.width());
            $('.modal-dialog', modal).width($('.modal-body', modal).outerWidth());
        },

        resetMediaModal: function (target) {
            $('.modal-body', target).html('').css({'width': '', 'height': ''});
            $('.modal-dialog', target).css('width', '');
            $(target).removeClass('pdf-modal').removeClass('image-modal');
            target.removeData();
            target.off('shown.bs.modal');
        },

        show: function () {
            if (typeof this.modalType !== 'undefined') {

                var _self = this;
                var target = $(this.options.target);
                var iframeHtml = '';

                $('.modal-header h3', target).text(this.options.title);

                // if it's an acceptable image, make the url the src for modal-image
                if (this.modalType === 'image') {
                    var activeImage = new Image();
                    activeImage.src = this.fileUrl;
                    $(activeImage).addClass('modal-image');

                    activeImage.onload = function () {
                        $('.modal-body', target).get(0).appendChild(activeImage);
                        _self.sizeModal(target, activeImage.width, activeImage.height);
                        _self._super('show');
                    };
                } else if (this.modalType === 'file') {
                    if (this.fileExtension === 'pdf') {
                        iframeHtml = '<iframe src="" class="modal-iframe" height="100%" width="100%"></iframe>';
                        target.addClass('pdf-modal');
                        _self._super('show');

                        target.on('shown.bs.modal', function () {
                            var modalBodyHeight = $('.modal-content', target).outerHeight() - $('.modal-header', target).outerHeight();
                            $('.modal-body', target).outerHeight(modalBodyHeight).html(iframeHtml);
                            $('.modal-iframe', target).attr('src', _self.fileUrl);
                        });
                    }
                } else if (this.modalType === 'video') {

                    // TODO [Implement]: Use the youtube API to create the iframe so it can be cotrolled via the api
                    if (_self.videoType === 'youtube') {
                        iframeHtml = '<iframe width="560" height="315" class="modal-video" src="" frameborder="0" allowfullscreen></iframe>';
                    }

                    target.addClass('video-modal');
                    $('.modal-body', target).html(iframeHtml);
                    _self.sizeModal(target, 560, 315);
                    _self._super('show');

                    target.on('shown.bs.modal', function () {
                        $('.modal-video', target).attr('src', '//www.youtube.com/embed/' + _self.videoId);
                    });
                }

                target.on('hidden.bs.modal', function () {
                    _self.resetMediaModal(target);
                });
            } else {
                this._super('show');
            }
        }
    });

    $.fn.modal = function (option, _relatedTarget) {
        return this.each(function () {
            var $this = $(this);
            var data = $this.data('bs.modal');
            var options = $.extend({}, _super.DEFAULTS, $this.data(), typeof option === 'object' && option);

            if (!data) {
                $this.data('bs.modal', (data = new Modal(this, options)));
            }

            if (typeof option === 'string') {
                data[option](_relatedTarget);
            } else if (options.show) {
                data.show(_relatedTarget);
            }
        });
    };
});