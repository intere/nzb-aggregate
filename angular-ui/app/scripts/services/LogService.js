'use strict';

/**
 * @ngdoc service
 * @name nzbUiApp.LogService
 * @description Manages interactions with the Log REST API: /log
 */
angular.module('nzbUiApp')
  .service('LogService', function LogService($http, $q, ServerBase) {    

    var service = {
      getLog: function(lines) {
        var request = ServerBase + '/log';
        if(lines) {
          request += '?lines=' + lines;
        }

        var deferred = $q.defer();

        $http.get(request).success(
          function(data){
            deferred.resolve(data);
          }).error(
          function(error){
            deferred.reject(error);
          });

        return deferred.promise;
      }
    };

    return service;

  });
