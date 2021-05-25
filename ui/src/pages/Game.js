import React, {useState, useEffect} from "react";
import { useAuth } from "../context/auth";
import { GameStateContext } from "../context/GameState";
import {useParams} from "react-router";
import Startup from "./Startup";
import {Redirect} from "react-router";
//import Voting from "./Voting";
import Playing from "./Playing";
import {DndProvider} from "react-dnd";
import {HTML5Backend} from "react-dnd-html5-backend";
import {SourceIndexes} from "../components/SlotIndexes";
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
            const totalCards = (state) => {
                let total = 0
                if (!state || !state.players) {
                    return total
                }
                state.players.forEach(p => {
                    if (p.hand) {
                        p.hand.forEach(c => total += c)
                    }
                })
                return total
            }
            let oldState = gameState
            // need to reset when hand changes, or when pile that's attached changes
            let oldHandSum = totalCards(oldState)
            let newHandSum = totalCards(data)
            let pileChanged = false
            if (oldState && oldState.piles && data.piles) {
                piled.keys.forEach(offset => {
                    let index = offset - SourceIndexes.PileOffset
                    if (oldState.piles[index].topCard !== data.piles[index].topCard) {
                        pileChanged = true;
                    }
                })
            }
            if (pileChanged || oldHandSum !== newHandSum) {
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
                    setState(indexCards(message.data.state))
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

    const registerDrop = (source, targetIndex, resetTarget) => {
        const sourceIndex = source.index
        let newAttached = {...isAttached}
        let newPiled = {...piled}
        resetAttached(sourceIndex, newAttached, newPiled)
        if (targetIndex != null && resetTarget) {
            const resetCards = piled[targetIndex] ?? []
            resetCards.forEach(c => resetAttached(c.data.sourceIndex, newAttached, newPiled))
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
            if (currentAttached[index + SourceIndexes.HandIndex] == null) {
                cardArrangement.push({card: card, index: index})
            } else {
                cardsPlayed += 1
            }
        })
        let pileArrangement = []
        gameState.piles.forEach((pile, index) => {
            let offset = SourceIndexes.PileOffset + index
            if (currentPiled[offset] && currentPiled[offset].length > 0) {
                pileArrangement.push({
                    direction: pile.direction,
                    cards: currentPiled[offset],
                    topCard: currentPiled[offset][currentPiled[offset].length - 1].card,
                    topCardIndex: currentPiled[offset][currentPiled[offset].length - 1].index
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
                    return <Playing arrangement={arrangement} />
                default:
                    return <div>Unknown state</div>

            }
        } else {
            switch (stage.stage) {
                case "NotStarted":
                    return <Startup/>
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
            <DndProvider backend={HTML5Backend}>
                {renderGameStage()}
            </DndProvider>
        </GameStateContext.Provider>
    )
}

export default Game;
