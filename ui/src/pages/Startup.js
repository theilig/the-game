import React, {useState} from "react";
import { useAuth } from "../context/auth";
import styled from "styled-components";
import {useGameState} from "../context/GameState";
import { Button } from "../components/InputElements"
import NumericInput from 'react-numeric-input';

const PlayerList = styled.div`
    display: flex;
    flex-direction: column;
    margin-left: 10px;
    font-size: xx-large;
`;

const RuleList = styled.div`
    display: flex;
    flex-direction: row;
    height: 27px;
`;

const Options = styled.div`
    display: flex;
    flex-direction: row;
    max-width:410px;
`;

function Startup() {
    const { authTokens } = useAuth()
    const { gameState, sendMessage } = useGameState()
    const [ handSize, setHandSize ] = useState(0)
    const [ cardsToPlay, setCardsToPlay ] = useState(0)
    const MAX_PLAYERS = 5
    function leaveGame() {
        sendMessage({
            messageType: "LeaveGame"
        })
    }
    function requestToJoin() {
        sendMessage({
            messageType: "JoinGame"
        })
    }
    function startGame() {
        const hand = handSize ? handSize : gameState.rules.cardsInHand
        const play = cardsToPlay ? cardsToPlay : gameState.rules.cardsToPlay
        sendMessage({
            messageType: "StartGame",
            data: {
                cardsInHand: hand,
                cardsToPlay: play
            }
        })
    }

    function acceptPlayer(userId) {
        sendMessage({
            messageType: "AcceptPlayer",
            data: {
                userId: userId
            }
        })
    }

    function rejectPlayer(userId) {
        sendMessage({
            messageType: "RejectPlayer",
            data: {
                userId: userId
            }
        })
    }

    const updateHandSize = function (newValue) {
        setHandSize(newValue)
    }

    const updateCardsToPlay = function (newValue) {
        setCardsToPlay(newValue)
    }

    const renderOptions = (isGameOwner) => {
        if (isGameOwner) {
            return (
                <Options>
                    <Button onClick={startGame}>Start Game</Button>
                    <Button onClick={leaveGame}>Leave Game</Button>
                </Options>
            )

        }
        let alreadyIn = false
        let playerCount = 0
        for (let i=0; i<gameState.players.length; i++) {
            if (gameState.players[i].userId === authTokens.user.userId) {
                alreadyIn = true;
            }
            if (!gameState.players[i].pending) {
                playerCount += 1
            }
        }
        if (alreadyIn || playerCount >= MAX_PLAYERS) {
            return (
                <Options>
                    <Button onClick={leaveGame}>Leave Game</Button>
                </Options>
            )
        }
        return (
            <Options>
                <Button onClick={requestToJoin}>Ask to Join</Button>
                <Button onClick={leaveGame}>Leave Game</Button>
            </Options>
        )
    }

    const renderPlayer = (player, isGameOwner) => {
        if (player.pending && isGameOwner) {
            return (
                <div key={player.userId}>
                    <Options>
                        <div>{player.name} (Pending)</div>
                        <Button key={player.userId + "accept"} onClick={() => acceptPlayer(player.userId)}>accept</Button>
                        <Button key={player.userId + "reject"} onClick={() => rejectPlayer(player.userId)}>reject</Button>
                    </Options>
                </div>
            )
        }
        if (player.pending) {
            return (<div key={player.userId}>{player.name} (Pending)</div>)
        }
        return (<div key={player.userId}>{player.name}</div>)
    }

    let isGameOwner = false
    if (gameState.players[0] && authTokens.user.userId === gameState.players[0].userId) {
        isGameOwner = true
    }

    return (
        <div>
            <RuleList>
                Hand Size: <NumericInput
                                min={1}
                                max={10}
                                value={gameState.rules.cardsInHand}
                                style={{
                                    input: {
                                        height: '27px',
                                        width: '50px',
                                    }
                                }}
                                onChange={updateHandSize}
                            />
                Cards To Play: <NumericInput
                                    min={1}
                                    max={5}
                                    value={gameState.rules.cardsToPlay}
                                    style={{
                                        input: {
                                            height: '27px',
                                            width: '50px',
                                        }
                                    }}
                                    onChange={updateCardsToPlay}
                                />
            </RuleList>
            <PlayerList>
                <div>Current Players</div>
                {gameState && gameState.players.map((player) => (
                        renderPlayer(player, isGameOwner)
                    )
                )}
            </PlayerList>
            {renderOptions(isGameOwner)}
        </div>
    )
}

export default Startup;