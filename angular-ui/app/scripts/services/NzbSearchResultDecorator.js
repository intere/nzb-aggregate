'use strict';

angular.module('nzbUiApp')
  .service('NzbSearchResultDecorator', function NzbSearchResultDecorator() {
    
    //
    // Service Implementation (delegates to Decorator class)
    //
  	var service = {
  		decorate: function(results) {
  			return new Decorator(results);
  		}
  	};

    /** Decorator Class.  */
  	function Decorator(results) {
  		this.results = results;

      /** Gets you the size of the result set (as if it were flat). */
  		this.getSize = function() {
  			var count = 0;
  			for(var i in results) {
  				count += results[i].nzbRows.length;
  			}
  			return count;
  		};

      /** Gets you a single NZB Row by index (acts like a flat array).  */
  		this.get = function(index) {
  			if(index===undefined || index < 0 || index >= this.getSize()) {
  				throw new Error('Invalid index: ' + index);
  			}

  			var count = 0;
  			for(var i in this.results) {
  				var calcIndex = index - count;
  				if(calcIndex<this.results[i].nzbRows.length) {
  					return this.results[i].nzbRows[calcIndex];
  				}
  				count += this.results[i].nzbRows.length;
  			}
  		};

      /** Hands you back a raw array of all of the results.  */
  		this.flattenResults = function() {
  			var flattened = [];

  			for(var i in this.results) {
  				for(var j in this.results[i].nzbRows) {
  					flattened[flattened.length] = this.results[i].nzbRows[j];
  				}
  			}

  			return flattened;
  		};

      /** Adds an NZB Result to the posting list. */
      this.addPost = function(objToAdd) {
        if(objToAdd!==undefined) {
          for(var i in this.results) {
            for(var j in this.results[i].nzbRows) {
              if(this.results[i].nzbRows[j]==objToAdd) {
                return addUnique(this.results[i].posts, objToAdd);
              }
            }
          }
        }

        return false;
      }

      /** Tells you if the collection has any posts or not.  */
      this.hasPosts = function() {
        for(var i in this.results) {
          if(this.results[i].posts.length>0) {
            return true;
          }
        }

        return false;
      }

      /** Gets you all of the posts as a flat array.  */
      this.getPosts = function() {
        var posts = [];

        for(var i in this.results) {
          for(var j in this.results[i].posts) {
            posts[posts.length] = this.results[i].posts[j];
          }
        }

        return posts;
      }

      /** Gets you the metadata associated with the index.  */
  		this.getMetadata = function(index) {
  			if(index===undefined || index < 0 || index >= this.getSize()) {
  				throw new Error('Invalid index: ' + index);
  			}

  			var count = 0;
  			for(var i in this.results) {
  				var calcIndex = index - count;
  				if(calcIndex<this.results[i].nzbRows.length) {
  					return {
  						action: this.results[i].action,
  						searchText: this.results[i].searchText
  					};
  				}
  				count += this.results[i].nzbRows.length;
  			}
  		}

      /** Takes the post model and converts it to a proper DTO to send.  */
      this.getPostDto = function() {
        var dtos = [];
        for(var i in this.results) {
          if(this.results[i].posts.length>0) {
            var posts = [];
            for(var j in this.results[i].posts) {
              posts[j] = {
                id: this.results[i].posts[j].id,
                checkboxName: this.results[i].posts[j].checkboxName
              }
            }
            dtos[dtos.length] = {
              action: this.results[i].action,
              posts: posts,
              searchText: this.results[i].searchText,
              parameters: this.results[i].parameters
            }
          }
        }

        return dtos;
      }

      /** Provides you with the unique set of groupss. */
      this.getUniqueGroups = function() {
        var flat = this.flattenResults();
        var groups = [];

        // Object to use as a hash to ensure we don't dupe any values:
        var unique = {};

        for(var i in flat) {
          if(!unique[flat[i].group]) {
            groups[groups.length] = flat[i].group;
            unique[flat[i].group] = flat[i].group;
          }
        }

        return groups;
      }

      /** Provides you with the unique set of posters. */
      this.getUniquePosters = function() {
        var flat = this.flattenResults();
        var posters = [];
        var unique = {};

        for(var i in flat) {
          if(!unique[flat[i].poster]) {
            posters[posters.length] = flat[i].poster;
            unique[flat[i].poster] = flat[i].poster;
          }
        }

        return posters;
      }

  	};

    //
    // HELPER FUNCTIONS
    //

    /** Helper function - adds the object to the array if it's not already in the array.  */
    function addUnique(array, object) {
      for(var i in array) {
        if(array[i]==object) {
          return false;
        }
      }

      array[array.length] = object;
      return true;
    }

    // Return the service.
  	return service;

  });
