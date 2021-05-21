import React from "react";
import { useAuth } from "../context/auth";
import { Button, Options } from "../components/InputElements"
import {useGameState} from "../context/GameState";
import PlayerHand from "../components/PlayerHand"
import Piles from "../components/Piles"

function Playing(props) {
    const { gameState, sendMessage } = useGameState()
    const { authTokens } = useAuth()
    const renderChoices = () => {
        const stage = gameState.stage
        if (parseInt(authTokens.user.userId) === stage.data.currentPlayerId) {
            return "";
        }
    }

    let piles = []
    gameState.piles.map((pile, index) => {
        let newPile = {"direction": pile.direction, "topCard": pile.topCard}
        props.arrangement.forEach((arrangement) => {
            if (index === arrangement.pile) {
                newPile.topCard = arrangement.card
            }
        })
        piles.push(newPile)
    })

    return (
        <div>
            <PlayerHand arrangement={props.arrangement} />
            <Piles piles={piles} />
            {renderChoices()}
        </div>
    )
}

export default Playing;



