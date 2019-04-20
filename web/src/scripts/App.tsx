import { GameState, selectMoveByAi, selectMove } from "./othello/OthelloApi";
import ReactDOM from "react-dom";
import { Title, BoardComponent, Result, Player, History } from "./components/Components";
import React from "react";

export type PlayerType = "Human" | "Ai"
export type GameInfo = {
    gameId: string,
    darkPlayer: PlayerType,
    lightPlayer: PlayerType
}

export function render(gameState: GameState, onSquareSelected: (n: number)=>void) {
    const { board, history } = gameState;
    console.log(history);
    ReactDOM.render((<main>
        <Title />
        {
            gameState.state === "Completed" ?
            <Result darkCount={gameState.darkCount} lightCount={gameState.lightCount}/> :
            <Player disc={gameState.selectPlayer}/>
        }
        <BoardComponent board={board} onSquareSelected={onSquareSelected}/>
        <History history={history}/>
    </main>), document.querySelector("#App"));
}

export function update(gameInfo: GameInfo, gameState: GameState): void {
    if (gameState.state === "Completed") {
        render(gameState, () => {});
        return;
    }
    const { availableMoves, selectPlayer } = gameState;
    const playerType = selectPlayer === "Dark" ? gameInfo.darkPlayer : gameInfo.lightPlayer;
    if (playerType === "Human") {
        if (availableMoves.includes(-1)) {
            selectMove({ gameId: gameInfo.gameId, move: -1 }).then(({ gameState }) => update(gameInfo, gameState));
        }
        else {
            let done = false
            render(gameState, n => {
                if (!done && availableMoves.includes(n)) {
                    done = true;
                    selectMove({ gameId: gameInfo.gameId, move: n }).then(({ gameState }) => update(gameInfo, gameState));
                }
            });
        }
    }
    else {
        render(gameState, ()=>{});
        selectMoveByAi({ board: gameState.board, selectPlayer: selectPlayer })
        .then(({ move }) => selectMove({ gameId: gameInfo.gameId, move: move }))
        .then(({ gameState }) => update(gameInfo, gameState));
    }
}
