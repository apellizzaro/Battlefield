'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:NewgameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('MainMenuCtrl', function ($scope,$location,gameService,currGame) {
    console.log('in the MainMenuCtrl controller');

    //initialize with some stats
    gameService.getGamesStatus ( function success (r){
        $scope.gamesStatus = r;
        $scope.formingGames = r.filter(function (e){return e.status=='GameSettingUp';}).length;
        $scope.inProgressGame = r.length -$scope.formingGames;
        },
         function error(e) {});


    $scope.joinGame = function (gameId) {
        console.log ("joining game:" + gameId);
        //gameService.currentGameId = gameId;
        currGame.gameId=gameId;
        //gameService.setCurrentGameId(gameId);
        $location.path("joinGame");
    }

    $scope.Navigate = function (page) {
        $location.path(page)
    }
  });
