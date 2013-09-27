'use strict';

angular.module('angularUiApp')
  .controller('MainCtrl', function ($scope) {
    $scope.navigation = [
        {display: "iTunes Search", href: "#/iTunesSearch"},
        {display: "NZB Search", href: "#/nzb-search"}
    ];
  });
