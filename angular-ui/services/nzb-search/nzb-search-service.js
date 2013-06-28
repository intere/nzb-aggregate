'use strict';

//
// You search using an object like the following sample object:
//
// var objSearch = {for: 'search text', maxResults: 2500, startIndex: 250 };
// NzbSearchService.search(objSearch, fnSuccess, fnError);
//
angular.module( 'nzb.utils' ).service('NzbSearchService', 
	function(NzbUrlBase, $http, $q, $log) {


		var service = {
			search : function(searchCriteria, fnSuccess, fnError) {

				if(!searchCriteria||!searchCriteria.for) {
					$log.warn("Bailing - you didn't provide search criteria");
					return;
				}

				var url = NzbUrlBase + "/search" + this.buildRequest(searchCriteria);

				return this.get(url);
			},

			get: function(url) {
				var deferred = $q.defer();

				$http.get(url)
					.success(function(result){
						deferred.resolve(result);
					})
					.error(function(error) {
						deferred.reject(error);
					});

				return deferred.promise;
			},

			buildRequest: function(searchCriteria) {
				var request = '';

				for(var i in searchCriteria) {
					if(request.length==0) {
						request = '?';
					} else {
						request += '&';
					}

					request += i + '=' + searchCriteria[i];
				}

				return request;
			}
		};

		return service;
	}
);