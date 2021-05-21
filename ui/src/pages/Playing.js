import React from "react";
import { useAuth } from "../context/auth";
import { Button, Options } from "../components/InputElements"
import {useGameState} from "../context/GameState";
import PlayerHand from "../components/PlayerHand"
import Piles from "../components/Piles"
import {SourceIndexes} from "../components/SlotIndexes";

function Playing(props) {
    const { gameState, sendMessage } = useGameState()
    const { authTokens } = useAuth()
    const playCards = () => {
        let plays = props.arrangement.piles.map((p, index) => {
            return {
                pile: index,
                cards: p.cards.map(c => {
                    return c.card
                })
            }
        })
        sendMessage({
            messageType: 'PlayCards',
            data: {plays: plays}
        })
    }

    const renderChoices = () => {
        const stage = gameState.stage
        let currentPlayerId = gameState.players[stage.data.currentPlayer].userId
        if (parseInt(authTokens.user.userId) === currentPlayerId) {
            if (props.arrangement.cardsPlayed >= stage.data.needToPlay) {
                return (
                    <Options>
                        <Button onClick={() => playCards()}>Done</Button>
                    </Options>
                )
            }
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
