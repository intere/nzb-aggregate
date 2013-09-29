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
    expect(DECORATED.addPost).not.toBeUndefined();
    expect(DECORATED.getPostDto).not.toBeUndefined();
    expect(DECORATED.hasPosts).not.toBeUndefined();
    expect(DECORATED.getUniqueGroups).not.toBeUndefined();
    expect(DECORATED.getUniquePosters).not.toBeUndefined();
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

  it('should not add undefined, null or other invalid objects to posts', function() {
    expect(DECORATED.addPost()).toBe(false);
    expect(DECORATED.addPost(null)).toBe(false);
    expect(DECORATED.addPost({})).toBe(false);
  });

  it('should add posts when we ask it to', function() {

    // Ensure the indexes 0 - 9 get added to the first result
    for(var i=0; i<10;i++) {
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(true);
      expect(DECORATED.results[0].posts[i]).toBe(toAdd);
    }

    // Ensure indexes 248-257 get added to the second result
    for(var i=248;i<258;i++) {
      var j=i-248;
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(true);
      expect(DECORATED.results[1].posts[j]).toBe(toAdd);
    }
  });

  it('should not add duplicate posts even if we ask it to', function() {
    // Ensure the indexes 0 - 9 get added to the first result
    for(var i=0; i<10;i++) {
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(true);
      expect(DECORATED.results[0].posts[i]).toBe(toAdd);
    }

    for(var i=0; i<10;i++) {
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(false);
      expect(DECORATED.results[0].posts[i]).toBe(toAdd);
      expect(DECORATED.results[0].posts.length).toBe(10);
    }


    // Ensure indexes 248-257 get added to the second result
    for(var i=248;i<258;i++) {
      var j=i-248;
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(true);
      expect(DECORATED.results[1].posts[j]).toBe(toAdd);
    }

    for(var i=248;i<258;i++) {
      var j=i-248;
      var toAdd = DECORATED.get(i);
      expect(DECORATED.addPost(toAdd)).toBe(false);
      expect(DECORATED.results[1].posts[j]).toBe(toAdd);
      expect(DECORATED.results[1].posts.length).toBe(10);
    }
  });

  it('should create a proper DTO for sending', function(){
    var indexes = [0, 200, 247, 248, 400, 495];

    for(var i=0;i<indexes.length;i++) {
      expect(DECORATED.addPost(DECORATED.get(indexes[i]))).toBe(true);
    }

    var postDto = DECORATED.getPostDto();
    expect(postDto).not.toBeUndefined();
    expect(postDto.length).toBe(2);
    for(var i in postDto) {
      expect(postDto[i].posts.length).toBe(3);
    }
  });

  it('should tell us if it has posts or not', function() {
    expect(DECORATED.hasPosts()).toBe(false);

    var indexes = [0, 200, 247, 248, 400, 495];

    for(var i=0;i<indexes.length;i++) {
      expect(DECORATED.addPost(DECORATED.get(indexes[i]))).toBe(true);
      expect(DECORATED.hasPosts()).toBe(true);
    }
  });

  it('should provide the unique set of groups', function() {
    var flat = DECORATED.flattenResults();
    var groups = DECORATED.getUniqueGroups();

    expect(groups).not.toBeUndefined();
    expect(groups.length).toBeGreaterThan(0);

    console.log('we have ' + groups.length + " groups");

    var unique = {};
    for(var i in groups) {
      expect(unique[groups[i]]).toBeUndefined();
      unique[groups[i]] = groups[i];
    }

  });

  it('should provide the unique set of posters', function() {
    var flat = DECORATED.flattenResults();
    var posters = DECORATED.getUniquePosters();

    expect(posters).not.toBeUndefined();
    expect(posters.length).toBeGreaterThan(0);

    var unique = {};
    for(var i in posters) {
      expect(unique[posters[i]]).toBeUndefined();
      unique[posters[i]] = posters[i];
    }
    
  });

});
