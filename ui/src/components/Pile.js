import React from "react";
import { useDrop } from 'react-dnd'
import {useGameState} from "../context/GameState"
import Up from "../img/Up.png"
import Down from "../img/Down.png"
import HandCard from "./HandCard";
import styled from "styled-components";
const PileDirection = styled.img`
    margin-bottom: -10px;
    margin-left: 21px;
    width: 50px;
`;


function Pile(props) {
    const {registerDrop} = useGameState()
    const [, drop] = useDrop({
        accept: ['Card'],
        canDrop: ((item) => {
            return true
        }),
        drop: (c) => {
            registerDrop(c.card, props.cards[0].data.sourceIndex)
        }
    })

    let cardMap = {
        "Up": Up,
        "Down": Down
    }

    return (
        <div ref={drop}>
            <PileDirection src={cardMap[props.direction]} alt={props.direction} />
            <HandCard card={props.topCard} />
        </div>
    )
}

export default Pile;