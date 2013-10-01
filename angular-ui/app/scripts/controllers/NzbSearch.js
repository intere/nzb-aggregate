'use strict';

angular.module('nzbUiApp')
  .controller('NzbSearchCtrl', function ($scope, $log, $filter, NzbService) {

    $scope.searchOptions = {label: 'Search Options',checked: true,isChecked: function() {return this.checked;},
      callback: function() {
        this.checked = !this.checked;
        $scope.forceUpdate();
      }
    };
    $scope.filterOptions = {label: 'Filter Options',checked: true,isChecked: function() {return this.checked;},
      callback: function() {
        this.checked = !this.checked;
        $scope.forceUpdate();
      }
    };
    $scope.searchResults = {label:'Search Results',checked: true,isChecked: function() {return this.checked;}, 
      callback: function() {
        this.checked = !this.checked;
        $scope.queueResults.checked = !this.checked;
        $scope.forceUpdate();
      }
    };
    $scope.queueResults = {label:'Queue Results',checked: false,isChecked: function() {return this.checked;}, 
      callback: function() {
        this.checked = !this.checked;
        $scope.searchResults.checked = !this.checked;
        $scope.forceUpdate();
      }
    };

    $scope.submitDownload = function() {
      $scope.loading = true;
      var dto = $scope.decoratedResults.getPostDto();
      $log.info("submitting DTO:");
      console.dir(dto);
      
      NzbService.submitDownload(dto).then(
        function(result){
          $scope.loading = false;
          console.dir(result);
        },function(error){
          $scope.loading = false;
          $log.error("Error: " + JSON.stringify(error));
        });
    }

    $scope.$root.showHide = [
      $scope.searchOptions,
      $scope.filterOptions,
      $scope.searchResults,
      $scope.queueResults
    ];

  	$scope.maxResults = 100;
  	$scope.selectedItems = [];
    $scope.selectedGroups = [];
    $scope.selectedPosters = [];
  	$scope.results = [];
  	$scope.decoratedResults = undefined;
  	$scope.loading = false;  	
    $scope.added = {};
  	$scope.filters = {
  		filterText: undefined,
  		useExternalFilter: true
  	};

  // Grid Options:
	$scope.searchGridOptions = {
	    data: 'filteredResults',
	    columnDefs: [
	    	{field:'id',displayName:'ID',width:35},
	    	{field:'collection',displayName:'',cellClass:'collection',cellTemplate:'<i class="icon-th" ng-show="COL_FIELD"></i>',width:25},
		    {field:'subject', displayName:'Subject', width:'40%'},
		    {field:'group', displayName:'Group',width:'*'},
		    {field:'poster', displayName:'Poster',width:'*'},
		    {field:'contains', displayName:'Contents',width:'*'},
		    {field:'size', displayName: 'Size', cellTemplate: '<div ng-cell-text>{{COL_FIELD.size}} {{COL_FIELD.unit}}</div>',width:'*'}
		],
		showFooter: true,
		totalServerItems: 0,
		selectedItems: $scope.selectedItems,
		multiSelect: true,
		enableColumnResize: true,
		filterOptions: $scope.filters,
		rowHeight: 60
	};

  $scope.queueGridOptions = $.extend({}, $scope.searchGridOptions);
  $scope.queueGridOptions.data = 'postItems';
  $scope.queueGridOptions.filterOptions = undefined;


	$scope.hasResults = function() {
		return $scope.results.length>0;
	}

	$scope.canAdd = function() {
		return $scope.selectedItems.length>0;
	}

	$scope.canPost = function() {
		return $scope.decoratedResults && $scope.decoratedResults.hasPosts();
	}

	$scope.addSelected = function() {
		for(var i in $scope.selectedItems) {
			$scope.decoratedResults.addPost($scope.selectedItems[i]);
		}

		for(var i in $scope.selectedItems) {
			for(var j in $scope.results) {
				if($scope.selectedItems[i]==$scope.results[j]) {
					$scope.results.splice(j,1);
					break;
				}
			}
		}
		$scope.selectedItems.length = 0;
    $scope.postItems = $scope.decoratedResults.getPosts();
    $scope.added = {};
    for(var i in $scope.postItems) {
      $scope.added[$scope.postItems[i].id] = $scope.postItems[i];
    }
    $scope.updateFilters();
	}

  	$scope.getMaxResults = function() {
  		return [50, 100, 250, 500, 1000, 2500, 5000];
  	};

  	$scope.nzbSearch = function() {
  		$scope.loading = true;
  		NzbService.doSearch($scope.searchText, $scope.maxResults, $scope.startIndex).then(
  			function(results){
  				$scope.selectedItems.length = 0;
  				$scope.decoratedResults = results;
  				$scope.loading = false;
          console.dir($scope.decoratedResults);
  			}, function(error) {
  				throw new Error(error);
  				$scope.loading = false;
  			}
  		);
  	};

    /** Applies the filtering function per row. */
  	$scope.myFilter = function(row) {

      // First - check to see if it's added already:
      if($scope.added[row.id]) {
        return false;
      }

  		
      // Check collection based filtering
      if($scope.showCollections && !row.collection) {
  			return false;
  		}

      // Check text based filtering
      if($scope.filterText && $scope.filterText.length>0 
  			&& row.subject.toLowerCase().indexOf($scope.filterText.toLowerCase())<0) {
  			return false;
  		}

      // Check group-based filtering
      if($scope.selectedGroups.length) {
        var found = false;
        for(var i in $scope.selectedGroups) {
          if(row.group==$scope.selectedGroups[i]) {
            found = true;
            break;
          }
        }
        if(!found) {
          return false;
        }        
      }

      // check poster-based filtering.
      if($scope.selectedPosters.length) {
        var found = false;
        for(var i in $scope.selectedPosters) {
          if(row.poster==$scope.selectedPosters[i]) {
            found = true;
            break;
          }
        }
        if(!found) {
          return false;
        }
      }

  		return true;
  	}

    /** Handles a change in selection of a filter.  */
    $scope.handleSelectionChange = function() {
      if($scope.selectedGroups) {
        $scope.updateFilters();
      }
    }

    /** Performs the filtering on the results using the filter function. */
  	$scope.updateFilters = function() {
  		$scope.filteredResults = $filter('filter')($scope.results, $scope.myFilter);
  		$scope.forceUpdate();
  	};

    /** if the decorated results are updated, then update the results, groups and posters.  */
    $scope.$watch('decoratedResults', function() {
      if($scope.decoratedResults) {
        $scope.results = $scope.decoratedResults.flattenResults();

        $scope.selectedGroups = $scope.decoratedResults.getUniqueGroups();
        $scope.selectedPosters = $scope.decoratedResults.getUniquePosters();
        $scope.selectedItems.length = 0;

        $scope.groups = $scope.decoratedResults.getUniqueGroups();
        $scope.posters = $scope.decoratedResults.getUniquePosters();
      }
    });

  	$scope.$watch('results', function() {
  		$scope.updateFilters();
  	});

  	$scope.$watch('filterText', function() {
  		$scope.updateFilters();
  	});

  	$scope.$watch('showCollections', function() {
  		$scope.updateFilters();
  	});

    $scope.forceUpdate = function() {
      if(!$scope.$$phase && !$scope.$root.$$phase) {
        $scope.$root.$apply();
      }
    }

  });
