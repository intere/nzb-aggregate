'use strict';

angular.module('nzbUiApp', [ 'ngGrid' ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {templateUrl: 'views/home.html'})
      .when('/iTunesSearch', {templateUrl: 'views/iTunesSearch.html', controller: 'iTunesSearchCtrl'})
      .when('/album/:id', {templateUrl: 'views/album.html'})
      .when('/about', {templateUrl: 'views/about.html'})
      .when('/NzbSearch', {templateUrl: 'views/NzbSearch.html',controller: 'NzbsearchCtrl'})
      .when('/NzbSearch/:searchTerm', {templateUrl: 'views/NzbSearch.html',controller: 'NzbsearchCtrl'})
      .otherwise({redirectTo: '/'});
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