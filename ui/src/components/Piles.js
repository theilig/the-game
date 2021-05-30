import React from "react";
import styled from "styled-components";
import Pile from "./Pile";
import {SourceIndexes} from "./SlotIndexes";

const PileContainer = styled.div`
    display: flex;
    flex-direction: row;
    margin-bottom: 20px;
`;

function Piles(props) {
    return (
        <PileContainer>
            {props.piles.map((pile, index) => {
                return (
                    <Pile
                        direction={pile.direction}
                        topCard={pile.topCard}
                        index={SourceIndexes.PileOffset + index}
                        topCardIndex={pile.topCardIndex}
                        registerDrop={props.registerDrop}
                        key={index}
                    />
                )
            })}
        </PileContainer>
    )
}

export default Piles;