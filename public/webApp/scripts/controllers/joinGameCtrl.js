'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:joinGameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('joinGameCtrl', function ($scope,$location,gameService,currGame) {
    console.log('in the joinGameCtrl controller');

    console.log ("gameId: " + currGame.gameId);

    gameService.getGameDetails(currGame.gameId,function success(game) {
        console.log (game.gameName);

        $scope.gameName = game.gameName;
        $scope.gridSize = game.boardSize;
        $scope.numberOfPlayers = game.players.length;
        $scope.gameConfig = game.shipsConfiguration.config;

         $scope.grid = new Array(game.boardSize);
             for (var i=0; i<game.boardSize; i++) {
                $scope.grid[i] = new Array(game.boardSize);
                for (var j =0; j<game.boardSize; j++) {
                    $scope.grid[i][j] ='.'}
             }


    },
    function error(e) {
        console.log (e);});


    $scope.Navigate = function (page) {
        $location.path(page)
    }

    $scope.cellClicked = function (r,c) {
        console.log (r + " - " + c);
        if  ($scope.grid[r][c]=='.')
            $scope.grid[r][c]='X';
        else
            $scope.grid[r][c]='.';
    }

    $scope.verify = function () {
        var gs = $scope.gridSize;
        var newG = new Array(gs);
        for (var i=0; i<$scope.gridSize; i++)
            newG[i] = new Array(gs);

        for (var i=0; i<gs; i++) {
            for (var j=0;j<gs; j++) {
                newG[i][j] = $scope.grid[i][j];
            }
        }

        var ships= new Array();

        for (var i=0; i<gs; i++) {
            for (var j=0;j<gs; j++) {
                if (newG[i][j] == 'X') {
                    ships.push(collectShip (newG, i,j));
                }
            }
        }

        var playerConfig = {};
        playerConfig[currGame.PlayerName]= ships;

        //send ship config to server
        gameService.addPlayer ({'gameId': currGame.gameId, 'players': playerConfig}, function success(s) {
            console.log ("yeah!");},
        function err (e) {
            console.log(e);});
    }

    var collectShip = function (grid, i,j) {
        var startPoint ={};
        var endPoint= {};
        if (grid[i][j]=='.')
            return null;
        startPoint = {'x':i,'y':j};
        endPoint = findEndPoint(grid,i,j,0,0);

        return {
            startPosition: startPoint,
            endPosition:endPoint
        };
    }

    var findEndPoint = function (grid,i,j,dx,dy) {
        var endPoint = {'x':i,'y':j};
        grid[i][j]='.';
        if (dx!=0 || dy!=0){
            if ((i+dx)>=0 && (j+dy)>=0  && (i+dx)<grid.length && (j+dy)<grid.length && grid[i+dx][j+dy] == 'X') {
                return findEndPoint(grid, i+dx, j+dy, dx, dy);
            }
            return endPoint;
        }

        for (var r=0; r<2; r++) {
            for (var c=-1; c<2; c++) {
                if ((i+r)>=0 && (j+c)>=0  && (i+r)<grid.length && (j+c)<grid.length && grid[i+r][j+c] == 'X') {
                    return findEndPoint(grid, i+r,j+c,r,c);
                }
            }
        }
        return endPoint;
    }
  });
