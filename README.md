# JumpakuOthello

Othello game logic and AI.

## Play with example web application

https://othello.jumpaku.net/app/

## Install API server

docker-compose.yml

```yml
version: '3'

services: 

  jumpaku-othello:
    images: jumpaku/jumpaku-othello
    container_name: 'jumpaku-othello'
```

`jumpaku-othello` listens at `8080` port.

## API specification

### Handle games

|  | URI Path | Method | Request body | Response body |
|-------------------------|------------------------|--------|------------------------------------|--------------------------------------------|
| Make a new game | `origin`/v1/games/?action=make | POST | (None) | `{ gameId: string, gameState: GameStateResult }` |
| Get a state of the game | `origin`/v1/games/?action=get | POST | `{ gameId: string }` | `{ gameId: string, gameState: GameStateResult }` |
| Make a move | `origin`/v1/games/?action=move | POST | `{ gameId: string, move: number }` | `{ gameId: string, gameState: GameStateResult }` |

`origin` is https://othello.jumpaku.net or where you installed jumpaku-othello.

#### Make new game

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=make
```

```json

```

#### Get current game state

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=get -d "{ \"gameId\":  }"
```

```json

```

#### Make a move

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=get -d "{ \"gameId\": , \"move\":  }"
```

```json

```

### Get a move from AI

|  | URI Path | Method | Request body | Response body |
|-------------------------|------------------------|--------|------------------------------------|--------------------------------------------|
| Get a move from AI | `origin`/v1/ai/move | POST | `{ selectPlayer: Disc, board: Board }` | `MoveResult` |

```sh
curl -X POST https://othello.jumpaku.net/v1/games/?action=get -d "{ \"board\": , \"selectPlayer\": \"Dark\" }"
```

```json

```

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

* `state` in `Completed` represents that the game has been completed.
* `darkCount` in `Completed` represents the number of dark discs on the board.
* `lightCount` in `Completed` represents the number of light discs on the board.

```ts
type InProgress = {
    state: "InProgress",
    selectPlayer: Disc,
    availableMoves: number[]
};
```

* `state` in `InProgress` represents that the game is in progress.
* `selectPlayer` in `InProgress` represents a color of the disc of player who is making a move.
* `availableMoves` in `InProgress` represents a list of position indices where the current player can select. If there is no position to place a disc, `availableMoves` is `[-1]`.

```ts
type GameStateBase = {
    board: Board,
    history: number[],
};
```

* `board` in `GameStateBase` represents current board of the game.
* `history` in `GameStateBase` represents a list of position indices where the player has been placed.

```ts
type GameState = GameStateBase & (Completed | InProgress);
```

```ts
type Error = {
    message: string
};
```

```ts
type GameStateResult = GameState | Error;
```

```ts
type MoveResult = { move: number } | Error;
```

