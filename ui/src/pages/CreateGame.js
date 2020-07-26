import React, { useState, useEffect } from "react";
import { Redirect } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from "../context/auth";
import styled from "styled-components";
import {Button, Error} from "../components/AuthForm"

const TeamPicker = styled.select`
    width: 200px;
    margin-right: 10px;
    line-height: 1.1;
    background: none;
`;

const TeamTitle = styled.div`
    width: 200px;
`;

const MonthPicker = styled.select`
    display: flex;
    width: 200px;
    line-height: 1.1;
    padding: 6px 10px;
    background: none;
`;

const TimeOfDayPicker = styled.select`
    width: 150px;
    line-height: 1.1;
    padding: 6px 10px;
    background: none;
`;

const ParkPicker = styled.select`
    width: 200px;
    margin-left: 10px;
    line-height: 1.1;
    padding: 6px 10px;
    background: none;
`;

const GeoLocationPicker = styled.select`
    width: 200px;
    line-height: 1.1;
    padding: 6px 10px;
    background: none;
`;

const GameCreation = styled.div`
    display: flex;
    margin-left: 10px;
`;

const PropertiesLabel = styled.div`
    line-height: 1.7;
`;

const PropertiesRow = styled.div`
    display: flex;
    margin-left: 10px;
    justify-content: space-between;
    width: 100%;
    align-content: center;

`;

const GameProperties = styled.div`
    display: flex;
    flex-direction: column;
    height: 100%;
    justify-content: space-between;
`;

function CreateGame(props) {
    const { authTokens } = useAuth();
    const [lastError, setLastError] = useState("");
    const [visitingTeamId, setVisitingTeamId] = useState(0);
    const [homeTeamId, setHomeTeamId] = useState(0);
    const [month, setMonth] = useState("April")
    const [tod, setTod] = useState("Night")
    const [parkId, setParkId] = useState(0)
    const [geoId, setGeoId] = useState(0)
    const [selectedGame, setSelectedGame] = useState(0)
    const [teams, setTeams] = useState([]);
    const [parks, setParks] = useState([]);
    const [geos, setGeos] = useState([])

    function createGame() {
        if (validateForm()) {
            axios("/api/createGame", {
                data: {
                    visitingTeamId,
                    homeTeamId,
                    parkId,
                    geoId,
                    month,
                    tod
                },
                method: "post",
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Authorization': 'Bearer ' + authTokens.token
                }
            }).then(result => {
                setSelectedGame(result.data.gameId)
            }).catch(error => {
                if (error.response) {
                    setLastError(error.response.data)
                } else {
                    // Something happened in setting up the request that triggered an Error
                    setLastError("Problem creating the game, please try again");
                }
            });
        }
    }

    function validateForm() {
        let validated = true;
        if (visitingTeamId === 0) {
            setLastError("You need to select a visiting team");
            validated = false;
        }
        if (homeTeamId === 0) {
            setLastError("You need to select a home team");
            validated = false;
        }
        return validated;
    }

    function updateHomeTeam(event) {
        const selectedTeam = teams[event.target.options.selectedIndex]
        setHomeTeamId(selectedTeam.teamId)
        if (selectedTeam) {
            setParkId(selectedTeam.parkId)
            setGeoId(selectedTeam.geoId)
        }
    }

    useEffect(() => {
        const fetchData = async () => {
            await axios("api/teamData", {
                method: "get",
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Authorization': 'Bearer ' + authTokens.token
                },
            }).then(result => {
                setTeams(result.data.teams)
                setParks(result.data.parks)
                setGeos(result.data.geos)
            }).catch(() => {
                setLastError("Data fetch failed");
            })
        };
        fetchData();
    }, [authTokens.token])


    if (selectedGame > 0) {
        let location = "/game/" + selectedGame;
        return (<Redirect to={location} />);
    }

    return (
        <div>
            <GameCreation>
                <TeamTitle>Visiting Team</TeamTitle>
                <TeamTitle>Home Team</TeamTitle>
            </GameCreation>
            <GameCreation>
                <TeamPicker name="visitingTeam" size="10" onChange={(e) =>
                    setVisitingTeamId(teams[e.target.options.selectedIndex].teamId)}>
                    {teams.map((team) => <option key={team.teamId}>{team.name}</option>)}
                </TeamPicker>
                <TeamPicker name="homeTeam" size="10" onChange={updateHomeTeam}>
                    {teams.map((team) => <option key={team.teamId}>{team.name}</option>)}
                </TeamPicker>
                <GameProperties>
                    <PropertiesRow>
                        <PropertiesLabel>Park:</PropertiesLabel>
                        <ParkPicker name="park" size="1" onChange={(e) => setParkId(e.target.key)}>
                            <option key={0} />
                            {parks.map((park) => {
                                if (park.parkId === parkId) {
                                    return (<option selected key={park.parkId}>{park.name}</option>)
                                } else {
                                    return (<option key={park.parkId}>{park.name}</option>)
                                }
                            })}
                        </ParkPicker>
                    </PropertiesRow>
                    <PropertiesRow>
                        <PropertiesLabel>Region:</PropertiesLabel>
                        <GeoLocationPicker name="geo" size="1" onChange={(e) => setGeoId(e.target.key)}>
                            <option key={0} />
                            {geos.map((geo) => {
                                if (geo.geoId === geoId) {
                                    return (<option selected key={geo.geoId}>{geo.name}</option>)
                                } else {
                                    return (<option key={geo.geoId}>{geo.name}</option>)
                                }
                            })}
                        </GeoLocationPicker>
                    </PropertiesRow>
                    <PropertiesRow>
                        <PropertiesLabel>Month:</PropertiesLabel>
                        <MonthPicker name="month" size="1" defaultValue="April" onChange={(e) => setMonth(e.target.value)}>
                            <option value="April">March/April</option>
                            <option value="May">May</option>
                            <option value="June">June</option>
                            <option value="July">July</option>
                            <option value="August">August</option>
                            <option value="September">September</option>
                            <option value="October">October/November</option>
                        </MonthPicker>
                    </PropertiesRow>
                    <PropertiesRow>
                        <PropertiesLabel>Time Of Day:</PropertiesLabel>
                        <TimeOfDayPicker name="tod" size="1" defaultValue="Night" onChange={(e) => setTod(e.target.value)}>
                            <option value="Day">Day</option>
                            <option value="Night">Night</option>
                        </TimeOfDayPicker>
                    </PropertiesRow>
                </GameProperties>
            </GameCreation>
            <Button onClick={createGame}>Start Game</Button>
            {lastError && <Error>{lastError}</Error>}
        </div>
    );
}

export default CreateGame;
