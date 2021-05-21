import React, {useState, useEffect} from "react";
import { useAuth } from "../context/auth";
import { GameStateContext } from "../context/GameState";
import {useParams} from "react-router";
import Startup from "./Startup";
import {Redirect} from "react-router";
//import Voting from "./Voting";
import Playing from "./Playing";
import cardImages from "../img/cards/cards";
import {DndProvider} from "react-dnd";
import {HTML5Backend} from "react-dnd-html5-backend";
import {SourceIndexes, TargetIndexes} from "../components/SlotIndexes";
//import GameResult from "./GameResult";
import ReconnectingWebSocket from "reconnecting-websocket";

function Game() {
    const [ gameState, setGameState ] = useState()
    const [ gameSocket, setGameSocket ] = useState(null)
    const { authTokens } = useAuth()
    const [ gameOver, setGameOver] = useState(false)
    const [ hovered, setHovered] = useState(null)
    const [ isAttached, setIsAttached ] = useState({})
    const [ piled, setPiled ] = useState({})
    const [ player, setPlayer ] = useState(null)

    let game = useParams()
    let gameId = parseInt(game.gameId)

    const cardBack = {
        cardType: 'CardBack',
        data: {
            id: 0,
            name: "CardBack",
            imageName: "card000.png",
            frequency: 0
        }
    }

    const sendMessage = (message) => {
        let ws = gameSocket
        message.data = message.data ?? {}
        message.data.gameId = gameId
        ws.send(JSON.stringify(message))
    }

    useEffect(() => {
        const setState = (data) => {
            setIsAttached({})
            setPiled({})
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
            const rws = new ReconnectingWebSocket('ws://localhost:9000/api/game');
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
    }, [gameSocket, authTokens.user.userId, gameId, authTokens.token])

    const isActivePlayer = (state) => {
        if (state && state.stage) {
            const stage = state.stage
            return stage.stage && stage.data &&
                stage.data.currentPlayer != null &&
                parseInt(authTokens.user.userId) === state.players[stage.data.currentPlayer].userId
        } else return false
    }

    const resetAttached = (sourceIndex, newAttached, newUsing) => {
        if (newAttached[sourceIndex] != null) {
            let removePiled = [...piled[newAttached[sourceIndex]]]
            removePiled = removePiled.filter((c) => {
                return c.data.sourceIndex !== sourceIndex
            })
            newUsing[newAttached[sourceIndex]] = removePiled
            newAttached[sourceIndex] = null
        }
    }

    const registerDrop = (source, targetIndex, resetTarget) => {
        const sourceIndex = source.data.sourceIndex
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
    }

    const getArrangement = (player, currentAttached, currentPiled) => {
        const hand = player.hand
        let cardArrangement = []
        hand.forEach((card, index) => {
            if (currentAttached[index + SourceIndexes.HandIndex] == null) {
                cardArrangement.push(card)
            }
        })
        return cardArrangement
    }

    const renderGameStage = () => {
        if (!gameState) {
            return ""
        }
        let arrangement = []
        let stage = gameState.stage

        let activePlayer = isActivePlayer(gameState)
        if (player && player.hand.length > 0) {
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

    const registerHovered = (name, location) => {
        if (name && name !== "CardBack") {
            setHovered({name:name, location:location})
        } else {
            setHovered(null)
        }
    }

    const renderHovered = () => {
        if (hovered) {
            const imgStyle = {
                width: '300px',
                height: '375px',
                position: 'absolute',
                top: hovered.location.top,
                left: hovered.location.left,
                pointerEvents: 'none'
            };
                return (<img style={{imgStyle}} />)
            //            return (<img style={imgStyle} src={cardImages[hovered.name]} title={hovered.name} alt={hovered.name} />)
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
            registerHovered: registerHovered,
            renderHovered: renderHovered,
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
