import { GameState, Board, GamesResult } from "../othello/OthelloApi";
import * as React from "react";
import darkSquare from "./../../../assets/Dark.png";
import lightSquare from "./../../../assets/Light.png";
import emptySquare from "./../../../assets/Empty.png";
import * as BoardStyle from "./../../styles/Board.module.css";


export class JumpakuOthelloView extends React.PureComponent {

    constructor(props: GamesResult) {
        super(props);
    }

    render(): JSX.Element {
        return <div></div>
    }
}

export const BoardComponent = (props: Board): JSX.Element => {
    return (<div className={BoardStyle.Board}>{
        Array.from(Array(64).keys()).map(n => {
            if (props.darkDiscs.includes(n)) return <img src={darkSquare} className={BoardStyle.Square} onClick={() => console.log(n)}/>
            if (props.lightDiscs.includes(n)) return <img src={lightSquare} className={BoardStyle.Square} onClick={() => console.log(n)}/>
            return <img src={emptySquare} className={BoardStyle.Square} onClick={() => console.log(n)}/>
        })
    }</div>);
}

export const InfomationComponent = (prop: GamesResult): JSX.Element => {
    return <div>{prop.gameId}: {JSON.stringify(prop.gameState)}</div>
}