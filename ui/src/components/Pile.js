import React from "react";
import { useDrop } from 'react-dnd'
import {useGameState} from "../context/GameState"
import Up from "../img/Up.png"
import Down from "../img/Down.png"
import HandCard from "./HandCard";
import styled from "styled-components";
import cardImages from "../img/cards/cards";
const PileDirection = styled.img`
    margin-bottom: -10px;
    margin-left: 21px;
    width: 50px;
`;

function Pile(props) {
    const dropOk = (pile, card) => {
        if (pile.direction === "Up") {
            return card > pile.topCard || card === pile.topCard - 10
        } else {
            return card < pile.topCard || card === pile.topCard + 10
        }
    }

    const [, drop] = useDrop({
        accept: ['Card'],
        canDrop: ((item) => {
            return dropOk(props, item.card)
        }),
        drop: (c) => {
            props.registerDrop(c, props.index)
        }
    })

    let cardMap = {
        "Up": Up,
        "Down": Down
    }

    if (props.topCardIndex) {
        return (
            <div ref={drop}>
                <PileDirection src={cardMap[props.direction]} alt={props.direction}/>
                <HandCard card={props.topCard} index={props.topCardIndex}/>
            </div>
        )
    } else {
        let style = {width: '70px', height: '100px', marginLeft: '10px', marginTop: '10px'}
        return (
            <div ref={drop}>
                <PileDirection src={cardMap[props.direction]} alt={props.direction}/>
                <div>
                    <img style={style}
                         src={cardImages[props.topCard]} title={props.topCard} alt={props.topCard}
                    />
                </div>
            </div>
        )
    }
}

export default Pile;