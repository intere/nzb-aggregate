'use strict';

angular.module('nzbUiApp')
  .directive('dropdownButton', function () {
    return {
      template: '<div class="btn-group"><button class="btn dropdown-toggle" data-toggle="dropdown">{{label}} <span class="caret"></span></button>'
      	+ '<ul class="dropdown-menu"><li ng-repeat="i in menu"><a ng-click="toggle(i)"><i class="icon-ok" ng-hide="!isSelected(i)"></i> {{i}}</a></li></ul>'      	
      	+ '</div>',
      restrict: 'A',
      replace: true,
      scope: {
      	label: '=dropdownButton',
      	menu: '=menu',
      	ngModel: '=ngModel',
      	callback: '=onChange'
      },
      link: function postLink(scope, element, attrs) {     	

      	/** Forces a render cycle.  */
      	scope.forceUpdate = function() {
      		if(!scope.$$phase && !scope.$root.$$phase) {
      			scope.$root.$digest();
      		}
      	};

      	scope.isSelected = function(item) {
      		return scope.used[item]	;
      	}

      	/** Pairs down the suggestion list (removes values already selected).  */
      	scope.filter = function() {
      		scope.used = {};
      		for(var i in scope.ngModel) {
      			scope.used[scope.ngModel[i]] = scope.ngModel[i];
      		}

      		if(!scope.suggestions) {
      			scope.suggestions = [];
      		}
      		scope.suggestions.length = 0;

      		for(var i in scope.propose) {
      			if(!scope.used[scope.propose[i]]) {
      				scope.suggestions[scope.suggestions.length] = scope.propose[i];
      			}
      		}
      	};

      	/** When a user clicks on a button that's an added value.  */
      	scope.remove = function(value) {
      		for(var i in scope.ngModel) {
      			if(scope.ngModel[i] == value) {
      				scope.ngModel.splice(i,1);
      				break;
      			}
      		}
      	};

      	scope.toggle = function(item) {
      		if(scope.isSelected(item)) {
      			scope.remove(item);
      		} else {
      			scope.add(item)
      		}
      		scope.updateAll();
      	}

      	scope.add = function(item) {
  			scope.ngModel[scope.ngModel.length] = item;
      	}

      	// Build the suggestion list to start:
      	scope.filter();

      	scope.updateAll = function() {
			scope.filter();
			if(scope.callback) {
				scope.callback();
			}
			scope.forceUpdate();                  
        }

        scope.$watch('menu', function() {
        	scope.updateAll();
        });

        scope.$watch('ngModel', function() {
        	scope.updateAll();
        });

      }
    };
  });
