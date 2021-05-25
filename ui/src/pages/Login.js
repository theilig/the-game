import React, { useState } from "react";
import { Redirect } from 'react-router-dom';
import axios from 'axios';
import logoImg from '../img/logo.png';
import { Card, Logo, Form, Input, Button} from '../components/AuthForm';
import { useAuth } from "../context/auth";

function Login(props) {
    const [isLoggedIn, setLoggedIn] = useState(false);
    const [name, setName] = useState("")
    const { setAuthTokens } = useAuth();
    const referrer = props.location.state ?
        (props.location.state.referrer.pathname || '/') :
        '/';

    function postLogin() {
        if (validateForm()) {
            axios("/api/login", {
                data: {
                    name
                },
                method: "post",
                headers: {'X-Requested-With': 'XMLHttpRequest'},
                withCredentials: true
            }).then(result => {
                setAuthTokens(result.data);
                setLoggedIn(true);
            })
        }
    }

    function validateForm() {
        let validated = true;
        if (name.length === 0) {
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
                    type="name"
                    value={name}
                    onChange={e => {
                        setName(e.target.value);
                    }}
                    placeholder="name"
                />
                <Button onClick={postLogin}>Play</Button>
            </Form>
        </Card>
    );
}

export default Login;
