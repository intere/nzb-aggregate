'use strict';

angular.module( 'nzb.utils', [ 'ui.bootstrap', 'ngGrid' ])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {templateUrl: 'views/home/home.html'})
            .when('/search', {templateUrl: 'views/search/music-search.html'})
            // .when('/dashboard', {templateUrl: 'dashboard/dashboard.html'})
            // .when('/articles/:articleId/edit/', {templateUrl: 'articles/create.html'})
            // .when('/articles/new/:flag', {templateUrl: 'articles/create.html'})
            // .when('/events/create/:flag/:editFlag', {templateUrl: 'events/create.html'})
            .otherwise({redirectTo: '/'});
    }])    
    .config(['$httpProvider', function($httpProvider) {
            delete $httpProvider.defaults.headers.common["X-Requested-With"];
    }])
    .controller({
            DummyCtrl: function($scope, $route) {
                    $scope.name = $route.current.name;
            },
            NavCtrl: function ($scope, $location) {
                    $scope.isView = function(name) {
                            return new RegExp('/'+name+'$').test($location.path());
                    }
            }

    })
    .run(function($rootScope, $log, $location) {
        $log.info('application run: (path="' + $location.path() + '")' );

    });