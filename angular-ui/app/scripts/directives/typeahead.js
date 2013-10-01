'use strict';

/**
 * directive: typeahead
 *   Purpose - provide 
 */
angular.module('nzbUiApp')
  .directive('typeahead', function ($compile) {
    return {
      template: '<div><input type="text" data-provide="typeahead" autocomplete="off" data-source="{{propose}}" ng-model="selected" updater="handleChange()" placeholder="{{placeholder}}">' +
      	'<div class="button-bar"><button ng-repeat="i in ngModel" ng-click="remove(i)" class="btn btn-inverse">{{i}}</button></div></div>',
      restrict: 'A',
      replace: true, 
      scope: {
      	propose: '=typeahead',
      	ngModel: '=ngModel',
            callback: '=onChange',
            placeholder: '=placeholder'
      },
      link: function postLink(scope, element, attrs) {

            if(!scope.ngModel) {
      		scope.ngModel = [];
      	}

      	/** Forces a render cycle.  */
      	scope.forceUpdate = function() {
      		if(!scope.$$phase && !scope.$root.$$phase) {
      			scope.$root.$digest();
      		}
      	}

      	/** Pairs down the suggestion list (removes values already selected).  */
      	scope.filter = function() {
      		var used = {};
      		for(var i in scope.ngModel) {
      			used[scope.ngModel[i]] = scope.ngModel[i];
      		}

      		if(!scope.suggestions) {
      			scope.suggestions = [];
      		}
      		scope.suggestions.length = 0;

      		for(var i in scope.propose) {
      			if(!used[scope.propose[i]]) {
      				scope.suggestions[scope.suggestions.length] = scope.propose[i];
      			}
      		}
      	}

      	/** When a user clicks on a button that's an added value.  */
      	scope.remove = function(value) {
      		for(var i in scope.ngModel) {
      			if(scope.ngModel[i] == value) {
      				scope.ngModel.splice(i,1);
                              scope.updateAll();
      				break;
      			}
      		}
      	};

      	// Build the suggestion list to start:
      	scope.filter();

      	/** Tap into the bootstrap typeahead and tie into it.  */
      	$(element[0].childNodes[0]).typeahead({
      		source: scope.suggestions,
      		updater: function(item) {      			
      			scope.ngModel[scope.ngModel.length] = item;
                        scope.updateAll();
      			return undefined;
      		}
      	});

            scope.updateAll = function() {
                  scope.filter();
                  if(scope.callback) {
                       scope.callback();
                  }
                  scope.forceUpdate();                  
            }

      	/** Setup watches.  */


      	scope.$watch('propose', function() {
      		scope.updateAll();
      	});

      	scope.$watch('ngModel', function(){
      		scope.updateAll();
      	});

      }
    };
  });
