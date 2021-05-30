import React from "react";
import { Button, Options } from "../components/InputElements"
import {useGameState} from "../context/GameState";
import PlayerHand from "../components/PlayerHand"
import Piles from "../components/Piles"

function Playing(props) {
    const { gameState, sendMessage, registerDrop } = useGameState()
    const dropOk = (pile, card) => {
        if (pile.direction === "Up") {
            return card > pile.topCard || card === pile.topCard - 10
        } else {
            return card < pile.topCard || card === pile.topCard + 10
        }
    }

    const playDroppedCards = (source, targetIndex, resetTarget) => {
        if (props.arrangement.cardsPlayed) {
            playCards()
        }
        registerDrop(source, targetIndex, resetTarget)
    }
    const getPlays = () => {
        return props.arrangement.piles.map((p, index) => {
            return {
                pile: index,
                cards: p.cards.map(c => {
                    return c.card
                })
            }
        })
    }
    const playCards = () => {
        sendMessage({
            messageType: 'PlayCards',
            data: {plays: getPlays()}
        })
    }

    const finishTurn = () => {
        sendMessage({
            messageType: 'FinishTurn'
        })
    }

    const renderChoices = (multiPlayer) => {
        const stage = gameState.stage
        let cannotPlay = true
        gameState.piles.forEach(p => {
            props.arrangement.cards.forEach(c => {
                if (dropOk(p, c.card)) {
                    cannotPlay = false;
                }
            })
        })
        if (props.activePlayer) {
            let options = []
            if (multiPlayer) {
                if (props.arrangement.cardsPlayed === 1) {
                    options.push(<Button key={'done'} onClick={() => playCards()}>Play</Button>)
                } else if (props.arrangement.cardsPlayed === 0 && stage.data.played >= stage.data.needToPlay) {
                    options.push(<Button key={'done'} onClick={() => finishTurn()}>Done</Button>)
                }
            } else {
                let showDone = props.arrangement.cardsPlayed >= stage.data.needToPlay
                if (multiPlayer) {
                    showDone = stage.data.played >= stage.data.needToPlay
                }
                if (showDone) {
                    options.push(<Button key={'done'} onClick={() => playCards()}>Done</Button>)
                } else if (cannotPlay) {
                    options.push(<Button key={'end'} onClick={() => playCards()}>End Game</Button>)
                }
            }
            return (
                <Options>
                    {options}
                </Options>
            )
        }
    }

    const multiPlayer = gameState.players.length > 1
    const currentPlayer = gameState.players.filter(p => p.userId === gameState.stage.data.currentPlayerId)
    let turnString = '';
    if (currentPlayer) {
        turnString = currentPlayer[0].name + " is playing."
    }
    return (
        <div>
            {props.activePlayer && <div>It's your Turn</div>}
            {!props.activePlayer && <div>{turnString}</div>}
            <PlayerHand arrangement={props.arrangement.cards} />
            <Piles piles={props.arrangement.piles}  registerDrop={playDroppedCards} />
            {renderChoices(multiPlayer)}
        </div>
    )
}

export default Playing;
