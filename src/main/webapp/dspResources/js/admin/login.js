/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function () {
    require(['jquery'], function ($) {

        if (document.cookie.indexOf('js=true') == -1) {
            document.cookie = 'js=true';
            window.location.reload(true);
        }

        $("input:text,form").attr("autocomplete", "off");

    });
});