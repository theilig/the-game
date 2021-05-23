import React from "react";
import {useGameState} from "../context/GameState";
import styled from "styled-components";
const OutsideBox = styled.div`
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    width: 288px;
`;

const Row = styled.div`
    display: flex;
    flex-direction: row;
`;

const Available = styled.div`
    background: lightgreen;
    flex: 1;
    text-align: right;
    border: solid;
    border-width: 1px;
`;

const Used = styled.div`
    background:red;
    flex: 1;
    text-align: right;
    border: solid;
    border-width: 1px;
`;

function Deck(props) {
    const {gameState} = useGameState()
    let deckMap = {}
    gameState.deck.forEach(card => deckMap[card] = true)

    const displayRow = (startingIndex) => {
        let numbers = []
        for (let j = 0; j < 10; j++) {
            let card = startingIndex + j
            if (deckMap[card]) {
                numbers.push(<Available key={card}>{card}</Available>)
            } else {
                numbers.push(<Used key={card}>{card}</Used>)
            }
        }
        return (
            <Row key={startingIndex}>
                {numbers}
             </Row>
        )
    };
    let rows = []
    for (let i = 1; i < 101; i+=10) {
        rows.push(displayRow(i))
    }
    return (
        <OutsideBox>
            {rows}
        </OutsideBox>
    )
}

export default Deck;