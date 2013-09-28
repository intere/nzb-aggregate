'use strict';

angular.module('nzbUiApp')
  .controller('NzbSearchCtrl', function ($scope, $log, NzbService) {

  	$scope.maxResults = 100;
  	$scope.selectedItems = [];
  	$scope.results = [];
  	$scope.decoratedResults = undefined;

  	// Grid Options:
	$scope.gridOptions = {
	    data: 'results',
	    columnDefs: [
	    	{field:'collection',displayName:'',cellClass:'collection',cellTemplate:'<i class="icon-th" ng-show="COL_FIELD"></i>', width: '20'},
		    {field:'subject', displayName:'Subject', width: "800"},
		    {field:'group', displayName:'Group'},
		    {field:'poster', displayName: 'Poster'},
		    {field:'size', displayName: 'Size', cellTemplate: '<div ng-cell-text>{{COL_FIELD.size}} {{COL_FIELD.unit}}</div>', width: '100'}
		    // {field:'', displayName: ''},

		],
		showFooter: true,
		totalServerItems: 0,
		selectedItems: $scope.selectedItems,
		multiSelect: true,
		enableColumnResize: true,
		showFilter: true,
		rowHeight: 60
	};

  	$scope.getMaxResults = function() {
  		return [50, 100, 250, 500, 1000, 2500, 5000];
  	};

  	$scope.nzbSearch = function() {
  		NzbService.doSearch($scope.searchText, $scope.maxResults, $scope.startIndex).then(
  			function(results){
  				$scope.selectedItems.length = 0;
  				$scope.decoratedResults = results;
  				$scope.results = results.flattenResults();
  				console.dir(results);
  			}, function(error) {
  				throw new Error(error);
  			}
  		);
  	};

  });
