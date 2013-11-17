'use strict';

angular.module('nzbUiApp')
  .controller('LogCtrl', function ($scope, $timeout, $log, LogService) {

    var stop = undefined;

    $scope.getLog = function() {
      LogService.getLog().then(
        function(data) {
          $scope.logLines = data;
          stop = $timeout($scope.getLog, 10000);
        }, function(error) {
          $log.error("Error getting log: " + JSON.stringify(error));
          stop = $timeout($scope.getLog, 10000);
        });
    }

    $scope.getLog();

    $scope.$on('$destroy', function(){
      $timeout.cancel(stop);
    });

  });
