'use strict';

angular.module('nzbUiApp')
  .service('NzbSearchResultDecorator', function NzbSearchResultDecorator() {
    
  	var service = {
  		decorate: function(results) {
  			return new Decorator(results);
  		}
  	};

  	function Decorator(results) {
  		this.results = results;

  		this.getSize = function() {
  			var count = 0;
  			for(var i in results) {
  				count += results[i].nzbRows.length;
  			}
  			return count;
  		};

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

  		this.flattenResults = function() {
  			var flattened = [];

  			for(var i in this.results) {
  				for(var j in this.results[i].nzbRows) {
  					flattened[flattened.length] = this.results[i].nzbRows[j];
  				}
  			}

  			return flattened;
  		};

  		this.getMetdata = function(index) {
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
  	}

  	return service;

  });
