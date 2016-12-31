'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:joinGameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('playGameCtrl', function ($scope,$location,gameService,currGame) {
    console.log('in the playGameCtrl controller');

    $scope.playerName = currGame.PlayerName;

    gameService.getNextPlayerTurn(currGame.gameId,
        function success(p){$scope.playerTurn=p;},
        function err(e){console.log(e);}
    );

    gameService.getPlayerBoards (currGame.gameId, currGame.PlayerName,
        function (s) {
            var mySrvBoard = s.ownBoard.grid.g;
            var boardSize = mySrvBoard.length;
             $scope.myBoard = new Array(boardSize);
             for (var i=0; i<boardSize; i++ ){
                    $scope.myBoard[i] = new Array(boardSize);
              }
             for (var i=0;i<boardSize; i++ ){
                  for (var j=0; j<boardSize; j++) {
                      if (mySrvBoard[i][j]=='EmptySea')
                          $scope.myBoard [i][j]='-.-';
                      else if (mySrvBoard[i][j]=='BattleShipSafe')
                           $scope.myBoard [i][j]='-o-';
                      else if (mySrvBoard[i][j]=='BattleShipHit')
                            $scope.myBoard [i][j]='X';
                  };
          };

          $scope.opponentsBoards = {};

          var opponents=Object.keys(s.opponentsBoards);
          opponents.forEach(function (o) {
            var oboard = new Array (boardSize);
            for (var i=0; i<boardSize; i++) {
                oboard[i]=new Array(boardSize);
            };
            for (var i=0;i<boardSize; i++ ){
              for (var j=0; j<boardSize; j++) {
                  if (s.opponentsBoards[o].grid.g[i][j]=='EmptySea')
                     oboard [i][j]='-.-';
                  else if (s.opponentsBoards[o].grid.g[i][j]=='MissedShot')
                     oboard[i][j]='O';
                  else if (s.opponentsBoards[o].grid.g[i][j]=='BattleShipHit')
                     oboard [i][j]='X';
               };
            };
            $scope.opponentsBoards[o] = oboard;
          });
          $scope.opponents=opponents;
        },
        function err(e){console.log(e);});

       $scope.ShotClicked = function(x,y) {
            gameService.shoot(currGame.gameId,x,y, function(s) {
                console.log(s);
                updateOpponentsBoards();
                },
             function(e){console.log(e);})
           };

       var updateOpponentsBoards = function () {
            gameService.getPlayerBoards (currGame.gameId, currGame.PlayerName,
            function (s) {

              var boardSize = s.ownBoard.grid.g.length;
              $scope.opponentsBoards = {};

              var opponents=Object.keys(s.opponentsBoards);
              opponents.forEach(function (o) {
                var oboard = new Array (boardSize);
                for (var i=0; i<boardSize; i++) {
                    oboard[i]=new Array(boardSize);
                };
                for (var i=0;i<boardSize; i++ ){
                  for (var j=0; j<boardSize; j++) {
                      if (s.opponentsBoards[o].grid.g[i][j]=='EmptySea')
                         oboard [i][j]='-.-';
                      else if (s.opponentsBoards[o].grid.g[i][j]=='MissedShot')
                         oboard[i][j]='O';
                      else if (s.opponentsBoards[o].grid.g[i][j]=='BattleShipHit')
                         oboard [i][j]='X';
                   };
                };
                $scope.opponentsBoards[o] = oboard;
              });
              $scope.opponents=opponents;
            },
            function err(e){console.log(e);});
       }
});