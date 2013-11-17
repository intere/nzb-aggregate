'use strict';

angular.module('nzbUiApp')
  .controller('QueueCtrl', function ($scope, $timeout, $log, QueueService) {

    var stop = undefined;

    $scope.getQueue = function() {
      QueueService.getQueue().then(
        function(data) {
          $scope.queue = data;
          stop = $timeout($scope.getQueue, 10000);
        }, function(error) {
          $log.error("Error getting queue: " + JSON.stringify(error));
          stop = $timeout($scope.getQueue, 10000);
        });
    }

    $scope.getQueue();

    $scope.$on('$destroy', function(){
      $timeout.cancel(stop);
    });
  });
