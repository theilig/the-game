import React from "react";
import {useGameState} from "../context/GameState";
import {useAuth} from "../context/auth";
import styled from "styled-components";

const HeaderBlock = styled.div`
    display:flex;
    margin-left: 10px;
`;
const HeaderEntry = styled.div`
    margin-left: 10px;
`;
function GameHeader(props) {
    const { authTokens } = useAuth()
    const { gameState } = useGameState()
    if (gameState) {
        return (<HeaderBlock>
                <HeaderEntry>{gameState.teams.road.city}</HeaderEntry>
                <HeaderEntry>{gameState.teams.road.score}</HeaderEntry>
                <HeaderEntry>{gameState.teams.home.city}</HeaderEntry>
                <HeaderEntry>{gameState.teams.home.score}</HeaderEntry>
                <HeaderEntry>{gameState.park.name}</HeaderEntry>
                <HeaderEntry>Temp: {gameState.park.temperature.result}</HeaderEntry>
                <HeaderEntry>Sky: {gameState.park.cloudCover.result}</HeaderEntry>
                <HeaderEntry>Wind: {gameState.park.wind.result}</HeaderEntry>
                <HeaderEntry>Month: {gameState.park.month}</HeaderEntry>
                <HeaderEntry>Time Of Day: {gameState.park.timeOfDay}</HeaderEntry>
            </HeaderBlock>
        );
    } else {
        return null;
    }
}
export default GameHeader;
