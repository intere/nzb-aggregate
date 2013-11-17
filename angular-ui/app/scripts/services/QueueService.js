'use strict';

/**
 * @ngdoc service
 * @name nzbUiApp.QueueService
 * @description Manages interactions with the Queue REST API: /queue
 */
angular.module('nzbUiApp')
  .service('QueueService', function QueueService($http, $q, ServerBase) {
    
    var service = {
      getQueue: function() {
        var request = ServerBase + '/queue';

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
