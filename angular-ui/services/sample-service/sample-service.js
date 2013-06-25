
'use strict';

angular.module( 'nzb.utils' ).service('SampleService', 
	function($http, $log) {

		var service = {
			post: function(object) {
				$http(
					{
						method: 'POST',
						url: 'http://jsfiddle.net/echo/json',
						dataType: 'json',
						json: JSON.stringify(object),
					}
				).success(function(data) {
					$log.info("Success: " + JSON.stringify(data));
				}).error(function(error) {
					$log.error("Error posting: " + error);
				});
			}
		};

		return service;

});