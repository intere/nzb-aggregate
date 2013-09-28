'use strict';

describe('Service: iTunesSearchService', function () {

  // load the service's module
  beforeEach(module('nzbUiApp'));

  // instantiate service
  var iTunesSearchService;
  beforeEach(inject(function (_iTunesSearchService_) {
    iTunesSearchService = _iTunesSearchService_;
  }));

  it('should do something', function () {
    expect(!!iTunesSearchService).toBe(true);
  });

});
