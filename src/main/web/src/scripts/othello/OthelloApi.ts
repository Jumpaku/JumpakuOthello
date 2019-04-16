
export type Board = {
    darkDiscs: number[],
    lightDiscs: number[],
}

export type GameState = {
    board: Board,
    history: number[],
} & ({
    state: "Completed",
    darkCount: number,
    lightCount: number
} | {
    state: "Inprogress",
    selectPlayer: "Dark" | "Light",
    availableMoves: number[]
});

export type GamesResult = {
    gameId: string,
    gameState: GameState
}

export const host: string = "http://172.20.10.3:8080"

export async function makeNewGame(): Promise<GamesResult> {
    return fetch(new Request(`${host}/v1/games/?action=make`, {
        method: 'POST'
    })).then(result => result.json().then(json => {
        console.log(`makeNewGame : ${JSON.stringify(json)}`);
        return json as GamesResult
    }));
}

export type GamesGetRequest = {
    gameId: String
}
export async function getGameState(requestData: GamesGetRequest): Promise<GamesResult> {
    return fetch(new Request(`${host}/v1/games/?action=get`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(result => result.json().then(json => {
        console.log(`getGameState : ${JSON.stringify(json)}`);
        return json as GamesResult
    }));
}

export type GamesMoveRequest = {
    gameId: String,
    move: number
}
export async function selectMove(requestData: GamesMoveRequest): Promise<GamesResult> {
    return fetch(new Request(`${host}/v1/games/?action=move`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(result => result.json().then(json => {
        console.log(`selectMove : ${JSON.stringify(json)}`);
        return json as GamesResult
    }));
}

export type SelectMoveByAiRequest = {
    selectPlayer: "Dark" | "Light",
    board: Board
}
export async function selectMoveByAi(requestData: SelectMoveByAiRequest): Promise<{ move: number }> {
    return fetch(new Request(`${host}/v1/ai/move/`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(result => result.json().then(json => {
        console.log(`selectMoveByAi : ${JSON.stringify(json)}`);
        return json as { move: number };
    }));
}