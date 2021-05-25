import React from "react";
import { useAuth } from "../context/auth";
import { Button, Options } from "../components/InputElements"
import {useGameState} from "../context/GameState";
import PlayerHand from "../components/PlayerHand"
import Piles from "../components/Piles"

function Playing(props) {
    const { gameState, sendMessage } = useGameState()
    const { authTokens } = useAuth()
    const dropOk = (pile, card) => {
        if (pile.direction === "Up") {
            return card > pile.topCard || card === pile.topCard - 10
        } else {
            return card < pile.topCard || card === pile.topCard + 10
        }
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

    const endGame = () => {
        sendMessage({
            messageType: 'EndGame',
            data: {plays: getPlays()}
        })
    }

    const renderChoices = () => {
        const stage = gameState.stage
        let currentPlayerId = stage.data.currentPlayerId
        let cannotPlay = true
        gameState.piles.forEach(p => {
            props.arrangement.cards.forEach(c => {
                if (dropOk(p, c.card)) {
                    cannotPlay = false;
                }
            })
        })
        if (parseInt(authTokens.user.userId) === currentPlayerId) {
            let options = []
            if (props.arrangement.cardsPlayed >= stage.data.needToPlay) {
                options.push(<Button key={'done'} onClick={() => playCards()}>Done</Button>)
            } else if (cannotPlay) {
                options.push(<Button key={'end'} onClick={() => endGame()}>End Game</Button>)
            }
            return (
                <Options>
                    {options}
                </Options>
            )
        }
    }

    return (
        <div>
            <PlayerHand arrangement={props.arrangement.cards} />
            <Piles piles={props.arrangement.piles} />
            {renderChoices()}
        </div>
    )
}

export default Playing;
