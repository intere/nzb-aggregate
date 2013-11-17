'use strict';

angular.module('nzbUiApp', [ 'ngGrid' ])
  .config([ '$routeProvider', function ($routeProvider) {
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
      .when('/log', {
        templateUrl: 'views/log.html',
        controller: 'LogCtrl'
      })
      .when('/queue', {
        templateUrl: 'views/queue.html',
        controller: 'QueueCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  }])

  .value( 'ServerBase', 'http://intere.servebeer.com:9090/nzb' )

  .config(['$httpProvider', function($httpProvider) {
    delete $httpProvider.defaults.headers.common["X-Requested-With"];
    $httpProvider.defaults.headers.post['Accept'] = "application/json";
  }])

  .controller('NavCtrl', [ '$scope', '$location', function ($scope, $location) {
    $scope.isView = function(name) {
      return new RegExp('/'+name+'$').test($location.path());
    }
  }])  
 
  .run([ '$rootScope', '$log', '$location', function($rootScope, $log, $location) {
    $rootScope.applicationName = "Music Search Utility";
    $log.info('application run: (path="' + $location.path() + '")' );

    $rootScope.hasShowHide = function() {
      return $rootScope.showHide;      
    }

    // When the route is about to change, remove the show/hide menu.
    $rootScope.$on('$locationChangeStart', function() {
      $rootScope.showHide = undefined;
    });

  }]);