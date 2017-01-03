'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:NewgameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('MainMenuCtrl', function ($scope,$location,gameService,userContext) {
    console.log('in the MainMenuCtrl controller');
    if (userContext.PlayerName)
        $scope.playerName = userContext.PlayerName;

    //initialize with some stats
    gameService.getGamesStatus ( function success (r){
        $scope.gamesStatus = r;
        $scope.formingGames = r.filter(function (e){return e.status=='GameSettingUp';}).length;
        $scope.inProgressGame = r.length -$scope.formingGames;
        },
         function error(e) {});


    $scope.joinGame = function (gameId) {
        console.log ("joining game:" + gameId);
        userContext.gameId=gameId;
        $scope.Navigate("joinGame");
    }

    $scope.startGame = function (gameId) {
        userContext.gameId=gameId;
        gameService.startGame (gameId, userContext.playerToken, function s(d) {
        console.log(d);
        $scope.Navigate ("playGame");},
            function err (e) {
                console.log (e);
            });
    }

    $scope.playGame = function (gameId) {
        userContext.gameId=gameId;
        $scope.Navigate ("playGame");
    }

    $scope.Navigate = function (page) {
        $location.path(page)
    }
  });
