console.log("This is Script File");

 
	
	
	
	

$(document).ready(function () {
	
	
	
			
    $('#search-button').on('click', function() {
        var query = $('#search-input').val();
        
        console.log(" >>>>>>>>>>>>>>>> : ", query);
        
        $.ajax({
            url: '/search',
            type: 'GET',
            data: {
                query: query
            },
            success: function(response) {
                if (response.redirectUrl) {
                    window.location.href = response.redirectUrl;
                } else {
                    // Handle the response and display results
                    $('#search-results').html(JSON.stringify(response.results));
                }
            },
            error: function(error) {
                console.error('Error:', error);
            }
        });
    });
	

	
    function adjustMargin() {
        var container = document.getElementById('responsiveContainer');
        if (window.innerWidth <= 768) {
            container.style.marginLeft = '-18px';
        } else {
            container.style.marginLeft = '170px';
        }
    }

    // Move the "Show entries" dropdown to the left end
    $('.dataTables_length').appendTo('.dataTables_wrapper .dataTables_filter');

    window.addEventListener('resize', adjustMargin);
    window.addEventListener('load', adjustMargin);



       
});

  // Toggle the sidebar
  $('#sidebarToggleTop').on('click', function() {
    $('body').toggleClass('sidebar-toggled');
    $('.sidebar').toggleClass('toggled');
    if ($('.sidebar').hasClass('toggled')) {
        $('.sidebar .collapse').collapse('hide');
    }
});
    
  
  
  /*function toggleSubMenu(link) {
    // Close all submenus first
    var subLinks = document.querySelectorAll(".sub-links");
    for (var i = 0; i < subLinks.length; i++) {
        subLinks[i].style.display = "none";
    }

    // Remove the "sub-links-open" class from all sub-links
    var subLinksArray = Array.from(document.querySelectorAll(".sub-link"));
    subLinksArray.forEach(function (subLink) {
        subLink.classList.remove("sub-links-open");
    });

    // Open the clicked submenu
    var submenu = link.nextElementSibling; // Find the next sibling, which is the submenu
    if (submenu.style.display === "block") {
        submenu.style.display = "none";
    } else {
        submenu.style.display = "block";
        link.classList.add("sub-links-open"); // Add the class when submenu is open
    }
}*/

function toggleSubMenu(link) {
    var submenu = link.nextElementSibling; // Find the next sibling, which is the submenu
    var isOpen = submenu.style.display === "block";

    // Close all submenus first
    var subLinks = document.querySelectorAll(".sub-links");
    for (var i = 0; i < subLinks.length; i++) {
        subLinks[i].style.display = "none";
    }

    // Remove the "sub-links-open" class from all sub-links
    var subLinksArray = Array.from(document.querySelectorAll(".sub-link"));
    subLinksArray.forEach(function (subLink) {
        subLink.classList.remove("sub-links-open");
    });

    // If the clicked submenu is not open, open it
    if (!isOpen) {
        submenu.style.display = "block";
        link.classList.add("sub-links-open"); // Add the class when submenu is open
    }
}

  
    
var submenuToggles = document.querySelectorAll(".submenu-toggle");

submenuToggles.forEach(function(submenuToggle) {
    var submenu = submenuToggle.nextElementSibling;
    var arrowIcon = submenuToggle.querySelector(".fas");

    submenuToggle.addEventListener("click", function(event) {
        event.preventDefault(); // Prevent the default behavior of the link
        
        if (submenu.style.maxHeight === "0px") {
            closeAllSubmenus();
            submenu.style.maxHeight = submenu.scrollHeight + "px";
            arrowIcon.style.transform = "rotate(180deg)";
        } else {
            submenu.style.maxHeight = "0px";
            arrowIcon.style.transform = "rotate(0deg)";
        }
    });
});


document.addEventListener("click", function(event) {
    if (!event.target.closest(".submenu-toggle")) {
        closeAllSubmenus();
    }
});

function closeAllSubmenus() {
    var submenus = document.querySelectorAll(".nav-second-level.submenu");
    var arrowIcons = document.querySelectorAll(".submenu-toggle .fas");
    
    submenus.forEach(function(submenu) {
        submenu.style.maxHeight = "0px";
    });
    
    arrowIcons.forEach(function(arrowIcon) {
        arrowIcon.style.transform = "rotate(0deg)";
    });
}


function togglePromotionPanel() {
		  $(document).on("click", ".promotion-link", function(e) {
		    e.preventDefault();
		    var panel = $(this).data("panel");
		    var $panel = $("#" + panel);
		    var $arrow = $(this).find(".arrow");

		    // Check if panel is already visible
		    if ($panel.is(":visible")) {
		      $panel.slideUp();
		      $arrow.removeClass("open");
		    } else {
		      // Hide other panels
		      $(".promotion-panel").not($panel).slideUp();
		      $(".arrow").not($arrow).removeClass("open");

		      // Show current panel
		      $panel.slideDown();
		      $arrow.addClass("open");
		    }
		  });
		}


/* --------------------------------- table Init ----------------------------------------------- */
	
	
	function readTableData(tableId){
		    var row = [];

		    $('tr.selected td').each(function(index,td){
		        var tdVals = _com_parseTableCell(this);

		        for(var i = 0; i < tdVals.length ; i++)
		            row.push(tdVals[i]);
		    });

		    return row;
		}

		function _com_parseTableCell(td){
		    var vals = [];

		    if($(td).find('a').length > 0){
		        vals.push($(td).find('a').text());
		    }else if($(td).find('input').length> 0){
		        $(td).find(':input').each(function(){
		            vals.push($(this).val());
		        });
		    }else{
		        vals.push($(td).text());
		    }
		    
		    return vals;
		}
		
		
        function tableInit(name,columns,order,columnDefs,select,url){
			
			tableInit(name,columns,order,columnDefs,select,url,isHorizentalScroll,false); 

			
		}

        function tableInit(name, columns, order, columnDefs, select, url, isInitScrollX) {
            var elID = '#' + name;
            var responsive = !isInitScrollX;

            var pageTable = $(elID).DataTable({
                scrollX: isInitScrollX,
                responsive: responsive,
                processing: true,
                serverSide: true,
                bLengthChange : true,
                language: {
                    processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Loading...</span>'
                },
                columnDefs: columnDefs,
                select: select,
                columns: columns,
                order: order,
                ajax: {
                    url: url,
                    data: function (d) {
                        var pageNumber = parseInt(d.start / d.length);
                        d.page = pageNumber;
                        d.size = d.length;
                        if (d.order) {
                            var order = d.order[0];
                            if (order) {
                                var col = d.columns[parseInt(order.column)].data;
                                d.sort = col + ',' + order.dir;
                            }
                        }
                        if (d.search)
                            d.query = d.search.value;
                    },
                    dataSrc: function (json) {
                        json['recordsTotal'] = json['totalElements'];
                        json['recordsFiltered'] = json['totalElements'];
                        return json.content;
                    }
                },
                fnRowCallback: function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                    if (typeof onRowDisplay !== 'undefined' && $.isFunction(onRowDisplay)) {
                        onRowDisplay(nRow, aData, iDisplayIndex, iDisplayIndexFull);
                    }
                },
                drawCallback: function () {
                    if (typeof onPostDraw !== 'undefined' && $.isFunction(onPostDraw)) {
                        onPostDraw();
                    }
                }
            });

            // Single checkbox selection logic
            $(elID).on('click', 'input.dt-checkboxes', function () {
                $('input.dt-checkboxes').not(this).prop('checked', false);
            });

            $(elID + '_filter input').unbind();
            $(elID + '_filter input').bind('keyup', function (e) {
                if (e.keyCode == 13) {
                    pageTable.search(this.value).draw();
                }
            });

            return pageTable;
        }
		
		// function tableInit(name,columns,order,columnDefs,select,url,isInitScrollX){
			
		// 	var elID = '#' + name;
			
		// 	var responsive = false;
			
		// 	if(!isInitScrollX){
				
		// 		isInitScrollX = false;
				
		// 		responsive = true;
				
		// 	}
			
		// 	var pageTable = $(elID).DataTable({
				
		// 		scrollX    : isInitScrollX,
		// 		responsive : responsive,
		// 		processing : true,
		// 		serverSide : true,
		// 		language   : {
					
		// 			processing : '<i class = "fa fa-spinner fa-spin fa-3x fa-fw"</i><span class="sr-only">Loading...</span> '
		// 		},
				
		// 		columnDefs : columnDefs,
		// 		select     : select,
		// 		columns    : columns,
		// 		order      : order,
				
		// 		ajax : {
					
		// 			url   : url,
		// 			data  : function(d){
						
		// 				var pageNumber = parseInt(d.start / d.length);
						
		// 				d.page = pageNumber;
		// 				d.size = d.length;
		// 				if(d.order){
							
		// 					var order = d.order[0];
							
		// 					if(order){
								
		// 						var col = d.columns[parseInt(order.column)].data;
		// 						if(col.sort)
		// 							col = col.sort;
		// 						d.sort = col + ',' + order.dir;
			
		// 					}
							
		// 				}
						
		// 				if(d.search)
		// 					d.query = d.search.value;
		// 			},
					
		// 			dataSrc : function(json){
						
		// 				json['recordsTotal'] = json['totalElements'];
		// 				json['recordsFiltered'] = json['totalElements'];
		// 				return json.content;
						
		// 			}
					
		// 		},
				
		// 		fnRowCallback : function( nRow, aData, iDisplayIndex, iDisplayIndexFull ){
					
		// 			if(typeof onRowDisplay !== 'undefined' && $.isFunction(onRowDisplay)){
						
		// 				console.log('onRowDisplay Callback');
						
		// 				onRowDisplay(nRow, aData, iDisplayIndex, iDisplayIndexFull);
						
		// 			}
					
		// 		},
				
		// 		drawCallback : function(){
					
		// 			if(typeof onPostDraw !== 'undefined' && $.isFunction(onPostDraw)){
						
						
		// 				console.log('onPostDraw Callback');
						
		// 				onPostDraw();
						
		// 			}
					
		// 		}
				
		// 	});
			
		// 	$(elID + '_filter input').unbind();
		// 	$(elID + '_filter input').bind('keyup', function(e){
				
				
		// 		if(e.keyCode == 13){
					
		// 			pageTable.search(this.value).draw();
					
		// 		}
				
		// 	});
			
		// 	return pageTable;
		// }
        
		
		// function tableInit(name, columns, order, columnDefs, select, url, isInitScrollX = false) {
        //     var elID = '#' + name;
        //     var responsive = !isInitScrollX;
        
        //     var pageTable = $(elID).DataTable({
        //         scrollX: isInitScrollX,
        //         responsive: responsive,
        //         processing: true,
        //         serverSide: true,
        //         language: {
        //             processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Loading...</span>'
        //         },
        //         columnDefs: columnDefs,
        //         select: select,
        //         columns: columns,
        //         order: order,
        //         ajax: {
        //             url: url,
        //             data: function(d) {
        //                 var pageNumber = parseInt(d.start / d.length);
        //                 d.page = pageNumber;
        //                 d.size = d.length;
        //                 if (d.order) {
        //                     var order = d.order[0];
        //                     if (order) {
        //                         var col = d.columns[parseInt(order.column)].data;
        //                         d.sort = col + ',' + order.dir;
        //                     }
        //                 }
        //                 if (d.search) d.query = d.search.value;
        //             },
        //             dataSrc: function(json) {
        //                 json['recordsTotal'] = json['totalElements'];
        //                 json['recordsFiltered'] = json['totalElements'];
        //                 return json.content;
        //             }
        //         },
        //         fnRowCallback: function(nRow, aData) {
        //             var jsonColumnIndex = 0;
        //             var jsonColumnData = aData[jsonColumnIndex];
                    
        //             if (jsonColumnData !== undefined && jsonColumnData !== null) {
        //                 try {
        //                     var keyValuePairs = JSON.parse(jsonColumnData);
        //                     var html = '<ul>';
        //                     for (var key in keyValuePairs) {
        //                         if (keyValuePairs.hasOwnProperty(key)) {
        //                             html += '<li><strong>' + key + ':</strong> ' + keyValuePairs[key] + '</li>';
        //                         }
        //                     }
        //                     html += '</ul>';
        //                     $('td:eq(' + jsonColumnIndex + ')', nRow).html(html);
        //                 } catch (error) {
        //                     console.error('Error parsing JSON data:', error);
        //                 }
        //             }
        //             return nRow;
        //         },
        //         drawCallback: function() {
        //             if (typeof onPostDraw !== 'undefined' && $.isFunction(onPostDraw)) {
        //                 console.log('onPostDraw Callback');
        //                 onPostDraw();
        //             }
        //         }
        //     });
        
        //     $(elID + '_filter input').unbind().bind('keyup', function(e) {
        //         if (e.keyCode == 13) {
        //             pageTable.search(this.value).draw();
        //         }
        //     });
        
        //     return pageTable;
        // }
		
		
		// function com_readMultiSelTableData(tableId , colSize){
			
		// 	var rows = [];
		// 	var row  = [];
			
		// 	$(tableID + 'tr.selected td').each(function(index, td){
				
		// 		var colIndex = td._DT_CellIndex.column;
				
		// 		if(colIndex == 0 && row.length > 0){
					
		// 			row.push(row);
		// 			row = [];
					
		// 		}
				
		// 		var tdVals = _com_parseTableCell(this);
		// 		for(var i = 0; i < tdVals.length; i++){
					
		// 			row.push(tdVals[i]);
					
		// 		}
				
		// 	});
			
		// 	if(row.length > 0){
				
		// 		rows.push(row);
				
		// 		return rows;
				
		// 		// console.log('rows: ' + JSON.stringify(rows));
				
		// 	}
			
		// }
		
		function com_disableClick(btnID){
			
			$('#' + btnID).attr('disabled', 'disable');
			
		}
		
		function com_enableClick(btnID){
			
			setTimeout(function(){
				
				$('#' + btnID).removeAttr('disabled');
				
			},1500);
			
			
		}
		
	// function statusAlert(type, msg){
			
	// 		statusAlertWidget('#status_alert_content',type,msg);
			
	// 	}
		
	// 	function statusAlertWidget(id, type, msg){
			
	// 		var statusView = $(id);
			
	// 		if(type == null && msg == null){
				
	// 			statusView.addClass('hidden');
	// 			return;
				
	// 		}
			
	// 		statusView.removeClass('alert-success');
	// 		statusView.removeClass('alert-info');
	// 		statusView.removeClass('alert-danger');
			
	// 		if(type == 'alert-progress'){
				
	// 			statusView.removeClass('alert-info');
	// 			statusView.html('<i class="fa fa-spinner fa-spin" style = "font-size:24px"></i>&nbsp;Please wait....');
				
	// 		}else{
				
	// 			statusView.addClass(type);
	// 			statusView.html(msg);
				
	// 		}
			
	// 		statusView.removeClass('hidden');
			
	// 	}

    // function statusAlert(type, msg){
    //     statusAlertWidget('#status_alert_content', type, msg);
    // }
    
    // function statusAlertWidget(id, type, msg){
    //     var statusView = $(id);
    
    //     if (type == null && msg == null) {
    //         statusView.addClass('hidden');
    //         return;
    //     }
    
    //     statusView.removeClass('alert-success alert-info alert-danger');
    
    //     if (type === 'alert-progress') {
    //         statusView.addClass('alert-info'); // Assuming progress should be styled like info
    //         statusView.html('<i class="fa fa-spinner fa-spin" style="font-size:24px"></i>&nbsp;Please wait....');
    //     } else {
    //         statusView.addClass(type);
    //         statusView.html(msg);
    //     }
    
    //     statusView.removeClass('hidden');
    // }


    function statusAlert(type, msg) {
        statusAlertWidget('#status_alert_content', type, msg);
    }
    
    function statusAlertWidget(id, type, msg) {
        var statusView = $(id);
    
        if (type == null && msg == null) {
            statusView.addClass('hidden');
            return;
        }
    
        statusView.removeClass('alert-success alert-info alert-danger');
    
        if (type === 'alert-progress') {
            statusView.addClass('alert-info'); // Assuming progress should be styled like info
            statusView.html('<i class="fa fa-spinner fa-spin" style="font-size:24px"></i>&nbsp;Please wait....');
        } else {
            statusView.addClass(type);
            statusView.html(msg);
        }
    
        statusView.removeClass('hidden');
    
        // Hide the alert after 5 seconds
        setTimeout(function() {
            statusView.addClass('hidden');
        }, 2000); // 5000 milliseconds = 5 seconds
    }
    
	
	/* --------------------------------- ./table Init ----------------------------------------------- */

/*function com_invokeUrlWithParms(url,filterFields,tableID){
	
	var parms = "";
	
	// clear table page state
	
	com_clearTableState(tableID);
	
	for(i=0; i<filterFields.length;i++){
		
		var fieldName = filterFields[i][0];
		var paramName = filterFields[i][1];
		var val = $(fieldName).val();
		
		if(!val)
			continue;
			
			
		if(params != "")
			params += "&" + paramName + "=" + val;
		else
			params += paramName + "=" + val;
		}
			
		if(params != ""){
			
			if(url.indexOf('?')<0)
				params = "?" + params;
			else
				params = "&" + params;
			
			
		}
		
		window.location.href = url + params;
	}*/
	
	function com_invokeUrlWithParms(url, filterFields, tableID) {
    var params = "";

    // Clear table page state
   // com_clearTableState(tableID);

    for (var i = 0; i < filterFields.length; i++) {
        var fieldName = filterFields[i][0];
        var paramName = filterFields[i][1];
        var val = $(fieldName).val();

        if (!val)
            continue;

        if (params !== "") {
            params += "&" + paramName + "=" + val;
        } else {
            params += paramName + "=" + val;
        }
    }

    if (params !== "") {
        if (url.indexOf('?') < 0) {
            params = "?" + params;
        } else {
            params = "&" + params;
        }
    }

    window.location.href = url + params;
}

	
	// clear table page state
	
	// @param id Table ID
	
	function com_clearTableState(id){
		
		if(!id){
			return;
		}
		
		var table = $('#' + id).DataTable();
		
		if(table && table.state){
			table.state.clear();
			console.log('Clearing... Page State_>');
		}
	}
	
// key value for pageable

/*function com2_getNotNullFields(obj,exclude){
	
	var fields = Object.keys(obj);
	
	if(exclude){
		
		for(i=0; i<exclude.length; i++){
			
			var index = fields.indexOf(exclude[i]);
			
			if(index > -1){
				
				fields.splice(index,1);
				
			}
			
		}
		
		var objWithVal = {};
		
		for(i=0 ; i<fields.length ; i++){
			
			var k = fields[i];
			var v = obj[ k ];
			
			if(v)
				objWithVal[ k ] = v;
	}
	
		return objWithVal;
	}
}*/

/*function com2_getNotNullFields(obj, exclude) {
    var fields = Object.keys(obj);

    if (exclude) {
        for (var i = 0; i < exclude.length; i++) {
            var index = fields.indexOf(exclude[i]);

            if (index > -1) {
                fields.splice(index, 1);
            }
        }

        var objWithVal = {};

        for (var i = 0; i < fields.length; i++) {
            var k = fields[i];
            var v = obj[k];

            if (v !== null) {
                objWithVal[k] = v;
            }
        }

        return objWithVal;
    }
}*/
	
	/*function com2_getNotNullFields(obj, exclude) {
    var objWithVal = {};

    for (var key in obj) {
        if (!exclude.includes(key) && obj[key] !== null) {
            objWithVal[key] = obj[key];
        }
    }

    return objWithVal;
}*/

function com2_getNotNullFields(obj) {
    if (Array.isArray(obj)) {
        return obj.map(com2_getNotNullFields);
    } else if (typeof obj === 'object' && obj !== null) {
        var result = {};
        for (var key in obj) {
            if (obj[key] !== null) {
                result[key] = com2_getNotNullFields(obj[key]);
            }
        }
        return result;
    } else {
        return obj;
    }
}

/*
	function makeResUrl(partUrl,id){
		
		return $(location).attr('origin') + partUrl + id;
		
	}*/
	
	function makeResUrl(partUrl, id) {
    // Ensure there is exactly one slash between the origin and the partUrl
    const separator = partUrl.startsWith('/') ? '' : '/';
    return $(location).attr('origin') + separator + partUrl + id;
}

	
	
	function showStatus(msg,clz){
		
		var status = $('#modalStatus');
		var progress = $('#modalProgress');
		
		status.removeClass("alert alert-success alert-danger");
		
		if(msg == null){
			
			status.html("");
			
			progress.removeClass('hidden');
			
		}else{
			
			status.html(msg);
			
			status.addClass(clz);
			
			progress.addClass('hidden');
			
		}
		
	}
	
	/** Simple Ajax Uploader v2.6.6 Copyright 2012-2019 LPology, LLC  MIT license https://github.com/LPology/Simple-Ajax-Uploader */
!function(e,t){"function"==typeof define&&define.amd?define(function(){return t(e)}):"object"==typeof module&&module.exports?module.exports=t(e):e.ss=t(e)}("undefined"!=typeof window?window:this,function(e){var t,s=e.ss||{},n=/^\s+/,i=/\s+$/,o=/[xy]/g,r=/.*(\/|\\)/,a=/.*[.]/,u=/[\t\r\n]/g,l=Object.prototype.toString.call(e.HTMLElement).indexOf("Constructor")>0,d=-1!==navigator.userAgent.indexOf("MSIE")&&-1===navigator.userAgent.indexOf("MSIE 1"),c=-1!==navigator.userAgent.indexOf("MSIE 7"),p=document.createElement("input");p.type="file",t="multiple"in p&&"undefined"!=typeof File&&"undefined"!=typeof(new XMLHttpRequest).upload,s.obj2string=function(e,t){"use strict";var n=[];for(var i in e)if(e.hasOwnProperty(i)){var o=t?t+"["+i+"]":i,r=e[i];n.push("object"==typeof r?s.obj2string(r,o):encodeURIComponent(o)+"="+encodeURIComponent(r))}return n.join("&")},s.extendObj=function(e,t){"use strict";for(var s in t)t.hasOwnProperty(s)&&(e[s]=t[s])},s.addEvent=function(e,t,n){"use strict";return e.addEventListener?e.addEventListener(t,n,!1):e.attachEvent("on"+t,n),function(){s.removeEvent(e,t,n)}},s.removeEvent=document.removeEventListener?function(e,t,s){e.removeEventListener&&e.removeEventListener(t,s,!1)}:function(e,t,s){var n="on"+t;"undefined"==typeof e[n]&&(e[n]=null),e.detachEvent(n,s)},s.newXHR=function(){"use strict";if("undefined"!=typeof XMLHttpRequest)return new e.XMLHttpRequest;if(e.ActiveXObject)try{return new e.ActiveXObject("Microsoft.XMLHTTP")}catch(t){return!1}},s.encodeUTF8=function(e){"use strict";return unescape(encodeURIComponent(e))},s.getIFrame=function(){"use strict";var e,t=s.getUID();return c?e=document.createElement('<iframe src="javascript:false;" name="'+t+'">'):(e=document.createElement("iframe"),e.src="javascript:false;",e.name=t),e.style.display="none",e.id=t,e},s.getForm=function(e){"use strict";var t=document.createElement("form");t.encoding="multipart/form-data",t.enctype="multipart/form-data",t.style.display="none";for(var s in e)e.hasOwnProperty(s)&&(t[s]=e[s]);return t},s.getHidden=function(e,t){"use strict";var s=document.createElement("input");return s.type="hidden",s.name=e,s.value=t,s},s.parseJSON=function(t){"use strict";if(!t)return!1;if(t=s.trim(t+""),e.JSON&&e.JSON.parse)try{return e.JSON.parse(t+"")}catch(n){return!1}var i,o=/(,)|(\[|{)|(}|])|"(?:[^"\\\r\n]|\\["\\\/bfnrt]|\\u[\da-fA-F]{4})*"\s*:?|true|false|null|-?(?!0\d)\d+(?:\.\d+|)(?:[eE][+-]?\d+|)/g,r=null;return t&&!s.trim(t.replace(o,function(e,t,s,n){return i&&t&&(r=0),0===r?e:(i=s||t,r+=!n-!s,"")}))?Function("return "+t)():!1},s.getBox=function(t){"use strict";var s,n,i=0,o=0;if(t.getBoundingClientRect)s=t.getBoundingClientRect(),n=document.documentElement,i=s.top+(e.pageYOffset||n.scrollTop)-(n.clientTop||0),o=s.left+(e.pageXOffset||n.scrollLeft)-(n.clientLeft||0);else do o+=t.offsetLeft,i+=t.offsetTop;while(t=t.offsetParent);return{top:Math.round(i),left:Math.round(o)}},s.addStyles=function(e,t){"use strict";for(var s in t)t.hasOwnProperty(s)&&(e.style[s]=t[s])},s.copyLayout=function(e,t){"use strict";var n=s.getBox(e);s.addStyles(t,{position:"absolute",display:"block",left:n.left+"px",top:n.top+"px",width:e.offsetWidth+"px",height:e.offsetHeight+"px"})},s.getUID=function(){"use strict";return"axxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(o,function(e){var t=16*Math.random()|0,s="x"==e?t:3&t|8;return s.toString(16)})};var h="".trim;s.trim=h&&!h.call("\ufeff ")?function(e){return null===e?"":h.call(e)}:function(e){return null===e?"":e.toString().replace(n,"").replace(i,"")};var f=[];return s.indexOf=f.indexOf?function(e,t){return e.indexOf(t)}:function(e,t){for(var s=0,n=e.length;n>s;s++)if(e[s]===t)return s;return-1},s.arrayDelete=function(e,t){var n=s.indexOf(e,t);n>-1&&e.splice(n,1)},s.getFilename=function(e){"use strict";return e.replace(r,"")},s.getExt=function(e){"use strict";return-1!==e.indexOf(".")?e.replace(a,""):""},s.isVisible=function(e){"use strict";return e?1!==e.nodeType||e==document.body?(e=null,!0):e.parentNode&&(e.offsetWidth>0||e.offsetHeight>0||"none"!=s.getStyle(e,"display").toLowerCase())?s.isVisible(e.parentNode):(e=null,!1):!1},s.getStyle=function(t,s){"use strict";if(e.getComputedStyle){var n=t.ownerDocument.defaultView.getComputedStyle(t,null);return n.getPropertyValue(s)}return t.currentStyle&&t.currentStyle[s]?t.currentStyle[s]:void 0},s.getFormObj=function(e){"use strict";for(var t,n=e.elements,i=["button","submit","image","reset"],o={},r=0,a=n.length;a>r;r++)if(t={},n[r].name&&!n[r].disabled&&-1===s.indexOf(i,n[r].type)){if(("checkbox"==n[r].type||"radio"==n[r].type)&&!n[r].checked)continue;t[n[r].name]=s.val(n[r]),s.extendObj(o,t)}return o},s.val=function(e){"use strict";if(e){if("SELECT"==e.nodeName.toUpperCase()){for(var t,s=e.options,n=e.selectedIndex,i="select-one"===e.type||0>n,o=i?null:[],r=0,a=s.length;a>r;r++)if((s[r].selected||r===n)&&!s[r].disabled){if(t=s[r].value?s[r].value:s[r].text,i)return t;o.push(t)}return o}return e.value}},s.hasClass=function(e,t){"use strict";return e&&t?(" "+e.className+" ").replace(u," ").indexOf(" "+t+" ")>=0:!1},s.addClass=function(e,t){"use strict";return e&&t?void(s.hasClass(e,t)||(e.className+=" "+t)):!1},s.removeClass=function(){"use strict";var e={};return function(t,s){return t&&s?(e[s]||(e[s]=new RegExp("(?:^|\\s)"+s+"(?!\\S)")),void(t.className=t.className.replace(e[s],""))):!1}}(),s.purge=function(e){"use strict";var t,n,i,o=e.attributes;if(o)for(t=o.length-1;t>=0;t-=1)i=o[t].name,"function"==typeof e[i]&&(e[i]=null);if(o=e.childNodes)for(n=o.length,t=0;n>t;t+=1)s.purge(e.childNodes[t])},s.remove=function(e){"use strict";e&&e.parentNode&&(s.purge(e),e.parentNode.removeChild(e)),e=null},s.verifyElem=function(t){"use strict";return"undefined"!=typeof jQuery&&t instanceof jQuery?t=t[0]:"string"==typeof t&&("#"==t.charAt(0)&&(t=t.substr(1)),t=document.getElementById(t)),t&&1===t.nodeType?("A"==t.nodeName.toUpperCase()&&(t.style.cursor="pointer",s.addEvent(t,"click",function(t){t&&t.preventDefault?t.preventDefault():e.event&&(e.event.returnValue=!1)})),t):!1},s._options={},s.uploadSetup=function(e){"use strict";s.extendObj(s._options,e)},s.SimpleUpload=function(e){"use strict";if(this._opts={button:"",url:"",dropzone:"",dragClass:"",form:"",overrideSubmit:!0,cors:!1,withCredentials:!1,progressUrl:!1,sessionProgressUrl:!1,nginxProgressUrl:!1,multiple:!1,multipleSelect:!1,maxUploads:3,queue:!0,checkProgressInterval:500,keyParamName:"APC_UPLOAD_PROGRESS",sessionProgressName:"PHP_SESSION_UPLOAD_PROGRESS",nginxProgressHeader:"X-Progress-ID",customProgressHeaders:{},corsInputName:"XHR_CORS_TARGETORIGIN",allowedExtensions:[],accept:"",maxSize:!1,name:"",data:{},noParams:!0,autoSubmit:!0,multipart:!0,method:"POST",responseType:"",debug:!1,hoverClass:"",focusClass:"",disabledClass:"",customHeaders:{},encodeHeaders:!0,autoCalibrate:!0,onBlankSubmit:function(){},onAbort:function(){},onChange:function(){},onSubmit:function(){},onProgress:function(){},onUpdateFileSize:function(){},onComplete:function(){},onDone:function(){},onAllDone:function(){},onExtError:function(){},onSizeError:function(){},onError:function(){},startXHR:function(){},endXHR:function(){},startNonXHR:function(){},endNonXHR:function(){}},s.extendObj(this._opts,s._options),s.extendObj(this._opts,e),this._queue=[],this._active=0,this._disabled=!1,this._maxFails=10,this._progKeys={},this._sizeFlags={},this._btns=[],this.addButton(this._opts.button),delete this._opts.button,this._opts.button=e=null,this._opts.multiple===!1&&(this._opts.maxUploads=1),""!==this._opts.dropzone&&this.addDZ(this._opts.dropzone),""===this._opts.dropzone&&this._btns.length<1)throw new Error("Invalid upload button. Make sure the element you're passing exists.");""!==this._opts.form&&this.setForm(this._opts.form),this._createInput(),this._manDisabled=!1,this.enable(!0)},s.SimpleUpload.prototype={destroy:function(){"use strict";for(var e=this._btns.length;e--;)this._btns[e].off&&this._btns[e].off(),s.removeClass(this._btns[e],this._opts.hoverClass),s.removeClass(this._btns[e],this._opts.focusClass),s.removeClass(this._btns[e],this._opts.disabledClass),this._btns[e].disabled=!1;this._killInput(),this._destroy=!0},log:function(t){"use strict";this._opts&&this._opts.debug&&e.console&&e.console.log&&e.console.log("[Uploader] "+t)},setData:function(e){"use strict";this._opts.data=e},setOptions:function(e){"use strict";s.extendObj(this._opts,e)},addButton:function(e){var t;if(e instanceof Array)for(var n=0,i=e.length;i>n;n++)t=s.verifyElem(e[n]),t!==!1?this._btns.push(this.rerouteClicks(t)):this.log("Button with array index "+n+" is invalid");else t=s.verifyElem(e),t!==!1&&this._btns.push(this.rerouteClicks(t))},addDZ:function(e){t&&(e=s.verifyElem(e),e?this.addDropZone(e):this.log("Invalid or nonexistent element passed for drop zone"))},setProgressBar:function(e){"use strict";this._progBar=s.verifyElem(e)},setPctBox:function(e){"use strict";this._pctBox=s.verifyElem(e)},setFileSizeBox:function(e){"use strict";this._sizeBox=s.verifyElem(e)},setProgressContainer:function(e){"use strict";this._progBox=s.verifyElem(e)},setAbortBtn:function(e,t){"use strict";this._abortBtn=s.verifyElem(e),this._removeAbort=!1,t&&(this._removeAbort=!0)},setForm:function(t){"use strict";if(this._form=s.verifyElem(t),this._form&&"FORM"==this._form.nodeName.toUpperCase()){var n=this;this._opts.autoSubmit=!1,this._opts.overrideSubmit&&(s.addEvent(this._form,"submit",function(t){t.preventDefault?t.preventDefault():e.event&&(e.event.returnValue=!1),n._validateForm()&&n.submit()}),this._form.submit=function(){n._validateForm()&&n.submit()})}else this.log("Invalid or nonexistent element passed for form")},getQueueSize:function(){"use strict";return this._queue.length},removeCurrent:function(e){"use strict";if(e){for(var t=this._queue.length;t--;)if(this._queue[t].id===e){this._queue.splice(t,1);break}}else this._queue.splice(0,1);this._cycleQueue()},clearQueue:function(){"use strict";this._queue.length=0},disable:function(e){"use strict";var t,n=this._btns.length;for(this._manDisabled=!e||this._manDisabled===!0,this._disabled=!0;n--;)t=this._btns[n].nodeName.toUpperCase(),"INPUT"!=t&&"BUTTON"!=t||(this._btns[n].disabled=!0),""!==this._opts.disabledClass&&s.addClass(this._btns[n],this._opts.disabledClass);this._input&&this._input.parentNode&&(this._input.parentNode.style.visibility="hidden")},enable:function(e){"use strict";if(e||(this._manDisabled=!1),this._manDisabled!==!0){var t=this._btns.length;for(this._disabled=!1;t--;)s.removeClass(this._btns[t],this._opts.disabledClass),this._btns[t].disabled=!1}},updatePosition:function(e){"use strict";e=e?e:this._btns[0],e&&this._input&&this._input.parentNode&&s.copyLayout(e,this._input.parentNode),e=null},rerouteClicks:function(t){"use strict";var n,i,o=this;if(n=s.addEvent(t,"mouseover",function(){o._disabled||(o._input||o._createInput(),o._overBtn=t,s.copyLayout(t,o._input.parentNode),o._input.parentNode.style.visibility="visible")}),i=s.addEvent(t,"click",function(e){e&&e.preventDefault&&e.preventDefault(),o._disabled||(o._input||o._createInput(),o._overBtn=t,d||o._input.click())}),t.off=function(){n(),i()},o._opts.autoCalibrate&&!s.isVisible(t)){o.log("Upload button not visible");var r=function(){s.isVisible(t)?(o.log("Upload button now visible"),e.setTimeout(function(){o.updatePosition(t),1===o._btns.length&&(o._input.parentNode.style.visibility="hidden")},200)):e.setTimeout(r,500)};e.setTimeout(r,500)}return t},submit:function(e,t){"use strict";if(!t&&this._queue.length<1)return void this._opts.onBlankSubmit.call(this);if(!(this._disabled||this._active>=this._opts.maxUploads||this._queue.length<1)&&this._checkFile(this._queue[0])){if(!1===this._opts.onSubmit.call(this,this._queue[0].name,this._queue[0].ext,this._queue[0].btn,this._queue[0].size))return void this.removeCurrent(this._queue[0].id);this._active++,(this._opts.multiple===!1||this._opts.queue===!1&&this._active>=this._opts.maxUploads)&&this.disable(!0),this._initUpload(this._queue[0])}}},s.IframeUpload={_detachEvents:{},_detach:function(e){this._detachEvents[e]&&(this._detachEvents[e](),delete this._detachEvents[e])},_getHost:function(e){var t=document.createElement("a");return t.href=e,t.hostname?t.hostname.toLowerCase():e},_addFiles:function(e){var t=s.getFilename(e.value),n=s.getExt(t);return!1===this._opts.onChange.call(this,t,n,this._overBtn,void 0,e)?!1:(this._queue.push({id:s.getUID(),file:e,name:t,ext:n,btn:this._overBtn,size:null}),!0)},_uploadIframe:function(t,n,i,o,r,a,u){"use strict";var l,d,c,p=this,h=this._opts,f=s.getUID(),_=s.getIFrame(),m=!1,g=!1;if(d=h.noParams===!0?h.url:h.nginxProgressUrl?d+(d.indexOf("?")>-1?"&":"?")+encodeURIComponent(h.nginxProgressHeader)+"="+encodeURIComponent(f):h.url,l=s.getForm({action:d,target:_.name,method:h.method}),h.onProgress.call(this,0),r&&(r.innerHTML="0%"),o&&(o.style.width="0%"),h.cors){var v=s.getUID();p._detachEvents[v]=s.addEvent(e,"message",function(e){return p._getHost(e.origin)!=p._getHost(h.url)?void p.log("Non-matching origin: "+e.origin):(m=!0,p._detach(v),h.endNonXHR.call(p,t.name,t.btn),void p._finish(t,"","",e.data,i,n,r,a,u))})}p._detachEvents[_.id]=s.addEvent(_,"load",function(){p._detach(_.id),h.sessionProgressUrl?l.appendChild(s.getHidden(h.sessionProgressName,f)):h.progressUrl&&l.appendChild(s.getHidden(h.keyParamName,f)),p._form&&s.extendObj(h.data,s.getFormObj(p._form));for(var d in h.data)h.data.hasOwnProperty(d)&&l.appendChild(s.getHidden(d,h.data[d]));h.cors&&l.appendChild(s.getHidden(h.corsInputName,e.location.href)),l.appendChild(t.file),p._detachEvents[t.id]=s.addEvent(_,"load",function(){if(_&&_.parentNode&&!g)if(p._detach(t.id),g=!0,delete p._progKeys[f],delete p._sizeFlags[f],a&&s.removeEvent(a,"click",c),h.cors)e.setTimeout(function(){s.remove(_),m||p._errorFinish(t,"","",!1,"error",n,i,r,a,u),h=f=_=i=n=r=a=u=null},600);else{try{var o=_.contentDocument?_.contentDocument:_.contentWindow.document,l=o.body.innerHTML;s.remove(_),_=null,h.endNonXHR.call(p,t.name,t.btn),p._finish(t,"","",l,i,n,r,a,u)}catch(d){p._errorFinish(t,"",d.message,!1,"error",n,i,r,a,u)}h=f=i=n=r=null}}),a&&(c=function(){if(s.removeEvent(a,"click",c),delete p._progKeys[f],delete p._sizeFlags[f],_){g=!0,p._detach(t.id);try{_.contentWindow.document.execCommand&&_.contentWindow.document.execCommand("Stop")}catch(o){}try{_.src="javascript".concat(":false;")}catch(o){}e.setTimeout(function(){s.remove(_),_=null},1)}p.log("Upload aborted"),h.onAbort.call(p,t.name,t.btn,t.size),p._last(i,n,r,a,u,t,"abort")},s.addEvent(a,"click",c)),p.log("Commencing upload using iframe"),l.submit(),e.setTimeout(function(){s.remove(l),l=null,p.removeCurrent(t.id)},1),p._hasProgUrl&&(p._progKeys[f]=1,e.setTimeout(function(){p._getProg(f,o,i,r,1),o=i=r=null},600))}),document.body.appendChild(l),document.body.appendChild(_)},_getProg:function(t,n,i,o,r){"use strict";var a,u,l,d=this,c=this._opts,p=(new Date).getTime();if(t&&c)if(c.nginxProgressUrl?u=c.nginxProgressUrl+"?"+encodeURIComponent(c.nginxProgressHeader)+"="+encodeURIComponent(t)+"&_="+p:c.sessionProgressUrl?u=c.sessionProgressUrl:c.progressUrl&&(u=c.progressUrl+"?progresskey="+encodeURIComponent(t)+"&_="+p),l=function(){var u,p,h,f,_;try{if(l&&(c.cors||4===a.readyState)){l=void 0,a.onreadystatechange=function(){};try{_=a.statusText,f=a.status}catch(m){_="",f=""}if(c.cors||f>=200&&300>f){if(u=s.parseJSON(a.responseText),u===!1)return void d.log("Error parsing progress response (expecting JSON)");if(c.nginxProgressUrl){if("uploading"==u.state)p=parseInt(u.size,10),p>0&&(h=Math.round(parseInt(u.received,10)/p*100),p=Math.round(p/1024));else if("done"==u.state)h=100;else if("error"==u.state)return void d.log("Error requesting upload progress: "+u.status)}else(c.sessionProgressUrl||c.progressUrl)&&u.success===!0&&(p=parseInt(u.size,10),h=parseInt(u.pct,10));if(h&&(o&&(o.innerHTML=h+"%"),n&&(n.style.width=h+"%"),c.onProgress.call(d,h)),p&&!d._sizeFlags[t]&&(i&&(i.innerHTML=p+"K"),d._sizeFlags[t]=1,c.onUpdateFileSize.call(d,p)),!h&&!p&&r>=d._maxFails)return r++,void d.log("Failed progress request limit reached. Count: "+r);100>h&&d._progKeys[t]&&e.setTimeout(function(){d._getProg(t,n,i,o,r),t=n=i=o=r=null},c.checkProgressInterval)}else delete d._progKeys[t],d.log("Error requesting upload progress: "+f+" "+_);a=p=h=f=_=u=null}}catch(m){d.log("Error requesting upload progress: "+m.message)}},c.cors&&!c.sessionProgressUrl){if(!e.XDomainRequest)return;a=new e.XDomainRequest,a.open("GET",u,!0),a.onprogress=a.ontimeout=function(){},a.onload=l,a.onerror=function(){delete d._progKeys[t],t=null,d.log("Error requesting upload progress")}}else{var h,f=c.sessionProgressUrl?"POST":"GET",_={};a=s.newXHR(),a.onreadystatechange=l,a.open(f,u,!0),c.sessionProgressUrl&&(h=encodeURIComponent(c.sessionProgressName)+"="+encodeURIComponent(t),_["Content-Type"]="application/x-www-form-urlencoded"),c.nginxProgressUrl&&(_[c.nginxProgressHeader]=t),_["X-Requested-With"]="XMLHttpRequest",_.Accept="application/json, text/javascript, */*; q=0.01",s.extendObj(_,c.customProgressHeaders);for(var m in _)_.hasOwnProperty(m)&&(c.encodeHeaders?a.setRequestHeader(m,s.encodeUTF8(_[m]+"")):a.setRequestHeader(m,_[m]+""));a.send(c.sessionProgressUrl&&h||null)}},_initUpload:function(e){return!1===this._opts.startNonXHR.call(this,e.name,e.btn)?(this._disabled&&this.enable(!0),void this._active--):(this._hasProgUrl=!!(this._opts.progressUrl||this._opts.sessionProgressUrl||this._opts.nginxProgressUrl),this._uploadIframe(e,this._progBox,this._sizeBox,this._progBar,this._pctBox,this._abortBtn,this._removeAbort),void(e=this._progBox=this._sizeBox=this._progBar=this._pctBox=this._abortBtn=this._removeAbort=null))}},s.XhrUpload={_addFiles:function(e){var t,n,i,o,r=e.length;for(this._opts.multiple||(r=1),o=0;r>o;o++){if(t=s.getFilename(e[o].name),n=s.getExt(t),i=Math.round(e[o].size/1024),!1===this._opts.onChange.call(this,t,n,this._overBtn,i,e[o]))return!1;this._queue.push({id:s.getUID(),file:e[o],name:t,ext:n,btn:this._overBtn,size:i})}return!0},_uploadXhr:function(e,t,n,i,o,r,a,u,l,d){"use strict";var c,p,h=this,f=this._opts,_=s.newXHR();o&&(o.innerHTML=e.size+"K"),u&&(u.innerHTML="0%"),r&&(r.style.width="0%"),c=function(t,n){var i;try{if(c&&(n||4===_.readyState))if(c=void 0,_.onreadystatechange=function(){},n)4!==_.readyState&&_.abort(),f.onAbort.call(h,e.name,e.btn,e.size),h._last(o,a,u,l,d,e,"abort");else{l&&s.removeEvent(l,"click",p);try{i=_.statusText}catch(r){i=""}_.status>=200&&_.status<300?(f.endXHR.call(h,e.name,e.size,e.btn),h._finish(e,_.status,i,_.responseText,o,a,u,l,d)):h._errorFinish(e,_.status,i,_.responseText,"error",a,o,u,l,d)}}catch(r){n||h._errorFinish(e,-1,r.message,!1,"error",a,o,u,l,d)}},l&&(p=function(){s.removeEvent(l,"click",p),c&&c(void 0,!0)},s.addEvent(l,"click",p)),_.onreadystatechange=c,_.open(f.method.toUpperCase(),t,!0),_.withCredentials=!!f.withCredentials,s.extendObj(i,f.customHeaders);for(var m in i)i.hasOwnProperty(m)&&(f.encodeHeaders?_.setRequestHeader(m,s.encodeUTF8(i[m]+"")):_.setRequestHeader(m,i[m]+""));if(s.addEvent(_.upload,"progress",function(e){if(e.lengthComputable){var t=Math.round(e.loaded/e.total*100);f.onProgress.call(h,t),u&&(u.innerHTML=t+"%"),r&&(r.style.width=t+"%")}}),f.onProgress.call(this,0),f.multipart===!0){var g=new FormData,v=!1;for(var b in n)n.hasOwnProperty(b)&&(b!==f.name||f.noParams!==!0||h._form||(v=!0),g.append(b,n[b]));v||g.append(f.name,e.file),this.log("Commencing upload using multipart form"),_.send(g)}else this.log("Commencing upload using binary stream"),_.send(e.file);this.removeCurrent(e.id)},_initUpload:function(e){"use strict";var t,n={},i={};return!1===this._opts.startXHR.call(this,e.name,e.size,e.btn)?(this._disabled&&this.enable(!0),void this._active--):(i["X-Requested-With"]="XMLHttpRequest",i["X-File-Name"]=s.encodeUTF8(e.name),"json"==this._opts.responseType.toLowerCase()&&(i.Accept="application/json, text/javascript, */*; q=0.01"),this._opts.multipart||(i["Content-Type"]="application/octet-stream"),this._form&&s.extendObj(n,s.getFormObj(this._form)),s.extendObj(n,this._opts.data),t=this._opts.noParams===!0?this._opts.url:this._opts.url+(this._opts.url.indexOf("?")>-1?"&":"?")+s.obj2string(n),this._uploadXhr(e,t,n,i,this._sizeBox,this._progBar,this._progBox,this._pctBox,this._abortBtn,this._removeAbort),void(this._sizeBox=this._progBar=this._progBox=this._pctBox=this._abortBtn=this._removeAbort=null))}},s.DragAndDrop={_dragFileCheck:function(e){if(e.dataTransfer.types)for(var t=0;t<e.dataTransfer.types.length;t++)if("Files"==e.dataTransfer.types[t])return!0;return!1},addDropZone:function(e){var t=this,n=[];s.addStyles(e,{zIndex:16777271}),e.ondragenter=function(e){return e.stopPropagation(),e.preventDefault(),t._dragFileCheck(e)?(0===n.length&&s.addClass(this,t._opts.dragClass),-1===s.indexOf(n,e.target)&&n.push(e.target),!1):!1},e.ondragover=function(e){return e.stopPropagation(),e.preventDefault(),t._dragFileCheck(e)&&(e.dataTransfer.dropEffect="copy"),!1},e.ondragend=function(){return s.removeClass(this,t._opts.dragClass),!1},e.ondragleave=function(e){return s.arrayDelete(n,e.target),0===n.length&&s.removeClass(this,t._opts.dragClass),!1},e.ondrop=function(e){e.stopPropagation(),e.preventDefault(),s.arrayDelete(n,e.target),0===n.length&&s.removeClass(this,t._opts.dragClass),t._dragFileCheck(e)&&!1!==t._addFiles(e.dataTransfer.files)&&t._cycleQueue()}}},s.extendObj(s.SimpleUpload.prototype,{_createInput:function(){"use strict";var e=this,n=document.createElement("div");this._input=document.createElement("input"),this._input.type="file",this._input.name=this._opts.name,t&&!l&&this._opts.multipleSelect&&(this._input.multiple=!0),"accept"in this._input&&""!==this._opts.accept&&(this._input.accept=this._opts.accept),s.addStyles(n,{display:"none",position:"relative",visibility:"hidden",overflow:"hidden",margin:0,padding:0,opacity:0,direction:"ltr",zIndex:16777270}),"0"!==n.style.opacity&&(n.style.filter="alpha(opacity=0)"),s.addStyles(this._input,{position:"absolute",right:0,margin:0,padding:0,fontSize:"480px",fontFamily:"sans-serif",cursor:"pointer",height:"100%",zIndex:16777270}),this._input.turnOff=s.addEvent(this._input,"change",function(){e._input&&""!==e._input.value&&!1!==e._addFiles(t?e._input.files:e._input)&&(s.removeClass(e._overBtn,e._opts.hoverClass),s.removeClass(e._overBtn,e._opts.focusClass),e._killInput(),e._createInput(),e._opts.autoSubmit&&e.submit())}),""!==e._opts.hoverClass&&(n.mouseOverOff=s.addEvent(n,"mouseover",function(){s.addClass(e._overBtn,e._opts.hoverClass)})),n.mouseOutOff=s.addEvent(n,"mouseout",function(){e._input.parentNode.style.visibility="hidden",""!==e._opts.hoverClass&&(s.removeClass(e._overBtn,e._opts.hoverClass),s.removeClass(e._overBtn,e._opts.focusClass))}),""!==e._opts.focusClass&&(this._input.focusOff=s.addEvent(this._input,"focus",function(){s.addClass(e._overBtn,e._opts.focusClass)}),this._input.blurOff=s.addEvent(this._input,"blur",function(){s.removeClass(e._overBtn,e._opts.focusClass)})),n.appendChild(this._input),document.body.appendChild(n),n=null},_last:function(e,t,n,i,o,r,a,u,l){"use strict";if(e&&(e.innerHTML=""),n&&(n.innerHTML=""),i&&o&&s.remove(i),t&&s.remove(t),this._active--,e=t=n=i=o=null,this._disabled&&this.enable(!0),this._destroy&&0===this._queue.length&&0===this._active)for(var d in this)this.hasOwnProperty(d)&&delete this[d];else this._opts.onDone.call(this,r.name,u,a,l,r.btn,r.size),this._cycleQueue(),0===this._queue.length&&0===this._active&&this._opts.onAllDone.call(this);r=a=u=l=null},_errorFinish:function(e,t,s,n,i,o,r,a,u,l){"use strict";this.log("Upload failed: "+t+" "+s),this._opts.onError.call(this,e.name,i,t,s,n,e.btn,e.size),this._last(r,o,a,u,l,e,"error",t,n),s=i=r=o=a=u=l=null},_finish:function(e,t,n,i,o,r,a,u,l){"use strict";return this.log("Server response: "+i),"json"==this._opts.responseType.toLowerCase()&&(i=s.parseJSON(i),i===!1)?void this._errorFinish(e,t,n,!1,"parseerror",r,o,u,l):(this._opts.onComplete.call(this,e.name,i,e.btn,e.size),this._last(o,r,a,u,l,e,"success",t,i),void(n=o=r=a=u=l=null))},_checkFile:function(e){"use strict";var t=!1,s=this._opts.allowedExtensions.length;if(s>0){for(;s--;)if(this._opts.allowedExtensions[s].toLowerCase()==e.ext.toLowerCase()){t=!0;break}if(!t)return this.removeCurrent(e.id),this.log("File extension not permitted"),this._opts.onExtError.call(this,e.name,e.ext),!1}return e.size&&this._opts.maxSize!==!1&&e.size>this._opts.maxSize?(this.removeCurrent(e.id),this.log(e.name+" exceeds "+this._opts.maxSize+"K limit"),this._opts.onSizeError.call(this,e.name,e.size,e.btn),!1):(e=null,!0)},_killInput:function(){"use strict";this._input&&(this._input.turnOff&&this._input.turnOff(),this._input.focusOff&&this._input.focusOff(),this._input.blurOff&&this._input.blurOff(),this._input.parentNode.mouseOverOff&&this._input.parentNode.mouseOverOff(),s.remove(this._input.parentNode),delete this._input,this._input=null)},_cycleQueue:function(){"use strict";this._queue.length>0&&this._opts.autoSubmit&&this.submit(void 0,!0)},_validateForm:function(){"use strict";return!this._form.checkValidity||this._form.checkValidity()}}),t?s.extendObj(s.SimpleUpload.prototype,s.XhrUpload):s.extendObj(s.SimpleUpload.prototype,s.IframeUpload),s.extendObj(s.SimpleUpload.prototype,s.DragAndDrop),s});
	
	
	
	
    
    
    function _searchCustomer(mobileNumberID,firstNameID,customer_nameID,hiddenID,statusAlertID){
		
		_searchCustomer(mobileNumberID,firstNameID,customer_nameID,hiddenID,statusAlertID,null);
		
	}
    
    /**
	* Search Customer and append HiddenID
	* @param mobileNumberID
	* @param customer_nameID
	* @param hiddenID
	* @param statusAlertID
	* @param performAction
	*/
	function _searchCustomer(mobileNumberID,firstNameID,customer_nameID,hiddenID,statusAlertID,performAction){
		
		$('#' + hiddenID).val();
		$('#' + mobileNumberID).closest('.form-group').removeClass('has-error');
		$('#' + firstNameID).closest('.form-group').removeClass('has-error');
		
		var mobileNumber = $('#' + mobileNumberID).val();
		
		if(mobileNumber == '' || mobileNumber.length < 10){
			
			$('#' + mobileNumberID).closest('.form-group').addClass('has-error');
			statusAlertWidget('#' + statusAlertID , 'alert alert-danger', 'Incorrect assign details. Mention Mobile Number');
			return;
		}
		
		statusAlertWidget('#' + statusAlertID, 'alert-progress', null);
		
		$.get('/api/customer/search?mobile_number=' + mobileNumber, function(data){
			
			$('#' + customer_nameID).removeClass('hidden');
			
			console.log('data --------------------------', data);
			
			if(data.length == 0){
				
				
				
				$('#' + firstNameID).val('');
				$('#' + firstNameID).prop("readonly",false);
				$('#' + firstNameID).closest('.form-group').addClass('has-error');
				
				statusAlertWidget('#' + statusAlertID,'alert alert-danger','Customer is not registered');
				
				if(performAction != null)
					performAction(undefined);
				return;
			}
			
			statusAlertWidget('#' + statusAlertID,'alert alert-success','Customer is registered');
			
			//var customer = data[0][ 'customer' ];
			
			
			
			console.log('data',data.firstName);
			
		//	$('#' + firstNameID).val(customer.firstName);
			$('#' + firstNameID).val(data.firstName);
			$('#' + firstNameID).prop("readonly",true);
		//	$('#' + hiddenID).val(customer.id);
		
			$('#' + hiddenID).val(data.id);
			
			if(performAction != null)
				performAction(data);
				//performAction(customer);
		}).fail(function(){
			
			statusAlertWidget('#' + statusAlertID,'alert alert-danger','Failed to search customer');
			
		});
	}
	

	
	/*--------------------------------------*/
	
/*	document.addEventListener('DOMContentLoaded', function () {
    applyActiveState();
});

	
function markAsActive(event, link) {
    

    var links = document.querySelectorAll('.nav-link');

    links.forEach(function (l) {
        l.classList.remove('active');
    });

    link.classList.add('active');

    // Store the active link index in local storage
    var index = link.getAttribute('data-index');
    localStorage.setItem('activeLinkIndex', index);

    // Navigate to the new page
    window.location.href = link.getAttribute('href');
}

document.addEventListener('DOMContentLoaded', function () {
    applyActiveState();
});
function applyActiveState() {
    var activeLinkIndex = localStorage.getItem('activeLinkIndex');

    if (activeLinkIndex !== null) {
        var link = document.querySelector('.nav-link[data-index="' + activeLinkIndex + '"]');
        if (link) {
            link.classList.add('active');
            // Add code to handle active icon based on your structure
        }
    }
}*/


/*======================================================= below working*/

/*document.addEventListener('DOMContentLoaded', function () {
    applyActiveState();
});

function markAsActive(event, link) {
	
	event.preventDefault();
    var links = document.querySelectorAll('.nav-link');

    links.forEach(function (l) {
        l.classList.remove('active');
    });

    link.classList.add('active');

    var index = link.getAttribute('data-index');
    console.log('Active link index:', index);

    // Convert the index to a string before storing
    localStorage.setItem('activeLinkIndex', index.toString());

    window.location.href = link.getAttribute('href');
}


function applyActiveState() {
    console.log('applyActiveState called');
    var activeLinkIndex = localStorage.getItem('activeLinkIndex');
    console.log('Stored active link index:', activeLinkIndex);

    if (activeLinkIndex !== null) {
        var link = document.querySelector('.nav-link[data-index="' + activeLinkIndex + '"]');
        if (link) {
            console.log('Found link:', link);
            link.classList.add('active');
            
            var icon = link.querySelector('i');
            
            if(icon) {
				icon.style.color = 'black';
			}
            
            // Add code to handle active icon based on your structure
        } else {
            console.log('Link not found.');
        }
    }
}*/

/*document.addEventListener('DOMContentLoaded', function () {
    applyActiveState();
});

function markAsActive(event, link) {
    console.log(">>>>>>>>>>>>>>>>>> >>>>>>>>>>>>>>>>>>>>>>>>> : ");
    
    var links = document.querySelectorAll('.nav-link');

    links.forEach(function (l) {
        l.classList.remove('active');
        // Reset styles for other elements if needed
        var icon = l.querySelector('i');
        if (icon) {
            icon.style.color = '';
        }
        var activeBar = l.querySelector('.active-bar');
        if (activeBar) {
            activeBar.style.backgroundColor = '';
            activeBar.style.height = '';
        }
    });

    link.classList.add('active');

    // Store the active link index in local storage
    var index = link.getAttribute('data-index');
    localStorage.setItem('activeLinkIndex', index);

    // Navigate to the new page
    window.location.href = link.getAttribute('href');
}

function applyActiveState() {
    var activeLinkIndex = localStorage.getItem('activeLinkIndex');
    var currentUrl = window.location.pathname + window.location.search;

    if (activeLinkIndex !== null) {
        var link = document.querySelector('.nav-link[data-index="' + activeLinkIndex + '"]');
        if (link) {
            var dataLinkTh = link.getAttribute('data-link-th');

            if (dataLinkTh !== null) {
                var allowedBaseUrls = ['/my/reward', '/my/reward/profile', '/my/contactUs'];

                // Check if the current URL starts with any of the allowed base URLs
                if (allowedBaseUrls.some(baseUrl => currentUrl.startsWith(baseUrl))) {
                    console.log('Found link:', link);
                    link.classList.add('active');

                    // Assuming your icon is within the link, modify the selector accordingly
                    var icon = link.querySelector('i'); // Adjust the selector based on your HTML structure

                    if (icon) {
                        icon.style.color = 'black'; // Change the color to black
                    }

                    // Add styling to the active bar
                    var activeBar = link.querySelector('.active-bar'); // Adjust the selector based on your HTML structure

                    if (activeBar) {
                        activeBar.style.backgroundColor = 'black'; // Change the color of the bar to black
                        activeBar.style.height = '2px'; // Set the height of the bar
                    }
                } else {
                    console.log('Link is not in allowed base URLs. Skipping styling.');
                }
            } else {
                console.log('data-link-th is null for the link.');
            }
        } else {
            console.log('Link not found.');
        }
    }
}



*/

/*function markAsActive(event, element) {
    // Get the selected feature index
    var selectedFeatureIndex = parseInt(element.getAttribute('data-index'));
    
    // Store the selected feature index in sessionStorage
    sessionStorage.setItem('selectedFeatureIndex', selectedFeatureIndex);

    // Remove 'selected' class from all menu items
    var menuItems = document.querySelectorAll('.nav-link');
    menuItems.forEach(item => item.classList.remove('selected'));

    // Add 'selected' class to the clicked menu item
    element.classList.add('selected');
}

// Restore the selected state on page load
document.addEventListener('DOMContentLoaded', function () {
    var selectedFeatureIndex = sessionStorage.getItem('selectedFeatureIndex');
    if (selectedFeatureIndex !== null) {
        // Remove 'selected' class from all menu items
        var menuItems = document.querySelectorAll('.nav-link');
        menuItems.forEach(item => item.classList.remove('selected'));

        // Add 'selected' class to the menu item with the stored index
        var selectedElement = document.querySelector('[data-index="' + selectedFeatureIndex + '"]');
        if (selectedElement) {
            selectedElement.classList.add('selected');
        }
    }
});*/

/*document.addEventListener('DOMContentLoaded', function () {
    applyActiveState();
});

function applyActiveState() {
	var activeLinkIndex = null;
	
	var activeLink = $("#activeLinkContainer").text();
	
	var index_ = $("#index_").text();
	
	var currentUrl = window.location.pathname + window.location.search;
	
	var activeBar = $(".active-bar_");
	
	console.log(">>>>>>>>>>>>>> : activeLink ", activeLink);
	console.log(">>>>>>>>>>>>>> : index_ ", index_);
	console.log(">>>>>>>>>>>>>> : currentUrl ", currentUrl);
	
	if(activeLink === currentUrl && index_ === "0") {
		console.log(">>>>>>>>>>>>>> In side if condition");
		activeLinkIndex = 0;
	}else if(activeLink === currentUrl && index_ === "1") {
		console.log(">>>>>>>>>>>>>> In side if condition");
		activeLinkIndex = 1;
	}else if(activeLink === currentUrl && index_ === "2") {
		console.log(">>>>>>>>>>>>>> In side if condition");
		activeLinkIndex = 2;
	}
	
	console.log("stored active link index ", activeLinkIndex);
	
	if(activeLinkIndex != null) {
		
		var link = document.querySelector('.nav-link[data-index="' + activeLinkIndex + '"]');
		
		console.log(" >>>>>>>>>>>>>>>>> link ", link);
		
		if(link) {
			
			link.classList.add('active');
			
			var icon = link.querySelector('i');
			
			if(icon) {
				console.log(">>>>> icon", icon);
				
				icon.style.color = 'black';
				
			}
			
			var activeBar = link.querySelector('.active-bar');
			
			if(activeBar) {
				activeBar.style.backgroundColor = 'black';
				activeBar.style.height = '2px';
				activeBar.style.width = '28px';
			}
			
		}
		
	}
}

*/

/*document.addEventListener('DOMContentLoaded', applyActiveState);

function applyActiveState() {
    var activeLinkIndex = null;
    var activeLink = $("#activeLinkContainer").text();
    var index_ = $("#index_").text();
    var currentUrl = window.location.pathname + window.location.search;
    var activeBar = $(".active-bar_");

    if (activeLink === currentUrl && index_ >= "0" && index_ <= "2") {
        activeLinkIndex = parseInt(index_);
        console.log("In side if condition");
    }

    console.log("Stored active link index: ", activeLinkIndex);

    if (activeLinkIndex !== null) {
        var link = document.querySelector('.nav-link[data-index="' + activeLinkIndex + '"]');

        if (link) {
            link.classList.add('active');
            var icon = link.querySelector('i');

            if (icon) {
                console.log("Icon: ", icon);
                icon.style.transition = 'color 0.3s ease'; // Transition for color change
                icon.style.color = 'black';
            }

            var activeBar = link.querySelector('.active-bar');

            if (activeBar) {
                activeBar.style.transition = 'background-color 0.3s ease, height 0.3s ease, width 0.3s ease'; // Transitions for background-color, height, and width
                activeBar.style.backgroundColor = 'black';
                activeBar.style.height = '2px';
                activeBar.style.width = '28px';
            }
        }
    }
}
*/

  

/* ------------ Membership Page Wise ------------------- */


function miniTableInit(name,columns,url){

	var elID = '#' + name;

	var pageTable = $(elID).DataTable({

		bSort      : false,
		responsive : true,
		processing : true,
		serverSide : true,
		language   : {

			processing : '<i class = "fa fa-spinner fa-spin fa-3x fa-fw"></i><span class = "sr-only">Loading...</span>'		

		},

		columnDefs  : undefined,
		select      : undefined,
		columns     : columns,
		order       : false,
		pagelength  : 5,
		bLengthChange : false,
		bFilter     : false,
		bInfo       : false,

		ajax : {

			url  :  url,
			data : function(d){

			var pageNumber = parseInt(d.start/d.length);
			d.page = pageNumber;
			d.size = d.length;
			if(d.order){
			
				var order = d.order[0];
				if(order){
					
					var col = d.columns[parseInt(order.column)].data;
					if(col.sort)
						col = col.sort;
					d.sort = col + ','+ order.dir;

				}

			}

			if(d.search)
				d.query = d.search.value;		
		
		},

			dataSrc : function(json){
						
						json['recordsTotal'] = json['totalElements'];
						json['recordsFiltered'] = json['totalElements'];
						return json.content;
						
					}		

	     },

drawCallback : function(){
					
					if(typeof onPostDraw !== 'undefined' && $.isFunction(onPostDraw)){
						
						
						console.log('onPostDraw Callback');
						
						onPostDraw();
						
					}
					
				}
				
			});
			
			$(elID + '_filter input').unbind();
			$(elID + '_filter input').bind('keyup', function(e){
				
				
				if(e.keyCode == 13){
					
					pageTable.search(this.value).draw();
					
				}
				
			});
			
			return pageTable;
		}


