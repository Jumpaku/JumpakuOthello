import * as React from "react";
import * as ReactDOM from "react-dom";
import styles from './../styles/style.module.css';
import { BoardComponent, InfomationComponent } from "./components/BoardComponent";
import { makeNewGame, GameState, selectMoveByAi, getGameState, selectMove } from "./othello/OthelloApi";

let gameId: string;
let gameState: GameState;

window.onload = () => {
    makeNewGame().then(result => {
        gameId = result.gameId
        gameState = result.gameState;
        return getGameState({ gameId: gameId })
    }).then(result => {
        const board = result.gameState.board
        if ('selectPlayer' in gameState) return selectMoveByAi({
            board: board,
            selectPlayer: gameState.selectPlayer
        });
        else return Promise.reject();
    }).then(move => {
        console.log(`${Math.floor(move.move/8)}, ${move.move%8}`);
        return selectMove({ gameId: gameId, move: move.move })
    }).then(result => {
        gameState = result.gameState;
        const board = result.gameState.board
        ReactDOM.render((
            <div>
                <div className={styles.Title}>Jumpaku Othello</div>
                <BoardComponent darkDiscs={board.darkDiscs} lightDiscs={board.lightDiscs}/>
                <InfomationComponent gameId={gameId} gameState={gameState}/>
            </div>
        ), document.getElementById("App"));
    });
};
