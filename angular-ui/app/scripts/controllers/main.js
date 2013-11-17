'use strict';

angular.module('nzbUiApp')
  .controller('MainCtrl', function ($scope) {
    $scope.search = [
        {display: "iTunes Search", href: "#/iTunesSearch"},
        {display: "NZB Search", href: "#/NzbSearch"}
    ];

    $scope.nzb = [
      {display: "Nzb Queue", href: "#/queue"},
      {display: "Log File", href: "#/log"},
    ];
  });
