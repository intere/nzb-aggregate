'use strict';

angular.module('nzb.utils').controller('NzbSearchCtrl',function($scope, $http, $log) {

	$scope.results = [];

	$scope.filterOptions = {
	    filterText: 'filteringText',
	    useExternalFilter: false
	};

	$http.get('/results-1.json')
		.success(function(data){
			$scope.results = aggregate(data);
			$scope.gridOptions.totalServerItems = $scope.results.length;
			// $log.info(JSON.stringify(data));
		}).error(function(error){
			$log.error("Error getting nzb results back");
		});

	function aggregate(data) {
		var results = [];
		for(var i in data) {
			for(var j in data[i].nzbRows) {
				results[results.length] = data[i].nzbRows[j];
			}
		}

		return results;
	};

	$scope.getSize = function(size) {
		if(size) {
			return size.size + " " + size.unit;
		}
	};

	// Grid Options:
	$scope.gridOptions = {
	    data: 'results',
	    columnDefs: [
	    	{field:'id', displayName:'ID',width: 60},
		    {field:'subject', displayName:'Subject',width:250},
		    {field:'poster', displayName:'Poster',width:100},
		    {field:'group', displayName: 'Group',width:150},
		    {field:'size', displayName:'Size',width:80,cellTemplate:'<span ng-cell-text>{{getSize(COL_FIELD)}}</span>',cellClass:'date'},
		    {field:'parts',}
		    // {field:'trackCount', displayName:'Tracks',width:'60'},
		    // {field:'primaryGenreName', displayName:'Genre',width:'100'},
		    // {field:'previewUrl', displayName:'Preview',width:80,cellTemplate:'<a ng-href="{{COL_FIELD}}" target="_new" ng-hide="!COL_FIELD">Listen</a>',cellClass:'link'}
		],
		filterOptions: $scope.filterOptions,
		showFooter: true,
		totalServerItems: 0,
		selectedItems: $scope.selectedItems,
		multiSelect: false,
		enableColumnResize: true,
		rowHeight: 60
	};
});