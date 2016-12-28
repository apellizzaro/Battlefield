'use strict';

/**
 * @ngdoc overview
 * @name battleShipApp
 * @description
 * # battleShipApp
 *
 * Main module of the application.
 */
angular
  .module('battleShipApp', [
    'ngCookies',
    'ngMessages',
    'ngResource',
    'ngRoute',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
              templateUrl: 'webApp/views/mainMenu.html',
              controller: 'MainMenuCtrl'
            })
      .when('/newGame', {
        templateUrl: 'webApp/views/newGame.html',
        controller: 'NewgameCtrl'
      })
      .when('/joinGame', {
        templateUrl: 'webApp/views/joinGame.html',
        controller: 'JoinGameCtrl'
      })
      .when('/resumeGame', {
        templateUrl: 'webApp/views/resumeGame.html',
        controller: 'ResumeGameCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
