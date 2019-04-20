import { makeNewGame } from "./othello/OthelloApi";
import { PlayerType, update } from "./App";


function start(darkPlayer: PlayerType, lightPlayer: PlayerType) {
    makeNewGame().then(({ gameId, gameState }) => update({ gameId: gameId, darkPlayer: darkPlayer, lightPlayer: lightPlayer }, gameState));
}

window.onload = () => start("Human", "Ai");
    
/*async () => { 
    const player = "Dark";
    let { gameId } = await makeNewGame();
    let { gameState } = await getGameState({ gameId: gameId });
    console.log(`state0: ${JSON.stringify(gameState)}`);
    await selectMove({ gameId: gameId, move: 26 });
    ({ gameState } = await getGameState({ gameId: gameId }));
    console.log(`state1: ${JSON.stringify(gameState)}`);
    const { move } = await (gameState.state === "InProgress" && selectMoveByAi({
        board: gameState.board,
        selectPlayer: gameState.selectPlayer
    })) || { move: -1 };
    await selectMove({ gameId: gameId, move: move });
    ({ gameState } = await getGameState({ gameId: gameId }));
    console.log(`state2: ${JSON.stringify(gameState)}`);
    const board = gameState.board;
    ReactDOM.render((
        <div>
            <div className={styles.Title}>Jumpaku Othello</div>
            <BoardComponent darkDiscs={board.darkDiscs} lightDiscs={board.lightDiscs}/>
        </div>
    ), document.getElementById("App"));
};*/
