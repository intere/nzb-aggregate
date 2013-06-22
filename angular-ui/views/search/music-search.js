'use strict';

angular.module( 'nzb.utils' )
	.controller( 'MusicSearch', function($scope, $log, $http) {
		$scope.resultTypes = [
			// 'artist',
			{
				name: 'album',
			},
			{
				name: 'song'				
			}
		];
		$scope.resultType = $scope.resultTypes[0];
		$scope.resultLimits = [
			25,
			50,
			100,
			200,
			300,
			500
		];
		$scope.resultLimit = 100;
		$scope.results = [];

		// Grid Options:
		$scope.gridOptions = {
		    data: 'results',
		    columnDefs: [
			    {field:'artistName', displayName:'Artist'},
			    {field:'collectionName', displayName:'Album'},
			    {field:'trackName', displayName: 'Track Name'},
			    {field:'releaseDate', displayName:'Release Date'},
			    {field:'trackCount', displayName:'Tracks'},
			    {field:'primaryGenreName', displayName:'Genre'}
			]
		};

		$scope.doSearch = function() {
			// $log.info("Searching for: " + $scope.searchText + ", by " + $scope.resultType);

			var url = 'https://itunes.apple.com/search?term=' + $scope.searchText 
				+ '&entity=' + $scope.resultType.name 
				+ "&limit=" + $scope.resultLimit
				+ '&callback=JSON_CALLBACK';

			$log.info("Executing Query: '" + url + "'");

			$http.jsonp(url).success(function(response){
				$log.info("We got " + response.resultCount + " results");
				$scope.results = response.results;
			}).error(function(error){
				$log.error("There was an error getting your results: " + error);
			});
		}
	});
