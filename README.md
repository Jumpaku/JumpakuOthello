# JumpakuOthello

Othello game logic and AI.

## Play with example web application

https://othello.jumpaku.net/app/

## Install with Docker

1. Start server by executing `docker run -d -p 81:8080 jumpaku/jumpaku-othello`.
2. jumpaku-othello listens at port `8080` in the container.
3. Access with `curl localhost:81/v1/api/` and get a response `Jumpaku Othello API v1`.

Docker Hub: https://hub.docker.com/r/jumpaku/jumpaku-othello

## Install without Docker

### Prerequisite

```sh
apt update -y && apt install -y git npm openjdk-8-jdk
```

### Installation

1. Build web resources and server by executing
  
```sh
WORKDIR=$(pwd)
# Clone repository
git clone https://github.com/Jumpaku/JumpakuOthello.git
# Build web resources
cd "${WORKDIR}"/JumpakuOthello/web 
npm install && npm run build
cp -r "${WORKDIR}"/JumpakuOthello/web/dist "${WORKDIR}"/JumpakuOthello/api/src/main/resources/
# Build server
cd "${WORKDIR}"/JumpakuOthello/api/
./gradlew build
```

2. Run the server by executing 

```sh
./gradlew run
```

3. The server listens at port `8080`.
4. Access with `curl localhost:8080/v1/api/` and get a response `Jumpaku Othello API v1`.

## API specification

### Handling games

|  | URI Path | Method | Request body type | Response body type |
|-------------------------|------------------------|--------|------------------------------------|--------------------------------------------|
| Make a new game | `origin`/v1/games/?action=make | POST | (None) | `{ gameId: string, gameState: GameStateResult }` |
| Get a state of the game | `origin`/v1/games/?action=get | POST | `{ gameId: string }` | `{ gameId: string, gameState: GameStateResult }` |
| Make a move | `origin`/v1/games/?action=move | POST | `{ gameId: string, move: number }` | `{ gameId: string, gameState: GameStateResult }` |

`origin` is https://othello.jumpaku.net or where you installed jumpaku-othello.

### Examples of handling a game

#### Make new game

Request

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=make
```

Response

```json
{
  "gameId": "adddaabc-ccc8-4ebb-b1f4-b8b4e7e9087c",
  "gameState": {
    "board": {
      "darkDiscs": [28,35],
      "lightDiscs": [27,36]
    },
    "history": [],
    "state": "InProgress",
    "selectPlayer": "Dark",
    "availableMoves": [19,26,37,44]
  }
}
```

#### Get current game state

Request

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=get -d '{ "gameId": "adddaabc-ccc8-4ebb-b1f4-b8b4e7e9087c" }'
```

Response

```json
{
  "gameId": "adddaabc-ccc8-4ebb-b1f4-b8b4e7e9087c",
  "gameState": {
    "board": {
      "darkDiscs": [28,35],
      "lightDiscs": [27,36]
    },
    "history": [],
    "state": "InProgress",
    "selectPlayer": "Dark",
    "availableMoves": [19,26,37,44]
  }
}
```

#### Make a move

Request

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=move -d '{ "gameId": "adddaabc-ccc8-4ebb-b1f4-b8b4e7e9087c", "move": 19 }'
```

Response

```json
{
  "gameId": "adddaabc-ccc8-4ebb-b1f4-b8b4e7e9087c",
  "gameState": {
    "board": {
      "darkDiscs": [19,27,28,35],
      "lightDiscs": [36]
    },
    "history": [19],
    "state": "InProgress",
    "selectPlayer": "Light",
    "availableMoves": [18,20,34]
  }
}
```

### Inquiring to the AI

|  | URI Path | Method | Request body type | Response body type |
|---|----------|--------|-------------------|--------------------|
| Get a move from AI | `origin`/v1/ai/move | POST | `{ selectPlayer: Disc, board: Board }` | `MoveResult` |
| Evaluate available moves by AI | `origin`/v1/ai/moves | POST | `{ selectPlayer: Disc, board: Board }` | `MovesResult` |

#### Example of getting a move from AI

Request

```sh
curl -X POST https://othello.jumpaku.net/v1/ai/move -d '{ "board": { "darkDiscs": [19,27,28,35], "lightDiscs": [36] }, "selectPlayer": "Light" }'
```

Response

```json
{
  "move": 18
}
```

If there is no position to place disc, you receive `{ "move": -1 }`.

#### Example of evaluating available moves by AI

Request

```sh
curl -X POST https://othello.jumpaku.net/v1/ai/moves -d '{ "board": { "darkDiscs": [19,27,28,35], "lightDiscs": [36] }, "selectPlayer": "Light" }' 
```

Response

```sh
{
  "moves": [
    {
      "move": 18, "evaluation": -20
    }, {
      "move": 20, "evaluation": -8.4
    }, {
      "move": 34, "evaluation": -1.6
    }
  ]
}
```

Elements in `moves` are not sorted.
If there is no position to place disc, you receive `{ "moves": [] }`.

### Definition of Types

```ts
type Disc = "Dark" | "Light";
```

```ts
type Board = {
    darkDiscs: number[],
    lightDiscs: number[],
};
```

* `darkDiscs` in `Board` represents a list of position indices where dark disc is placed. Each position index `n` in `darkDiscs` represents a position (`Math.floor(n/8)`, `n%8`) on the board.
* `lightDiscs` in `Board` represents a list of position indices where dark disc is placed. Each position index `n` in `lightDiscs` represents a position (`Math.floor(n/8)`, `n%8`) on the board.

```ts
type Completed = {
    state: "Completed",
    darkCount: number,
    lightCount: number
};
```

* `state: "Completed"` in `Completed` represents that the game has been completed.
* `darkCount` in `Completed` represents the number of dark discs on the board.
* `lightCount` in `Completed` represents the number of light discs on the board.

```ts
type InProgress = {
    state: "InProgress",
    selectPlayer: Disc,
    availableMoves: number[]
};
```

* `state: "InProgress"` in `InProgress` represents that the game is in progress.
* `selectPlayer` in `InProgress` represents a color of the disc of player who is making a move.
* `availableMoves` in `InProgress` represents a list of position indices where the current player can select. If there is no position to place a disc, `availableMoves` is `[-1]`.

```ts
type GameStateBase = {
    board: Board,
    history: number[],
};
```

* `board` in `GameStateBase` represents the current board of the game.
* `history` in `GameStateBase` represents a list of position indices where the players have been placed.

```ts
type GameState = GameStateBase & (Completed | InProgress);
```

```ts
type Error = {
    message: string
};
```

* `message` in `Error` represents an error message from the API server.

```ts
type GameStateResult = {
  gameId: string,
  gameState: GameState
} | Error;
```

* `gameId` identifies which game you want to handle.

```ts
type MoveResult = { move: number } | Error;
```

```ts
type MovesResult = { moves: EvaluatedMove[] } | Error;
```

```ts
type EvaluatedMove = { move: number: evaluation: number };
```

## More information

https://jumpaku.hatenablog.com/entry/2019/09/17/Jumpaku_Othello

