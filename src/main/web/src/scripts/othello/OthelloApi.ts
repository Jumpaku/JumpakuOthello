
export type Board = {
    darkDiscs: number[],
    lightDiscs: number[],
}
export type Disc = "Dark" | "Light"
export type Completed = {
    state: "Completed",
    darkCount: number,
    lightCount: number
}
export type InProgress = {
    state: "InProgress",
    selectPlayer: Disc,
    availableMoves: number[]
}
export type GameStateBase = {
    board: Board,
    history: number[],
}
export type GameState = GameStateBase & (Completed | InProgress);

export type GamesResult = {
    gameId: string,
    gameState: GameState
}

export type Error = {
    message: string
}

export type Response<T> = T | Error

const host: string = "http://172.20.10.3:8080"

async function parseResponse(response: { json: () => any }): Promise<GamesResult> {
    const json = await (response.json() as Promise<Response<GamesResult>>)
    console.log((json as GamesResult).gameState);
    return Object.assign({}, json as GamesResult);
}

export async function makeNewGame(): Promise<GamesResult> {
    return fetch(new Request(`${host}/v1/games/?action=make`, {
        method: 'POST'
    })).then(parseResponse);
}

export type GamesGetRequest = { gameId: String }
export async function getGameState(requestData: GamesGetRequest): Promise<GamesResult> {
    console.log(requestData);
    return fetch(new Request(`${host}/v1/games/?action=get`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(parseResponse);
}

export type GamesMoveRequest = { gameId: String, move: number }
export async function selectMove(requestData: GamesMoveRequest): Promise<GamesResult> {
    console.log(requestData);
    return fetch(new Request(`${host}/v1/games/?action=move`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(parseResponse);
}


export type SelectMoveByAiRequest = {
    selectPlayer: Disc,
    board: Board
}
export type SelectorResult = { move: number }
export async function selectMoveByAi(requestData: SelectMoveByAiRequest): Promise<SelectorResult> {
    return fetch(new Request(`${host}/v1/ai/move/`, {
        method: 'POST',
        body: JSON.stringify(requestData)
    })).then(result => result.json().then(json => {
        console.log(json);
        return Object.assign({}, json as SelectorResult);
    }));
}