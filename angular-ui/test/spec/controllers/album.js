'use strict';

describe('Controller: AlbumCtrl', function () {

  // load the controller's module
  beforeEach(module('nzbUiApp'));

  var AlbumCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    AlbumCtrl = $controller('AlbumCtrl', {
      $scope: scope
    });
  }));

});
