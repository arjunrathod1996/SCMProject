/*!
 * Start Bootstrap - SB Admin 2 v3.3.7+1 (https://startbootstrap.com/template-overviews/sb-admin-2)
 * Copyright 2013-2016 Start Bootstrap
 * Licensed under MIT (https://github.com/BlackrockDigital/startbootstrap/blob/gh-pages/LICENSE)
 */
$(function() {
    $('#side-menu').metisMenu();
  });
  
  //Loads the correct sidebar on window load,
  //collapses the sidebar on window resize.
  // Sets the min-height of #page-wrapper to window size
  $(function() {
    $(window).bind("load resize", function() {
      var topOffset = 50;
      var width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
      if (width < 768) {
        $('div.navbar-collapse').addClass('collapse');
        topOffset = 100; // 2-row-menu
      } else {
        $('div.navbar-collapse').removeClass('collapse');
      }
  
      var height = ((this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height) - 1;
      height = height - topOffset;
      if (height < 1) height = 1;
      if (height > topOffset) {
        $("#page-wrapper").css("min-height", (height) + "px");
      }
    });
  
    var url = window.location;
    // var element = $('ul.nav a').filter(function() {
    //     return this.href == url;
    // }).addClass('active').parent().parent().addClass('in').parent();
    var element = $('ul.nav a').filter(function() {
      return this.href == url;
    }).addClass('active').parent();
  
    while (true) {
      if (element.is('li')) {
        element = element.parent().addClass('in').parent();
      } else {
        break;
      }
    }
  });
  $(function() {
  
    Morris.Area({
      element: 'morris-area-chart',
      data: [{
        period: '2010 Q1',
        iphone: 2666,
        ipad: null,
        itouch: 2647
      }, {
        period: '2010 Q2',
        iphone: 2778,
        ipad: 2294,
        itouch: 2441
      }, {
        period: '2010 Q3',
        iphone: 4912,
        ipad: 1969,
        itouch: 2501
      }, {
        period: '2010 Q4',
        iphone: 3767,
        ipad: 3597,
        itouch: 5689
      }, {
        period: '2011 Q1',
        iphone: 6810,
        ipad: 1914,
        itouch: 2293
      }, {
        period: '2011 Q2',
        iphone: 5670,
        ipad: 4293,
        itouch: 1881
      }, {
        period: '2011 Q3',
        iphone: 4820,
        ipad: 3795,
        itouch: 1588
      }, {
        period: '2011 Q4',
        iphone: 15073,
        ipad: 5967,
        itouch: 5175
      }, {
        period: '2012 Q1',
        iphone: 10687,
        ipad: 4460,
        itouch: 2028
      }, {
        period: '2012 Q2',
        iphone: 8432,
        ipad: 5713,
        itouch: 1791
      }],
      xkey: 'period',
      ykeys: ['iphone', 'ipad', 'itouch'],
      labels: ['iPhone', 'iPad', 'iPod Touch'],
      pointSize: 2,
      hideHover: 'auto',
      resize: true
    });
  
    Morris.Donut({
      element: 'morris-donut-chart',
      data: [{
        label: "Download Sales",
        value: 12
      }, {
        label: "In-Store Sales",
        value: 30
      }, {
        label: "Mail-Order Sales",
        value: 20
      }],
      resize: true
    });
  
    Morris.Bar({
      element: 'morris-bar-chart',
      data: [{
        y: '2006',
        a: 100,
        b: 90
      }, {
        y: '2007',
        a: 75,
        b: 65
      }, {
        y: '2008',
        a: 50,
        b: 40
      }, {
        y: '2009',
        a: 75,
        b: 65
      }, {
        y: '2010',
        a: 50,
        b: 40
      }, {
        y: '2011',
        a: 75,
        b: 65
      }, {
        y: '2012',
        a: 100,
        b: 90
      }],
      xkey: 'y',
      ykeys: ['a', 'b'],
      labels: ['Series A', 'Series B'],
      hideHover: 'auto',
      resize: true
    });
  
  });
  $('.ttspan-right').on('click', function(e) {
    console.log('right');
    e.preventDefault();
    $("#wrapper").toggleClass("tt-toggled");
  });