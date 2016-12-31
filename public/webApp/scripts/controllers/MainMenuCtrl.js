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
    if (currGame.PlayerName)
        $scope.playerName = currGame.PlayerName;

    //initialize with some stats
    gameService.getGamesStatus ( function success (r){
        $scope.gamesStatus = r;
        $scope.formingGames = r.filter(function (e){return e.status=='GameSettingUp';}).length;
        $scope.inProgressGame = r.length -$scope.formingGames;
        },
         function error(e) {});


    $scope.joinGame = function (gameId) {
        console.log ("joining game:" + gameId);
        currGame.gameId=gameId;
        $scope.Navigate("joinGame");
    }

    $scope.startGame = function (gameId) {
        currGame.gameId=gameId;
        gameService.startGame (gameId, function s(d) {
        console.log(d);
        $scope.Navigate ("playGame");},
            function err (e) {
                console.log (e);
            });
    }

    $scope.playGame = function (gameId) {
        currGame.gameId=gameId;
        $scope.Navigate ("playGame");
    }

    $scope.Navigate = function (page) {
        currGame.PlayerName=$scope.playerName;
        $location.path(page)
    }
  });
