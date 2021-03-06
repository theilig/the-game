import React, { useState } from 'react';
import { BrowserRouter as Router, Route } from "react-router-dom"
import GameList from './pages/GameList';
import Login from './pages/Login';
import { AuthContext } from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import CreateGame from "./pages/CreateGame"
import Game from "./pages/Game"

function App(props) {
    let existingTokens = localStorage.getItem("tokens");
    try {
        existingTokens = JSON.parse(existingTokens);
    } catch (e) {
        existingTokens = {};
    }
    const [authTokens, setAuthTokens] = useState(existingTokens);

    const setTokens = (data) => {
        localStorage.setItem("tokens", JSON.stringify(data));
        setAuthTokens(data);
    }

    return (
        <AuthContext.Provider value={{ authTokens, setAuthTokens: setTokens  }}>
            <Router>
                <div>
                    <PrivateRoute exact path="/" component={GameList}/>
                    <PrivateRoute exact path="/games" component={GameList}/>
                    <Route path="/login" component={Login} />
                    <Route path="/createGame" component={CreateGame} />
                    <Route path="/game/:gameId" component={Game} />
                </div>
            </Router>
        </AuthContext.Provider>
    );
}
export default App;
