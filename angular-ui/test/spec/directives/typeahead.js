'use strict';

describe('Directive: typeahead', function () {
  beforeEach(module('nzbUiApp'));

  var element;

  it('should make hidden element visible', inject(function ($rootScope, $compile) {
    element = angular.element('<typeahead></typeahead>');
    element = $compile(element)($rootScope);
    expect(element.text()).toBe('this is the typeahead directive');
  }));
});
