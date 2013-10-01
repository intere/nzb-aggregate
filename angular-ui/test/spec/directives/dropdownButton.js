'use strict';

describe('Directive: dropdownButton', function () {
  beforeEach(module('nzbUiApp'));

  var element;

  it('should make hidden element visible', inject(function ($rootScope, $compile) {
    element = angular.element('<dropdown-button></dropdown-button>');
    element = $compile(element)($rootScope);
    expect(element.text()).toBe('this is the dropdownButton directive');
  }));
});
