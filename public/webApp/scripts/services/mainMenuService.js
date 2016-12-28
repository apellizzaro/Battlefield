'use strict';

angular.module('battleShipApp')
  .service('mainMenuService', function mainMenuService($http) {

    // AngularJS will instantiate a singleton by calling "new" on this function

    console.log('Inside New Game Service');
    return {
             getGamesStatus : function(s,e){
                console.log('Inside getGamesStatus');

                $http({
                  method: 'GET',
                  url: '/api/v1/gameStats'
                }).then(function successCallback(response) {s(response)},
                 function errorCallback(response) {e(response)} );
             }




         };

  });
