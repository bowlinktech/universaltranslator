/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

require(['./main'], function () {
    require(['jquery'], function ($) {

        //var searchTimeout;

        //Fade out the updated/created message after being displayed.
        if ($('.alert').length > 0) {
            $('.alert').delay(2000).fadeOut(1000);
        }

        $("input:text,form").attr("autocomplete", "off");

        $(document).on('click', '#orgRow', function () {
            window.location.href = $(this).attr('rel') + '/';
        });

        $('#searchOrgBtn').click(function () {
            $('#searchForm').submit();
        });
	
	var oSettings = datatable.fnSettings();
        
        datatable.fnSort( [ [5,'desc'] ] );


        /*$("#searchTerm").keyup(function(event) {
         var term = $(this).val();
         
         if(term.length >= 3 || term.length == 0) {
         if(searchTimeout) {clearTimeout(searchTimeout);}
         searchTimeout = setInterval("orglookup()",500);
         }
         });*/


        /*function orglookup() {
         if(searchTimeout) {clearTimeout(searchTimeout);}
         
         var term = $('#searchTerm').val().toLowerCase();
         
         if(term.length >= 3 || term.length == 0) {
         $('#searchForm').submit();
         }
         }*/
    });
});




