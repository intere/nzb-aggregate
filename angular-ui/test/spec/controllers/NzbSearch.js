'use strict';

describe('Controller: NzbSearchCtrl', function () {

  // load the controller's module
  beforeEach(module('nzbUiApp'));

  var NzbsearchCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    NzbsearchCtrl = $controller('NzbSearchCtrl', {
      $scope: scope
    });
  }));
});
