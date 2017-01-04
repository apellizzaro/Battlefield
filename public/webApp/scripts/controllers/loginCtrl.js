'use strict';


angular.module('battleShipApp')
  .controller('loginCtrl', function ($scope,$location,gameService,userContext) {

    if (userContext.PlayerName)
        $scope.playerName = userContext.PlayerName;

    $scope.login = function () {
        var playerName = $scope.playerName;
        gameService.doLogin(playerName, function (d) {
            userContext.PlayerName = playerName;
            userContext.playerToken = d;
            window.localStorage.setItem("contextPlayerName", playerName);
            window.localStorage.setItem("contextPlayerToken", d);
            $location.path("mainMenu");
        }, function (e) {
            console.log(e);
            $scope.errorMsg = e;
        });
    }


});