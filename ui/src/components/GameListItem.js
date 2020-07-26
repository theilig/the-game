import React from "react";
import styled from "styled-components";

const Summary = styled.div`
    box-sizing: border-box;
    max-width: 200px;
    max-height: 100px;
    padding: 0 2rem;
    display: flex;
    align-items: left;
`;

const Team = styled.div`
    display: flex;
    width: 100%;
    max-height: 50px;
    font-weight: 400;
    font-size: xx-large;
    min-width: 175px;
`;

const TeamList = styled.div`
`;

const Logo = styled.img`
    height: 100%;
    margin-right: 1rem;
    max-height: 30px;
    align-self: center;
`;

const Abbrev = styled.div`
    align-self: center;
`;

const Score = styled.div`
    text-align: right;
    margin-left: 18px;
    align-self: end;
    margin-left: auto;
    margin-right: 30px;
`;

const InningHalf = styled.div`
    align-self: center;
`;

const InningNumber = styled.div`
    align-self: center;
    text-align: center;
`;

const InningSummary = styled.div`
    align-self: center;
    font-size: x-large
`;

function GameListItem(props) {
    return (
        <Summary>
            <TeamList>
                <Team>
                    <Logo src={props.game.state.road.icon}/>
                    <Abbrev>{props.game.state.road.name}</Abbrev>
                    <Score>{props.game.state.road.score}</Score>
                </Team>
                <Team>
                    <Logo src={props.game.state.home.icon}/>
                    <Abbrev>{props.game.state.home.name}</Abbrev>
                    <Score>{props.game.state.home.score}</Score>
                </Team>
            </TeamList>
            <InningSummary>
                <InningHalf>{props.game.state.inning.half}</InningHalf>
                <InningNumber>{props.game.state.inning.number}</InningNumber>
            </InningSummary>
        </Summary>
    );
}

export default GameListItem;
