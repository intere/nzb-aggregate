'use strict';

describe('Controller: ItunessearchCtrl', function () {

  // load the controller's module
  beforeEach(module('angularUiApp'));

  var ItunessearchCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    ItunessearchCtrl = $controller('ItunessearchCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
