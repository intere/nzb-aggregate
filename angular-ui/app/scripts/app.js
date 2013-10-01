'use strict';

angular.module('nzbUiApp', [ 'ngGrid' ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/iTunesSearch', {
        templateUrl: 'views/iTunesSearch.html',
        controller: 'iTunesSearchCtrl'
      })
      .when('/album/:id', {
        templateUrl: 'views/album.html',
        controller: 'AlbumCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html',
        controller: 'AboutCtrl'
      })
      .when('/NzbSearch', {
        templateUrl: 'views/NzbSearch.html',
        controller: 'NzbSearchCtrl'
      })
      .when('/test', {
        templateUrl: 'views/test.html',
        controller: 'TestCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  })

  .value( 'ServerBase', 'http://localhost:9090/rest' )

  .config(['$httpProvider', function($httpProvider) {
    delete $httpProvider.defaults.headers.common["X-Requested-With"];
  }])

  .controller('NavCtrl', function ($scope, $location) {
    $scope.isView = function(name) {
      return new RegExp('/'+name+'$').test($location.path());
    }
  })  
  .run(function($rootScope, $log, $location) {
    $rootScope.applicationName = "Music Search Utility";
    $log.info('application run: (path="' + $location.path() + '")' );
  });