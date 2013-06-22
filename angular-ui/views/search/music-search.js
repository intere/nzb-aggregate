'use strict';

angular.module( 'nzb.utils' )
	.controller( 'MusicSearchCtrl', function($scope, $log, $location, MusicSearchService) {
		// Options for the Result Type Select (using objects for demonstration purposes)
		$scope.searchTypes = [
			{name: 'All'},
			{name: "Mix", type: 'mixTerm'}, 
			// {name: "Genre", type: 'genreIndex'},
			{name: "Artist", type: 'artistTerm'}, 
			{name: "Composer", type: 'composerTerm'}, 
			{name: "Album", type: 'albumTerm'}, 
			// {name: "Rating", type: 'ratingIndex'}, 
			{name: "Song", type: 'songTerm'}
		];

		$scope.resultTypes = [ 
			{ name: 'Album', type: 'album' },
			{ name: 'Song', type: 'song' }
		];

		$scope.resultType = $scope.resultTypes[0];
		$scope.searchType = $scope.searchTypes[0];

		// The Options for the Result Limits
		$scope.resultLimits = [ 25, 50, 100, 200, 300, 500 ];
		// Setting this resultLimit sets the default option in the select
		$scope.resultLimit = 100;

		// Start with no results (or selected results) in the table.
		$scope.results = $scope.selectedItems = [];

		// Grid Options:
		$scope.gridOptions = {
		    data: 'results',
		    columnDefs: [
		    	{cellClass:'album-cover',cellTemplate:'<img ng-src="{{COL_FIELD}}">',field:'artworkUrl60', displayName:'Cover'},
			    {field:'artistName', displayName:'Artist'},
			    {field:'collectionName', displayName:'Album'},
			    {field:'trackName', displayName: 'Track Name'},
			    {field:'releaseDate', displayName:'Release Date'},
			    {field:'trackCount', displayName:'Tracks'},
			    {field:'primaryGenreName', displayName:'Genre'}
			],
			showFooter: true,
			totalServerItems: 0,
			selectedItems: $scope.selectedItems,
			multiSelect: false,
			enableColumnResize: true
		};

		$scope.viewSelected = function() {
			var selected = $scope.gridOptions.selectedItems[0];

			if(selected.wrapperType == "collection") {
				$location.path("/album/" + selected.collectionId);
			} else if(selected.wrapperType == "track") {
				$log.info("TODO: handle track (id=" + selected.trackId + ")");
			} else {

				$log.warn("Unknown wrapper type: '" + selected.wrapperType + "'");
			}
		}

		$scope.canView = function() {
			return $scope.gridOptions.selectedItems != undefined && $scope.gridOptions.selectedItems.length > 0;
		}

		// Search Function:
		$scope.doSearch = function() {

			MusicSearchService.searchText = $scope.searchText;
			MusicSearchService.searchType = $scope.searchType.type;
			MusicSearchService.resultType = $scope.resultType.type;
			MusicSearchService.limit = $scope.resultLimit;


			MusicSearchService.get()
				.success(function(response){
					$log.info("We got " + response.resultCount + " results");
					$scope.results = response.results;
					$scope.gridOptions.totalServerItems = $scope.results.length;
					$scope.gridOptions.selectedItems.length = 0;
				})
				.error(function(error){
					$log.error("There was an error getting your results: " + error);
				});
		}

	}
);
