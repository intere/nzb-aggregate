'use strict';

angular.module('angularUiApp')
  .controller('iTunesSearchCtrl', function($scope, $log, $location, iTunesSearchService) {
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

		// $scope.layoutPlugin = new ngGridLayoutPlugin();

		$scope.resultType = $scope.resultTypes[0];
		$scope.searchType = $scope.searchTypes[0];

		// The Options for the Result Limits
		$scope.resultLimits = [ 25, 50, 100, 200, 300, 500 ];
		
		// Setting this resultLimit sets the default option in the select
		$scope.resultLimit = iTunesSearchService.limit || 100;

		// Start with no results (or selected results) in the table.
		$scope.results = iTunesSearchService.results;

		// Default the Selection to nothing
		$scope.selectedItems = [];


		$scope.searchText = iTunesSearchService.searchText || "";
		$scope.searchType = findSearchType(iTunesSearchService.searchType, $scope.searchTypes[0]);
		$scope.resultType = findResultType(iTunesSearchService.resultType, $scope.resultTypes[0]);
		
		function findSearchType(selectedType, defaultType) {
			if(selectedType) {
				for(var i in $scope.searchTypes) {
					if($scope.searchTypes[i].type == selectedType) {
						return $scope.searchTypes[i];
					}
				}
			}

			return defaultType || $scope.searchTypes[0];
		}

		function findResultType(selectedType, defaultType) {
			if(selectedType) {
				for(var i in $scope.resultTypes) {
					if($scope.resultTypes[i].type==selectedType) {
						return $scope.resultTypes[i];
					}
				}
			}

			return defaultType || $scope.resultTypes[0];
		}

		// Grid Options:
		$scope.gridOptions = {
		    data: 'results',
		    columnDefs: [
		    	{field:'artworkUrl60', displayName:'Cover',cellClass:'album-cover',cellTemplate:'<img ng-src="{{COL_FIELD}}">',width: 60},
			    {field:'artistName', displayName:'Artist',width:150},
			    {field:'collectionName', displayName:'Album',width:250},
			    {field:'trackName', displayName: 'Track Name',width:'auto'},
			    {field:'releaseDate', displayName:'Released',width:80,cellTemplate:'<span ng-cell-text>{{convertDate(COL_FIELD)}}</span>',cellClass:'date'},
			    {field:'trackCount', displayName:'Tracks',width:'60'},
			    {field:'primaryGenreName', displayName:'Genre',width:'100'},
			    {field:'previewUrl', displayName:'Preview',width:80,cellTemplate:'<a ng-href="{{COL_FIELD}}" target="_new" ng-hide="!COL_FIELD">Listen</a>',cellClass:'link'}
			],
			showFooter: true,
			totalServerItems: 0,
			selectedItems: $scope.selectedItems,
			multiSelect: false,
			enableColumnResize: true,
			rowHeight: 60
		};

		$scope.convertDate = function(date) {
			// return date.substring(0,10);
			return moment(date, 'YYYY-MM-DDTHH:mm:ss').format('YYYY-MM-DD');
		}

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

			iTunesSearchService.searchText = $scope.searchText;
			iTunesSearchService.searchType = $scope.searchType.type;
			iTunesSearchService.resultType = $scope.resultType.type;
			iTunesSearchService.limit = $scope.resultLimit;

			iTunesSearchService.get()
				.success(function(response){
					$log.info("We got " + response.resultCount + " results");
					$scope.results = iTunesSearchService.results = response.results;
					$scope.gridOptions.totalServerItems = $scope.results.length;
					$scope.gridOptions.selectedItems.length = 0;
				})
				.error(function(error){
					$log.error("There was an error getting your results: " + error);
				});
		}

	}
);
