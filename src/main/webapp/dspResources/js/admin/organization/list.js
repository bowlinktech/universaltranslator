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

        $(document).on('click', '.orgRow', function () {
            window.location.href = $(this).attr('rel') + '/';
        });

        $('#searchOrgBtn').click(function () {
            $('#searchForm').submit();
        });
	
	$(document).ready(function() {
	    
	    var table = $('#organization-table').DataTable({
		bServerSide: true,
		bProcessing: false, 
		deferRender: true,
		aaSorting: [[5,'desc']],
		sPaginationType: "bootstrap", 
		oLanguage: {
		   sSearch: "_INPUT_",
		   sLengthMenu: '<select class="form-control" style="width:150px">' +
			'<option value="10">10 Records</option>' +
			'<option value="20">20 Records</option>' +
			'<option value="30">30 Records</option>' +
			'<option value="40">40 Records</option>' +
			'<option value="50">50 Records</option>' +
			'<option value="-1">All</option>' +
			'</select>',
		    sProcessing: "<div style='background-color:#64A5D4; height:50px; margin-top:200px'><p style='color:white; padding-top:15px;' class='bolder'>Retrieving Results. Please wait...</p></div>"
		},
		sAjaxSource: "/administrator/organizations/ajax/getOrganizations",
		aoColumns: [
		    {
			"mData": "id", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "5%",
			"render": function ( data, type, row, meta ) {
			    return data;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    },
		    {
			"mData": "orgName", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "20%",
			"render": function ( data, type, row, meta ) {
			    return data;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    },
		    {
			"mData": "organizationType", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "20%",
			"render": function ( data, type, row, meta ) {
			   return data;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    },
		    {
			"mData": "address", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "20%",
			"render": function ( data, type, row, meta ) {
			    var contactInfo;
			    
			    if(data == '') {
				contactInfo = "N/A";
			    }
			    else {
				contactInfo = data;
			    
				if(row.address2 !== '') {
				    contactInfo = contactInfo + '<br />' + row.address2;
				}
				if(row.city !== '') {
				    contactInfo = contactInfo + '<br />' + row.city;
				}
				if(row.state !== '') {
				    contactInfo = contactInfo + '&nbsp;' + row.state;
				}
				if(row.postalCode !== '') {
				    contactInfo = contactInfo + '&nbsp;' + row.postalCode;
				}
			    }
			    return contactInfo;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    },
		    {
			"mData": "helRegistryId", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "10%",
			"className": "center-text",
			"render": function ( data, type, row, meta ) {
			    if(data > 0) {
				return "Yes (id: " + data + ")";
			    }
			    else {
				return "No";
			    }
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL);
			    $(td).addClass('orgRow');
			 }
		    },
		     {
			"mData": "dateCreated", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "15%",
			"className": "center-text",
			"render": function ( data, type, row, meta ) {
			    var dateC = new Date(row.dateCreated);
			    var myDateFormatted = ((dateC.getMonth()*1)+1)+'/'+dateC.getDate()+'/'+dateC.getFullYear();
			    return myDateFormatted;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    },
		    {
			"mData": "orgName", 
			"defaultContent": "",
			"bSortable":true,
			"sWidth": "10%",
			"className": "center-text",
			"render": function ( data, type, row, meta ) {
			    var editLink = '<a href="javascript:void(0);" class="btn btn-link" title="Edit this organization" role="button"><span class="glyphicon glyphicon-edit"></span> Edit</a>';
			    return editLink;
			},
			'createdCell':  function (td, cellData, rowData, row, col) {
			    $(td).attr('rel', rowData.cleanURL); 
			    $(td).addClass('orgRow');
			 }
		    }
		 ]
            });   
	    table.columns.adjust().draw();
	});






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




