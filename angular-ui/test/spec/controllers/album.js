'use strict';

describe('Controller: AlbumCtrl', function () {

  // load the controller's module
  beforeEach(module('angularUiApp'));

  var AlbumCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    AlbumCtrl = $controller('AlbumCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
