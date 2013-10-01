'use strict';

angular.module('nzbUiApp')
  .controller('TestCtrl', function ($scope, $log) {

  	$scope.data = [ 
  		'alpha', 'bravo', 'charlie', 'delta', 'echo', 'foxtrot', 'golf', 'hotel' 
  	];
  	$scope.selection = [];

  	$scope.add = function() {
  		$scope.data[$scope.data.length] = $scope.addText;
  		$scope.addText = '';
  		if(!$scope.$digest && !$scope.$root.$phase) {
  			$scope.$root.apply();
  		}
  	}

    $scope.handleSelectionChange = function() {
      $log.info("handleSelectionChange()");
      $scope.forceUpdate();
    }

  	$scope.$watch('selection', function() {
  		if(!$scope.$$phase && !$scope.$root.$$phase) {
  			$scope.$root.$apply();
  		}
  	});

    /** Forces a render cycle.  */
    $scope.forceUpdate = function() {
      if(!$scope.$$phase && !$scope.$root.$$phase) {
        $scope.$root.$digest();
      }
    }

  });
