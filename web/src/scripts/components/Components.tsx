import { Board, Disc } from "../othello/OthelloApi";
import * as React from "react";
import darkSquare from "./../../../assets/Dark.png";
import lightSquare from "./../../../assets/Light.png";
import emptySquare from "./../../../assets/Empty.png";
import * as Styles from "./../../styles/style.module.css";

export const Title = (): JSX.Element => (<div className={Styles.Title}>Jumpaku Othello</div>);

export type BoardProps = { board: Board, onSquareSelected: (n: number) => void }
export const BoardComponent = ({board, onSquareSelected}: BoardProps): JSX.Element => (
    <div className={Styles.Board}>{
        Array.from(Array(64).keys()).map(n => {
            if (board.darkDiscs.includes(n)) return (
                <img src={darkSquare} className={Styles.Square} onClick={() => onSquareSelected(n)} />
            )
            if (board.lightDiscs.includes(n)) return (
                <img src={lightSquare} className={Styles.Square} onClick={() => onSquareSelected(n)} />
            )
            return (
                <img src={emptySquare} className={Styles.Square} onClick={() => onSquareSelected(n)} />
            )
        })
    }</div>
);

export type ResultProps = { darkCount: number, lightCount: number };
export const Result = ({ darkCount, lightCount }: ResultProps): JSX.Element => (
    <div className={Styles.Result}>Dark : {darkCount} - Light : {lightCount}</div>
);

export type PlayerProps = { disc: Disc };
export const Player = ({ disc }: PlayerProps): JSX.Element => (
    <div className={Styles.Player}>Player : {disc}</div>
);

export type HistoryProps = { history: number[] };
export const History = ({ history }: HistoryProps) => {
    const text = history
    .filter(n => 0<=n && n<=63)
    .map(n => `${1+(n%8)}${"ABCDEFGH"[Math.floor(n/8)]}`)
    .join("");
    return (
        <div className={Styles.History}>
            <div><label>History</label></div>
            <div><input className={Styles.HistoryText} type={"text"} onChange={()=>{}} value={text}/></div>
        </div>
    );
};
