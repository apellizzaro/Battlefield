'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:NewgameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('MainMenuCtrl', function ($scope,$location,mainMenuService) {
    console.log('in the MainMenuCtrl controller');

    //initialize with some stats
    var gameStatus = mainMenuService.getGamesStatus(function success(response) {
            $scope.inProgressGame = response.data.inProgressGames;
            $scope.formingGames = response.data.waitingForPlayers;
    }, function onError(s) {});


    $scope.Navigate = function (page) {
        $location.path(page)
    }
  });
