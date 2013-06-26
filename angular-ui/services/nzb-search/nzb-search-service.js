'use strict';

//
// You search using an object like the following sample object:
//
// var objSearch = {for: 'search text', maxResults: 2500, startIndex: 250 };
// NzbSearchService.search(objSearch, fnSuccess, fnError);
//
angular.module( 'nzb.utils' ).service('NzbSearchService', 
	function(NzbUrlBase, $http, $log) {


		var service = {
			search : function(searchCriteria, fnSuccess, fnError) {

				if(!searchCriteria||!searchCriteria.for) {
					$log.warn("Bailing - you didn't provide search criteria");
					return;
				}

				var url = NzbUrlBase + "/search?for="

			}
		};

		return service;
	}
);