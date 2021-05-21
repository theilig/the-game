import React from "react";
import styled from "styled-components";
import HandCard from "./HandCard";
import {SourceIndexes, TargetIndexes} from "./SlotIndexes";

const HandContainer = styled.div`
    display: flex;
    flex-direction: row;
    margin-bottom: 20px;
`;

function PlayerHand(props) {
    return (
        <HandContainer>
            {props.arrangement.map((column, index) => {
                return (
                        <HandCard card={column}
                                  index={SourceIndexes.HandIndex + index}
                                  key={SourceIndexes.HandIndex + index}
                        />
                )
            })}
        </HandContainer>
    )
}

export default PlayerHand;