'use strict';

describe('Service: NzbService', function () {

  // load the service's module
  beforeEach(module('nzbUiApp'));

  // instantiate service
  var NzbService;
  beforeEach(inject(function (_NzbService_) {
    NzbService = _NzbService_;
  }));

  it('should do something', function () {
    expect(!!NzbService).toBe(true);
  });

});
