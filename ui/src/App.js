import React, { useState } from 'react';
import { BrowserRouter as Router, Route } from "react-router-dom"
import GameList from './pages/GameList';
import Login from './pages/Login';
import Signup from "./pages/Signup";
import { AuthContext } from "./context/auth";
import PrivateRoute from "./PrivateRoute";
import Game from "./pages/Game"
import axios from "axios";
import {Switch} from "react-router";

function App(props) {
    let existingTokens = localStorage.getItem("tokens");
    try {
        existingTokens = JSON.parse(existingTokens);
    } catch (e) {
        existingTokens = {};
    }
    const [authTokens, setAuthTokens] = useState(existingTokens);
    const [isLoggedIn, setIsLoggedIn] = useState(existingTokens != null)

    const setTokens = (data) => {
        localStorage.setItem("tokens", JSON.stringify(data));
        setAuthTokens(data);
    }

    const logout = () => {
        setTokens({})
        setIsLoggedIn(false)
    }

    const confirm = (email, token) => {
        axios("/api/confirm", {
            data: {
                email,
                token
            },
            method: "post",
            headers: {'X-Requested-With': 'XMLHttpRequest'},
            withCredentials: true
        }).then(result => {
            setTokens(result.data);
            setIsLoggedIn(true)
            return null
        }).catch(error => {
            return error
        });
    }

    const NotFound = () => (
        <div>
            <h1>404 - Not Found!</h1>
        </div>
    )

    return (
        <AuthContext.Provider value={{ authTokens, confirm: confirm, logout: logout, isLoggedIn: isLoggedIn }}>
            <Router>
                <Switch>
                    <PrivateRoute exact path="/" component={GameList}/>
                    <PrivateRoute exact path="/games" component={GameList}/>
                    <Route path="/login" component={Login} />
                    <Route path="/signup" component={Signup} />
                    <Route path="/game/:gameId" component={Game} />
                    <Route path="/confirm/:email/:confirmationToken" component={GameList} />
                    <Route component={NotFound} />
                </Switch>
            </Router>
        </AuthContext.Provider>
    );
}
export default App;
