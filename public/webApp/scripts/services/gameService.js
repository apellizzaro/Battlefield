'use strict';

angular.module('battleShipApp')
  .service('gameService', function gameService($http) {
    console.log('Inside Joint game Service');

        return {
                 getGameDetails : function(gameId, s,e){
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
                                       },
                                 headers: {
                                     'x-battlefield-userToken': gameConfig.playerToken
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

                   startGame: function (gameId, userToken, onSuccess, onError){
                         $http({
                             method: 'POST',
                             url: '/api/v1/game/'+gameId +'/start',
                             headers: {
                                 'x-battlefield-userToken': userToken
                                }
                             }).then(function s(r){onSuccess(r.data);}, function e(r) {onError(r.data);});
                   },

                   getPlayerBoards: function (gameId, playerToken, s, e){
                        $http({
                              method: 'GET',
                              url: '/api/v1/game/'+ gameId  + '/boards',
                              headers: {
                               'x-battlefield-userToken': playerToken
                              }
                            }).then(function successCallback(response) {s(response.data)},
                             function errorCallback(response) {e(response.data)} );
                         },

                   getNextPlayerTurn : function (gameId,s,e) {
                       $http({
                             method: 'GET',
                             url: '/api/v1/game/'+ gameId +'/nextturn'
                           }).then(function successCallback(response) {s(response.data)},
                            function errorCallback(response) {e(response.data)} );
                    },

                   shoot: function (gameId,playerToken,x,y, s, e) {
                         $http({
                             method: 'POST',
                             url: '/api/v1/game/'+ gameId +'/shoot',
                             data: {'x':x,'y':y},
                             headers: {
                               'x-battlefield-userToken': playerToken
                              }
                           }).then(function successCallback(response) {s(response.data)},
                            function errorCallback(response) {e(response.data)} );
                   },
                   doLogin: function (playerName, s,e) {
                        $http({
                             method: 'POST',
                             url: '/api/v1/user/login',
                             data: {'name' : playerName}
                           }).then(function successCallback(response) {s(response.data)},
                            function errorCallback(response) {e(response.data)} );
                   }
             };
  });