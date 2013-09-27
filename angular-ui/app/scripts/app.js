'use strict';

angular.module('angularUiApp', [ 'ngGrid' ])
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
      .otherwise({
        redirectTo: '/'
      });
  });
