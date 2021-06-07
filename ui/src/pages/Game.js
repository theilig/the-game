import React, {useState, useEffect} from "react";
import { useAuth } from "../context/auth";
import { GameStateContext } from "../context/GameState";
import {useParams} from "react-router";
import Startup from "./Startup";
import {Redirect} from "react-router";
//import Voting from "./Voting";
import Playing from "./Playing";
import {DndProvider} from "react-dnd";
import MultiBackend from 'react-dnd-multi-backend';
import HTML5ToTouch from 'react-dnd-multi-backend/dist/esm/HTML5toTouch';
import {SourceIndexes, TargetIndexes} from "../components/SlotIndexes";
import ReconnectingWebSocket from "reconnecting-websocket";
import configData from "../environment/config.json";

function Game() {
    const [ gameState, setGameState ] = useState()
    const [ gameSocket, setGameSocket ] = useState(null)
    const { authTokens } = useAuth()
    const [ gameOver, setGameOver] = useState(false)
    const [ isAttached, setIsAttached ] = useState({})
    const [ piled, setPiled ] = useState({})
    const [ player, setPlayer ] = useState(null)

    let game = useParams()
    let gameId = parseInt(game.gameId)

    const sendMessage = (message) => {
        let ws = gameSocket
        message.data = message.data ?? {}
        message.data.gameId = gameId
        ws.send(JSON.stringify(message))
    }

    useEffect(() => {
        const setState = (data) => {
            // need to reset when currentPlayer changes
            if (gameState && gameState.stage.data.currentPlayerId !== data.stage.data.currentPlayerId) {
                setIsAttached({})
                setPiled({})
            }

            setGameState(data);
        }

        const connectToGame = (ws, gameId) => {
            ws.send(JSON.stringify(
                {
                    messageType: "Authentication",
                    data: {
                        token: authTokens.token
                    }
                }
            ))
            ws.send(JSON.stringify(
                {
                    messageType: "ConnectToGame",
                    data: {
                        gameId: gameId
                    }
                }
            ))
        }

        const indexCards = (state => {
            if (state.stage.stage === "NotStarted" ||
                state.stage.stage === "GameEnded")  {
                return state
            }
            state.players.forEach((p) => {
                if (authTokens.user.userId === p.userId) {
                    setPlayer(p)
                }
            })

            return state
        })
        if (gameSocket == null) {
            const rws = new ReconnectingWebSocket(configData.WEBSOCKET_URL + '/api/game');
            rws.addEventListener('open', () => {
                setGameSocket(rws)
                connectToGame(rws, gameId)
            })
            rws.addEventListener('message', (received) => {
                const message = JSON.parse(received.data)
                if (message.messageType === "GameState") {
                    setState(indexCards(message.data.projection))
                } else if (message.messageType === "GameOver") {
                    setGameOver(true)
                } else if (message.messageType === "Not Authenticated") {
                    setGameOver(true)
                }
            })
        }
    }, [gameSocket, piled.keys, gameState, authTokens.user.userId, gameId, authTokens.token])

    const isActivePlayer = (state) => {
        if (state && state.stage) {
            const stage = state.stage
            return stage.stage && stage.data &&
                stage.data.currentPlayerId != null &&
                parseInt(authTokens.user.userId) === stage.data.currentPlayerId
        } else return false
    }

    const resetAttached = (sourceIndex, newAttached, newPiled) => {
        if (newAttached[sourceIndex] != null) {
            let removePiled = [...piled[newAttached[sourceIndex]]]
            removePiled = removePiled.filter((c) => {
                return c.index !== sourceIndex
            })
            newPiled[newAttached[sourceIndex]] = removePiled
            newAttached[sourceIndex] = null
        }
    }

    const registerDrop = (source, targetIndex) => {
        const sourceIndex = source.index
        let newAttached = {...isAttached}
        let newPiled = {...piled}
        if (gameState.players.length > 1) {
            // for multi-player game we only attache one card at a time
            newAttached = {}
            newPiled = {}
        } else {
            resetAttached(sourceIndex, newAttached, newPiled)
        }
        if (targetIndex != null) {
            newAttached[sourceIndex] = targetIndex
            let piledList = newPiled[targetIndex] ?? []
            piledList = [...piledList, source]
            newPiled[targetIndex] = piledList
        }
        setPiled(newPiled)
        setIsAttached(newAttached)
    }

    const getArrangement = (player, currentAttached, currentPiled) => {
        const hand = player.hand
        let cardArrangement = []
        let cardsPlayed = 0
        hand.forEach((card, index) => {
            if (currentAttached[card + SourceIndexes.HandIndex] == null) {
                cardArrangement.push({card: card, index: index})
            } else {
                cardsPlayed += 1
            }
        })
        let pileArrangement = []
        gameState.piles.forEach((pile, index) => {
            let offset = SourceIndexes.PileOffset + index
            let piledCards = []
            if (currentPiled[offset] && currentPiled[offset].length > 0) {
                piledCards = currentPiled[offset].filter(c => hand.includes(c.card))
            }
            if (piledCards.length > 0) {
                pileArrangement.push({
                    direction: pile.direction,
                    cards: piledCards,
                    topCard: piledCards[piledCards.length - 1].card,
                    topCardIndex: piledCards[piledCards.length - 1].index
                })
            } else {
                pileArrangement.push({direction: pile.direction, topCard: pile.topCard, cards: []})
            }
        })
        return {cards: cardArrangement, piles: pileArrangement, cardsPlayed: cardsPlayed}
    }

    const renderGameStage = () => {
        if (!gameState) {
            return ""
        }
        let arrangement = []
        let stage = gameState.stage

        let activePlayer = isActivePlayer(gameState)
        if (player && player.hand.length > 0 && gameState.piles) {
            arrangement = getArrangement(
                player,
                isAttached,
                piled)
        }

        if (activePlayer) {
            switch (stage.stage) {
                case "Playing":
                    return <Playing
                        arrangement={arrangement}
                        activePlayer={activePlayer}
                    />
                default:
                    return <div>Unknown state</div>

            }
        } else {
            switch (stage.stage) {
                case "NotStarted":
                    return <Startup/>
                case "Playing":
                    return <Playing
                        arrangement={arrangement}
                        activePlayer={activePlayer}
                    />
                default:
                    break;
            }
            return <div>Unknown state</div>
        }
    }

    if (gameOver) {
        if (gameSocket !== null) {
            setGameSocket(null)
            gameSocket.close()
        }
        return (
            <Redirect to={"/"} />
        )
    }
    return (
        <GameStateContext.Provider value={{
            gameState: gameState,
            registerDrop: registerDrop,
            sendMessage: sendMessage
        }}>
            <DndProvider backend={MultiBackend} options={HTML5ToTouch}>
                {renderGameStage()}
            </DndProvider>
        </GameStateContext.Provider>
    )
}

export default Game;
