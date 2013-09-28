'use strict';

angular.module('nzbUiApp')
  .service('iTunesSearchService', function($http, $log) {

	// Create the Service
	var service = {

		searchText: undefined,
		searchType: undefined,
		resultType: undefined,
		limit: undefined,
		results: [],

		get: function() {
			var url = 'https://itunes.apple.com/search' 
				+ '?term=' + this.searchText
				+ (this.searchType ? '&attribute=' + this.searchType : "")
				+ (this.resultType ? '&entity=' + this.resultType : "")
				+ (this.limit ? "&limit=" + this.limit: "")
				+ '&callback=JSON_CALLBACK';

			$log.info("Executing Query: '" + url + "'");

			return $http.jsonp(url);
		},

		getSongs: function(albumId) {
			if(!albumId) {
				$log.error("You must provide an Album ID: bad Programmer!");
				return undefined;
			}

			var url = "https://itunes.apple.com/lookup?id=" 
				+ albumId + "&entity=song"
				+ '&callback=JSON_CALLBACK';


			$log.info("Executing Query: '" + url + "'");

			return $http.jsonp(url);
		}
	};


	// Return the service instance:
	return service;
});
