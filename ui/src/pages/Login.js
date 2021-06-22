import React, { useState } from "react";
import { Link, Redirect } from 'react-router-dom';
import axios from 'axios';
import logoImg from '../img/logo.png';
import { Card, Logo, Form, Input, Error, EmailRegex } from '../components/AuthForm';
import { Button } from "../components/InputElements"
import { useAuth } from "../context/auth";

function Login(props) {
    const [isLoggedIn, setLoggedIn] = useState(false);
    const [lastError, setLastError] = useState("");
    const [confirming, setConfirming] = useState(false)
    const [token, setToken] = useState("")
    const [email, setEmail] = useState("")
    const { confirm } = useAuth();
    const referrer = props.location.state ?
        (props.location.state.referrer.pathname || '/') :
        '/';

    function postConfirming() {
        if (validateForm()) {
            const error = confirm(email, token)
            if (error) {
                if (error.response) {
                    setLastError(error.response.data)
                } else {
                    // Something happened in setting up the request that triggered an Error
                    setLastError("Problem connecting to login server, please try again");
                }
            } else {
                setLoggedIn(true)
            }
        }
    }
    function postLogin() {
        if (validateForm()) {
            axios("/api/login", {
                data: {
                    email
                },
                method: "post",
                headers: {'X-Requested-With': 'XMLHttpRequest'},
                withCredentials: true
            }).then( _ => {
                setConfirming(true)
            }).catch(error => {
                if (error.response) {
                    setLastError(error.response.data)
                } else {
                    // Something happened in setting up the request that triggered an Error
                    setLastError("Problem connecting to login server, please try again");
                }
            });
        }
    }

    function validateForm() {
        let validated = true;
        if (email.length === 0 || !EmailRegex.test(email)) {
            setLastError("you need to sign in with an email");
            validated = false;
        }
        if (confirming && token.length === 0) {
            setLastError("you need the token from your e-mail to confirm");
            validated = false;
        }
        return validated;
    }

    if (isLoggedIn) {
        return <Redirect to={referrer} />;
    }

    return (
        <Card>
            <Logo src={logoImg} />
            <Form>
                <Input
                    type="email"
                    value={email}
                    onChange={e => {
                        setEmail(e.target.value);
                    }}
                    placeholder="email"
                />
                {confirming &&
                <Input
                    type="confirmationCode"
                    value={token}
                    onChange={e => {
                        setToken(e.target.value);
                    }}
                    placeholder="check your e-mail for code"
                />
                }
                {confirming && <Button onClick={postConfirming}>Confirm</Button>}
                {confirming && <Button onClick={postLogin}>Send Confirmation Again</Button>}
                {!confirming && <Button onClick={postLogin}>Sign In</Button>}
            </Form>
            <Link to="/signup">Sign Up for New Account</Link>
            { lastError && <Error>{lastError}</Error> }
        </Card>
    );
}

export default Login;
