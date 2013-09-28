'use strict';

angular.module('nzbUiApp')
  .controller('MainCtrl', function ($scope) {
    $scope.navigation = [
        {display: "iTunes Search", href: "#/iTunesSearch"},
        {display: "NZB Search", href: "#/NzbSearch"}
    ];
  });
