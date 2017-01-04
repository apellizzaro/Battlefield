'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:NewgameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('NewgameCtrl', function ($scope,$location, gameService, userContext) {

    if (!userContext.PlayerName ){
        $location.path("login");
    }

    $scope.createGame = function(){
    	console.log('======================');
    	console.log("game name: " + $scope.game.name);
    	console.log("grid size: " + $scope.game.gridSize);
    	console.log("Ships of 4: " + $scope.game.nShip4);
    	console.log("Ships of 3: " + $scope.game.nShip3);
    	console.log("Ships of 2: " + $scope.game.nShip2);
    	console.log("Ships of 1: " + $scope.game.nShip1);
    	console.log('======================');

    	gameService.crateNewGame( {
    	'gameName': $scope.game.name,
    	'gridSize' : $scope.game.gridSize,
    	'playerToken': userContext.playerToken,
    	'nShips4' :$scope.game.nShip4,
    	'nShips3' :$scope.game.nShip3,
    	'nShips2' :$scope.game.nShip2,
    	'nShips1' :$scope.game.nShip1},
    	                    function(s) {$scope.newGameId=s;
    	                    $location.path ("mainMenu");},
    	                    function (e) {$scope.newGameId=e});
    };
  });
