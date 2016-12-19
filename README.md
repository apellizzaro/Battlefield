#BattleField game, backend support
#====================================

##Overview
##=======================================
This project implements a simple Battlefield server.
The service provides API to create an play Battlefields games. It is missing a front end implementation.

The size of the board can be an arbitrary value, that is set at game creation.

The number of players is also arbitrary: players are able to join in a game, as long as it is not started.
When  a player joins a game, he sets the battleships on the board, by providing the start and end
coordinate for each ship.

The current implementation does not enforce that each player has the same number of ships. Future development.

In the current implementation, a game can be started by anyone.

Once the game is started, players take turn to shoot. The order is determined by the service.
After each shot, the service returns a Map[String, result] indicating player->result of the shot:
Hit, ,Miss, Sunk, already hit.

The game ends when there is only one player with at least one part of the ship not hit.

Each player has his own board, with his battleships, and one board for each opponent.
After every shot, the boards get updated.

The current implementation is missing a publisher/subscriber event logic, so clients will need to poll
the service for getting an update on the status of the boards, and the status of a game.


##Technology
##=======================================
The service is implemented using the Play! framework with Scala.
To build the service:
*sbt compile*

To create a distributable:
*sbt dist*


##API
##=====================================
