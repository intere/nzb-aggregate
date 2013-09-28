'use strict';

describe('Controller: iTunesSearchCtrl', function () {

  // load the controller's module
  beforeEach(module('nzbUiApp'));

  var ItunessearchCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ItunessearchCtrl = $controller('iTunesSearchCtrl', {
      $scope: scope
    });
  }));

});
