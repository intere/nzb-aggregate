'use strict';

angular.module( 'nzb.utils', [ 'ui.bootstrap', 'ngGrid' ])

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/', {templateUrl: 'views/home/home.html'})
            .when('/search/itunes', {templateUrl: 'views/search/music-search.html'})
            .when('/album/:id', {templateUrl: 'views/album/show-album.html'})
            .when('/about', {templateUrl: 'views/about/about.html'})
            .when('/search/nzb', {templateUrl:'views/search/nzb-search.html'})
            // .when('/dashboard', {templateUrl: 'dashboard/dashboard.html'})
            // .when('/articles/:articleId/edit/', {templateUrl: 'articles/create.html'})
            // .when('/articles/new/:flag', {templateUrl: 'articles/create.html'})
            // .when('/events/create/:flag/:editFlag', {templateUrl: 'events/create.html'})
            .otherwise({redirectTo: '/'});
    }])    
    .config(['$httpProvider', function($httpProvider) {
            delete $httpProvider.defaults.headers.common["X-Requested-With"];
    }])
    .controller('NavCtrl', function ($scope, $location) {
        $scope.isView = function(name) {
                return new RegExp('/'+name+'$').test($location.path());
        }
    })
    .controller('MainCtrl', function($scope){
        $scope.navigation = [
            {display: "iTunes Search", href: "#/search/itunes"},
            {display: "NZB Search", href: "#/search/nzb"}
        ];
    })
    .run(function($rootScope, $log, $location, SampleService) {
        $rootScope.applicationName = "Music Search Utility";
        $log.info('application run: (path="' + $location.path() + '")' );

        SampleService.post({name:"Eric", age: 33, mug: "Piglet", littleBrother: "Klondyke", bigBrother: "Poodull"});
    });