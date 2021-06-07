import React, {useState} from "react";
import { Link } from "react-router-dom";
import axios from 'axios';
import logoImg from "../img/logo.png";
import { Card, Logo, Form, Input, Error, Success, EmailRegex } from '../components/AuthForm';
import { Button } from "../components/InputElements"

function Signup() {
    const [email, setEmail] = useState("")
    const [lastError, setLastError] = useState("");
    const [name, setName] = useState("")
    const [success, setSuccess] = useState("")

    function postSignup() {
        if (validateForm()) {
            axios("api/signup", {
                data: {
                    email,
                    name
                },
                method: "post",
                headers: {'X-Requested-With': 'XMLHttpRequest'},
                withCredentials: true
            }).then(result => {
                setLastError("");
                setSuccess(result.data)
            }).catch(error => {
                if (error.response) {
                    setLastError(error.response.data)
                } else {
                    setLastError("There was an error trying to submit your request, please try again");
                }
            });
        }
    }
    function validateForm() {
        let validated = true;
        if (name.length === 0) {
            setLastError("you must enter a name")
        }
        if (email.length === 0) {
            setLastError("you need to provide an email");
            validated = false;
        }
        if (!EmailRegex.test(email)) {
            setLastError("Invalid email address");
            validated = false;
        }

        return validated;
    }

    return (
        <Card>
            <Logo src={logoImg}/>
            <Form>
                <Input
                    type="text"
                    value={name}
                    onChange={e => {
                        setName(e.target.value);
                    }}
                    placeholder="Name"
                />
                <Input
                    type="email"
                    value={email}
                    onChange={e => {
                        setEmail(e.target.value);
                    }}
                    placeholder="email"
                />
                <Button onClick={postSignup}>Sign Up</Button>
            </Form>
            <Link to="/login">Log into an existing account</Link>
            { lastError && <Error>{lastError}</Error> }
            { success && <Success>{success}</Success> }
        </Card>
    );
}

export default Signup;
