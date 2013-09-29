'use strict';

angular.module('nzbUiApp')
  .controller('NzbSearchCtrl', function ($scope, $log, $filter, NzbService) {

  	$scope.maxResults = 100;
  	$scope.selectedItems = [];
  	$scope.results = [];
  	$scope.decoratedResults = undefined;
  	$scope.loading = false;
  	
  	$scope.filterOptions = {
		filterText: undefined,
		useExternalFilter: true
	};

  	// Grid Options:
	$scope.gridOptions = {
	    data: 'filteredResults',
	    columnDefs: [
	    	{field:'id',displayName:'ID',width:35},
	    	{field:'collection',displayName:'',cellClass:'collection',cellTemplate:'<i class="icon-th" ng-show="COL_FIELD"></i>', width: 25 },
		    {field:'subject', displayName:'Subject', width: "800"},
		    {field:'group', displayName:'Group'},
		    {field:'poster', displayName:'Poster'},
		    {field:'contains', displayName:'Contents'},
		    {field:'size', displayName: 'Size', cellTemplate: '<div ng-cell-text>{{COL_FIELD.size}} {{COL_FIELD.unit}}</div>', width: '100'}
		],
		showFooter: true,
		totalServerItems: 0,
		selectedItems: $scope.selectedItems,
		multiSelect: true,
		enableColumnResize: true,
		filterOptions: $scope.filterOptions,
		rowHeight: 60
	};

	$scope.canAdd = function() {
		return $scope.selectedItems.length>0;
	}

	$scope.canPost = function() {
		return $scope.decoratedResults && $scope.decoratedResults.hasPosts();
	}

	$scope.addSelected = function() {
		for(var i in $scope.selectedItems) {
			$scope.decoratedResults.addPost($scope.selectedItems[i]);
		}

		for(var i in $scope.selectedItems) {
			for(var j in $scope.results) {
				if($scope.selectedItems[i]==$scope.results[j]) {
					$scope.results.splice(j,1);
					break;
				}
			}
		}

		$scope.selectedItems.length = 0;
	}

  	$scope.getMaxResults = function() {
  		return [50, 100, 250, 500, 1000, 2500, 5000];
  	};

  	$scope.nzbSearch = function() {
  		$scope.loading = true;
  		NzbService.doSearch($scope.searchText, $scope.maxResults, $scope.startIndex).then(
  			function(results){
  				$scope.selectedItems.length = 0;
  				$scope.decoratedResults = results;
  				$scope.results = results.flattenResults();
  				console.dir(results);
  				$scope.loading = false;
  			}, function(error) {
  				throw new Error(error);
  				$scope.loading = false;
  			}
  		);
  	};

  	$scope.myFilter = function(row) {
  		if($scope.showCollections && !row.collection) {
  			return false;
  		} else if($scope.filterText && $scope.filterText.length>0 
  			&& row.subject.toLowerCase().indexOf($scope.filterText.toLowerCase())<0) {
  			return false;
  		}


  		return true;
  	}

  	$scope.updatefilters = function() {
  		$scope.filteredResults = $filter('filter')($scope.results, $scope.myFilter);
  		if(!$scope.$$phase) {
  			$scope.$digest();
  		}
  	};

  	$scope.$watch('results', function() {
  		$scope.updatefilters();
  	});

  	$scope.$watch('filterText', function() {
  		$scope.updatefilters();
  	});

  	$scope.$watch('showCollections', function() {
  		$scope.updatefilters();
  	});

  });
