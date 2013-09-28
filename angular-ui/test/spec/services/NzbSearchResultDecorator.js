'use strict';

describe('Service: NzbSearchResultDecorator', function () {

  // load the service's module
  beforeEach(module('nzbUiApp'));

  // instantiate service
  var NzbSearchResultDecorator;
  var TestDataService;
  var TEST_DATA;
  var DECORATED;
  var EXPECTED_SIZE = 496;

  beforeEach(inject(function (_NzbSearchResultDecorator_, _TestDataService_) {
    NzbSearchResultDecorator = _NzbSearchResultDecorator_;
    TestDataService = _TestDataService_;
    TEST_DATA = TestDataService.getSampleResponse(0);
    DECORATED = NzbSearchResultDecorator.decorate(TEST_DATA);
  }));

  it('should do something', function () {
    expect(!!NzbSearchResultDecorator).toBe(true);
    expect(!!TestDataService).toBe(true);
    expect(TEST_DATA).not.toBeUndefined();
  });

  it('should have some methods', function() {
    expect(DECORATED.getSize).not.toBeUndefined();
    expect(DECORATED.get).not.toBeUndefined();
    expect(DECORATED.flattenResults).not.toBeUndefined();
    expect(DECORATED.getMetadata).not.toBeUndefined();
  });

  it('should calculate result size correctly', function() {
    expect(DECORATED.getSize()).toBe(EXPECTED_SIZE);
  });

  it('should handle bad data', function() {
    try {
      DECORATED.get(-1);
      expect(false).toBe(true);
    } catch(ex) { /** This is expected.  */ };

    try {
      DECORATED.get();
      expect(false).toBe(true);
    } catch(ex) { /** This is expected.  */ };

    try {
      DECORATED.get(EXPECTED_SIZE);
      expect(false).toBe(true);
    } catch(ex) { /** This is expected.  */ };

  });

  it('should get results correctly', function() {
    expect(DECORATED.get(0)).toBe(TEST_DATA[0].nzbRows[0]);
    expect(DECORATED.get(247)).toBe(TEST_DATA[0].nzbRows[247]);
    expect(DECORATED.get(248)).toBe(TEST_DATA[1].nzbRows[0]);
    expect(DECORATED.get(400)).toBe(TEST_DATA[1].nzbRows[152]);
    expect(DECORATED.get(495)).toBe(TEST_DATA[1].nzbRows[247]);
  });

  it('should flatten data correctly', function() {
    var flattened = DECORATED.flattenResults();

    for(var i=0; i<EXPECTED_SIZE; i++) {
      expect(flattened[i]).toBe(DECORATED.get(i));
    }

  });

});
