'use strict';

angular.module('nzbUiApp')
  .directive('spinner', function ($log) {

    return {
      template: '<div class="loading-spinner" ng-show="busy"></div>',
      restrict: 'A',
      scope: {
      	busy: '=spinner',
      	lines: '=lines',
      	width: '=width',
      	radius: '=radius',
      	color: '=color',
      	length: '=length'
      },
      link: function postLink(scope, element, attrs) {

      	// Spinner Options:
      	var opts = {
		  lines: scope.lines || 13, // The number of lines to draw
		  length: scope.length || 20, // The length of each line
		  width: scope.width || 10, // The line thickness
		  radius: scope.radius || 30, // The radius of the inner circle
		  corners: 1, // Corner roundness (0..1)
		  rotate: 0, // The rotation offset
		  direction: 1, // 1: clockwise, -1: counterclockwise
		  color: scope.color || '#000', // #rgb or #rrggbb or array of colors
		  speed: 1, // Rounds per second
		  trail: 60, // Afterglow percentage
		  shadow: false, // Whether to render a shadow
		  hwaccel: false, // Whether to use hardware acceleration
		  className: 'spinner', // The CSS class to assign to the spinner
		  zIndex: 2e9, // The z-index (defaults to 2000000000)
		  top: 'auto', // Top position relative to parent in px
		  left: 'auto' // Left position relative to parent in px
		};

		scope.spinner = new Spinner(opts).spin(element[0]);

		scope.$watch('busy', function() {
			element[0].style.display = (scope.busy ? 'block' : 'none');
			$log.info("Busy: " + scope.busy);
		});

      }
    };
  });
