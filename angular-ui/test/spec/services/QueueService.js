'use strict';

describe('Service: QueueService', function () {

  // load the service's module
  beforeEach(module('nzbUiApp'));

  // instantiate service
  var QueueService;
  beforeEach(inject(function (_QueueService_) {
    QueueService = _QueueService_;
  }));

  it('should do something', function () {
    expect(!!QueueService).toBe(true);
  });

});
