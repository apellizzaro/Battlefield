'use strict';

/**
 * @ngdoc service
 * @name battleShipApp.newGameService
 * @description
 * # newGameService
 * Service in the battleShipApp.
 */
angular.module('battleShipApp')
  .service('newGameService', function newGameService($http) {
    // AngularJS will instantiate a singleton by calling "new" on this function
    console.log('Inside New Game Service');
    return {
             crateNewGame : function(gameConfig, onSuccess,onError){
                console.log('Inside crateNewGame');
                console.log('======================');

                $http({
                      method: 'PUT',
                      url: '/api/v1/newGame',
                      params: {'name':gameConfig.gameName,
                                'boardsize':gameConfig.gridSize
                                },
                      data: {'shipsConfiguration':{'config':[
                                    {'length': 4, 'quantity':gameConfig.nShips4},
                                    {'length': 3, 'quantity':gameConfig.nShips3},
                                    {'length': 2, 'quantity':gameConfig.nShips2},
                                    {'length': 1, 'quantity':gameConfig.nShips1},
                                ]},playersSetup:{}
                            }
                    }).then(function s(r){onSuccess(r.data);}, function e(r) {onError(r.data);});
             }
         };

  });
