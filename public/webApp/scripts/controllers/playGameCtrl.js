'use strict';

/**
 * @ngdoc function
 * @name battleShipApp.controller:joinGameCtrl
 * @description
 * # NewgameCtrl
 * Controller of the battleShipApp
 */
angular.module('battleShipApp')
  .controller('playGameCtrl', function ($scope,$location,gameService,userContext,$interval) {
    console.log('in the playGameCtrl controller');

    if (userContext.PlayerName) {
        $scope.playerName = userContext.PlayerName;
    }
    else {
        userContext.PlayerName = window.localStorage.getItem("contextPlayerName");
        userContext.playerToken = window.localStorage.getItem("contextPlayerToken");
        userContext.gameId = window.localStorage.getItem("contextGameId");
        $scope.playerName = userContext.PlayerName;
    }

    var updatePlayerTurn = function () {
        gameService.getNextPlayerTurn(userContext.gameId,
                    function success(p){
                    $scope.playerTurn=p;},
                    function err(e){
                    $scope.messageText = e;
                    console.log(e);}
                );
    }

    updatePlayerTurn();

    gameService.getPlayerBoards (userContext.gameId, userContext.playerToken,
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
                      else if (mySrvBoard[i][j]=='BattleShipHit' || mySrvBoard[i][j]=='BattleShipSunk')
                            $scope.myBoard [i][j]='X';
                      else if (mySrvBoard[i][j]=='MissedShot')
                            $scope.myBoard [i][j]='~';
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
                  else if (s.opponentsBoards[o].grid.g[i][j]=='BattleShipSunk')
                     oboard [i][j]='*';
               };
            };
            $scope.opponentsBoards[o] = oboard;
          });
          $scope.opponents=opponents;
        },
        function err(e){
        $scope.messageText = e;
        console.log(e);});

       //==========================================
       var formatMessage = function (o) {
         var finalString="";
         var keys = Object.keys(o);
         keys.forEach (function(k) {
            finalString+= k + ":" + o[k]+";";
         });

         return finalString;

       }
       //========================================
       $scope.ShotClicked = function(x,y) {
            gameService.shoot(userContext.gameId,userContext.playerToken, x,y, function(s) {
                $scope.messageText = formatMessage(s);
                updateOpponentsBoards();
                updatePlayerTurn();
                },
             function(e){console.log(e);})
           };

       //===========================================
       var updateOpponentsBoards = function () {
            gameService.getPlayerBoards (userContext.gameId, userContext.playerToken,
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
                      else if (s.opponentsBoards[o].grid.g[i][j]=='BattleShipSunk')
                        oboard [i][j]='*';
                   };
                };
                $scope.opponentsBoards[o] = oboard;
              });
              $scope.opponents=opponents;
            },
            function err(e){console.log(e);
            $scope.messageText = e;});
       }

       var updateOwnBoard = function () {
          gameService.getPlayerBoards (userContext.gameId, userContext.playerToken,
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
                             else if (mySrvBoard[i][j]=='BattleShipHit' || mySrvBoard[i][j]=='BattleShipSunk')
                                 $scope.myBoard [i][j]='X';
                             else if (mySrvBoard[i][j]=='MissedShot')
                                $scope.myBoard [i][j]='~';
                         };
                 };
                },function err(e){console.log(e);
                     $scope.messageText = e;});

          updatePlayerTurn();
        };

       var stopUpdating = function() {
                 if (angular.isDefined(stop)) {
                   $interval.cancel(stop);
                   stop = undefined;
                 }
               };

       $scope.$on('$destroy', function() {
                // Make sure that the interval is destroyed too
                stopUpdating();
              });


       var stop = $interval(updateOwnBoard,2000);

});