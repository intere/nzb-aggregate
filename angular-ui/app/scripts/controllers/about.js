'use strict';

angular.module('nzbUiApp')
  .controller('AboutCtrl', function($scope){
	$scope.views = [
		{
			name: "iTunes Search",
			about: "Allows you to search iTunes by Artist, Composer, Album, Song.  You can return your results by Album or by Song"
		},
		{
			name: "NZB Search",
			about: "Allows you to execute a search against NZBs and allows you to queue up an NZB"
		}
	];
});
