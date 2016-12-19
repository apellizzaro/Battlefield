#BattleField game, backend support


##Overview
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
The service is implemented using the Play! framework with Scala.
To build the service:
```
sbt compile
```

To create a distributable:
```
sbt dist
```


##API

###PUT  /api/v1/newGame                             
Used to create a new game. The game is created with a *GameSettingUp* mode
####Parameters

####Query parameters:
* *name*: The name of the game
* *boardsize*: The size of the board


####Body
A  json specifying the battleships for players (all or some, it does not matter)
it is a Map[ PlayerName:String, Array[{startPosition, endPosition}]
Example:
```
{"John" : [
  {"startPosition":{
  "x":1,"y":3},
  "endPosition":{
  "x":4,"y":3}},
  {"startPosition":{
  "x":3,"y":3},
  "endPosition":{
  "x":6,"y":6}}
],
 "Tobias" : [
  {"startPosition":{
  "x":5,"y":1},
  "endPosition":{
  "x":5,"y":5}}
],"Antonio" : [
  {"startPosition":{
  "x":0,"y":0},
  "endPosition":{
  "x":4,"y":4}}
]
}
```

####Returns
A string with the game identifier (a Guid)

####RequestExample:
PUT http://127.0.0.1:9000/api/v1/newGame?name=test&boardsize=10

###PUT  /api/v1/game/:gameId/players
Add players to an existing game with id=gameId.
####Parameters
* gameId: the Id of the game, as returned by the newGame API 


####Body
Same as newGame APi

####Returns
A string with the game identifier (a Guid)

###POST /api/v1/game/:gameId/start
Sets the game status to Inprogress
####Parameters
* gameId: the Id of the game, as returned by the newGame API 


###GET  /api/v1/game/:gameId/status
Get the game status
####Parameters
* gameId: the Id of the game, as returned by the newGame API 

####Returns
A json indicating the list of Players, who's turn next and the game status (InProgress, SettingUp, Ended)

###GET  /api/v1/game/:gameId/player/:playerId/boards
Gets the boards for a player.
####Parameters
* gameId: the Id of the game, as returned by the newGame API 
* playerId: the name of the player. 

####Returns
A json representing the player board and the player's opponents board.
It can be a fairly big json, as it retruns all the cell of a board, with every cell a string indicating  if it is a hot, miss, openSea etc). The opponents boards are in a Map[Opponent name, board]

###POST /api/v1/game/:gameId/shoot
Performs the shooting operation,the player who is shooting is determine bythe service.
This operation will update all the boards, according to the result of the shooting.
####Parameters
* gameId: the Id of the game, as returned by the newGame API 
* Body parameter: a Json representing the point to shoot:
```
{"x":3,"y":4}
```
####Return
A json,inidicating the result of the shot for each user (Map[PlayerName, resultShooting])

