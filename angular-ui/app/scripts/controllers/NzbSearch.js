'use strict';

angular.module('nzbUiApp')
  .controller('NzbSearchCtrl', function ($scope, $log, NzbService) {

  	$scope.maxResults = 500;
  	$scope.results = [];
  	$scope.decoratedResults = undefined;

 //  	// Grid Options:
	// $scope.gridOptions = {
	//     data: 'results',
	//     columnDefs: [
	//     	{field:'artworkUrl60',displayName:'Cover',cellClass:'album-cover',cellTemplate:'<img ng-src="{{COL_FIELD}}">'},
	// 	    {field:'artistName', displayName:'Artist'},
	// 	    {field:'collectionName', displayName:'Album'},
	// 	    {field:'trackName', displayName: 'Track Name'},
	// 	    {field:'releaseDate', displayName:'Released', cellTemplate:'<span ng-cell-text>{{convertDate(COL_FIELD)}}</span>',cellClass:'date'},
	// 	    {field:'trackCount', displayName:'Tracks'},
	// 	    {field:'primaryGenreName', displayName:'Genre'},
	// 	    {field:'previewUrl', displayName:'Preview', cellTemplate:'<a ng-href="{{COL_FIELD}}" target="_new" ng-hide="!COL_FIELD">Listen</a>',cellClass:'link'}
	// 	],
	// 	showFooter: true,
	// 	totalServerItems: 0,
	// 	selectedItems: $scope.selectedItems,
	// 	multiSelect: false,
	// 	enableColumnResize: true,
	// 	rowHeight: 60
	// };

  	$scope.getMaxResults = function() {
  		return [50, 100, 250, 500, 1000, 2500, 5000];
  	};

  	$scope.nzbSearch = function() {
  		NzbService.doSearch($scope.searchText, $scope.maxResults, $scope.startIndex).then(
  			function(results){
  				$scope.decoratedResults = results;
  				$scope.results = results.flattenResults();
  				console.dir(results);
  			}, function(error) {
  				throw new Error(error);
  			}
  		);
  	};

  });
