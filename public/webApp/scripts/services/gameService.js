'use strict';

angular.module('battleShipApp')
  .service('gameService', function gameService($http) {
    console.log('Inside Joint game Service');

        return {
                 getGameDetails : function(gameId, s,e){
                    console.log ("gameService::getGameDetails:" + gameId);
                    $http({
                      method: 'GET',
                      url: '/api/v1/game/'+ gameId + '/details',
                    }).then(function successCallback(response) {s(response.data)},
                     function errorCallback(response) {e(response.data)} );
                 },

                  getGamesStatus : function(s,e){
                                 $http({
                                   method: 'GET',
                                   url: '/api/v1/gamesSummary'
                                 }).then(function successCallback(response) {s(response.data)},
                                  function errorCallback(response) {e(response.data)} );
                              },

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
                                },

                  addPlayer : function(playerConfig, onSuccess,onError){
                                                     console.log('Inside addPlayer');
                                                     console.log('======================');
                  /*
                   {"playerName" : [{'startPosition':{'x':1,'y':1},
                                     'endPosition':{'x':1,'y':1}
                                     }]
                    }
                  */
                                                     $http({
                                                           method: 'PUT',
                                                           url: '/api/v1/game/'+playerConfig.gameId +'/players',
                                                           data: playerConfig.players
                                                         }).then(function s(r){onSuccess(r.data);}, function e(r) {onError(r.data);});
                                                  },


             };
  });