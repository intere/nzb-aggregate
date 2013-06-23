'use strict';

angular.module( 'nzb.utils' )
	.controller( 'ShowAlbumCtrl', function($scope, $routeParams, $log, MusicSearchService) {

		$log.info("Params: " + JSON.stringify($routeParams));
		$scope.selectedItems =[];

		$scope.gridOptions = {
		    data: 'results',
		    columnDefs: [
		    	{field:'discNumber', displayName:'Disc #',width:'auto'},
		    	{field:'trackNumber', displayName:'Track #',width:'auto'},
			    {field:'trackName', displayName: 'Track Name',width:'auto'},
			    {field:'trackTimeMillis', displayName: 'Track Time (ms)',width:'auto',cellTemplate:'<span ng-cell-text>{{trackTime(COL_FIELD)}}</span>'},
			    {field:'previewUrl', displayName:'Preview',width:'auto',cellTemplate:'<a ng-href="{{COL_FIELD}}" target="_new">Listen</a>'}
			],
			showFooter: true,
			totalServerItems: 0,
			selectedItems: $scope.selectedItems,
			multiSelect: false,
			enableColumnResize: true
		};

		//
		// Convert milliseconds to HH:mm:ss
		//
		$scope.trackTime = function(ms) {
			var time = "";

			var secs = Math.floor(ms / 1000) % 60;
			var mins = Math.floor(ms / (1000 * 60)) % 60;
			var hrs = Math.floor(ms / (1000 * 60 * 60));

			if(hrs>0) {
				if(hrs<10) {
					time += '0';
				}
				time += hrs + ':';
			}
			if(mins>0) {
				if(time.length>0 && mins<10) {
					time += '0';
				}
				time += mins + ':';
			}
			if(secs<10) {
				time += '0';
			}
			time += secs;

			return time;
		}

		// When the page loads - go out and get the data we need
		if($routeParams.id) {
			MusicSearchService.getSongs($routeParams.id)
				.success(function(result){
					var data = [];
					for(var i in result.results) {
						if(result.results[i].wrapperType=='track') {
							data[data.length] = result.results[i];
							$scope.discs = result.results[i].discCount;
						} else if(result.results[i].wrapperType == 'collection') {
							$scope.collection = result.results[i];
						}
					}

					$scope.results = data;
					$scope.gridOptions.totalServerItems = data.length;
					$scope.gridOptions.selectedItems.length = 0;

				}).error(function(error){
					$log.error("Error getting album information for album with id: '" + $routeParams.id + "'");
				})
		} else {
			$log.error("Error, no ID provided in the route");
		}

	}
);