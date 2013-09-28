'use strict';

angular.module('nzbUiApp')
  .service('NzbService', function NzbService($http, $q, $log, ServerBase, NzbSearchResultDecorator) {

  	var service = {

  		doSearch: function(searchFor, maxResults, startIndex) {
  			if(!searchFor) {
  				throw new Error('You have to provide something to search for');
  			} else if(!maxResults) {
  				throw new Error('You have to provide a maximum number of results');
  			}

  			var url = ServerBase + '/search?for=' + searchFor + "&maxResults=" + maxResults;
  			if(startIndex) {
  				url += "&startIndex=" + startIndex;
  			}

  			var deferred = $q.defer();

  			$http.get(url).success(function(data) {
  				deferred.resolve(NzbSearchResultDecorator.decorate(data));
  			}).error(function(error) {
  				deferred.reject(error);
  			});

  			return deferred.promise;
  		}

  	};


  	return service;
    
  });
