/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


require(['./main'], function() {
    require(['jquery', 'maps'], function($) {
        
        $.ajax({
            url: 'getPartnerMapEntries.do',
            type: 'GET',
            success: function(data) {
                //set up map options
                $("#map").mapmarker({
                zoom : 9,
                center : 'Franklin, MA',
                markers : data
                });
            },
            error: function(error) {
                console.log(error);
            }
        });
        
    });
});   


