import React, {ChangeEvent, useState} from "react";
import {Typography} from "@material-ui/core";
import AppBar from "@material-ui/core/AppBar";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import {useDispatch} from "react-redux";
import {updateSession} from "../../store/system/actions";
import {Link} from "react-router-dom";
import { useHistory } from 'react-router'

export const Login: React.FC = () => {

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')

    const dispatch = useDispatch()
    const { push } = useHistory()

    const sendAuth = (e: React.MouseEvent) => {
        e.preventDefault()
        dispatch(updateSession({
            // always login :)
            loggedIn: true,
            userName: username,
        }))
        push('/dashboard')
    }

    const updateUsername = (e: ChangeEvent<HTMLInputElement>) => {
        setUsername(e.target.value)
    }

    const updatePassword = (e: ChangeEvent<HTMLInputElement>) => {
        setPassword(e.target.value)
    }

    return (
        <div>
            <AppBar
                title="Login"
            />
            <Typography component={"h1"}>Login</Typography>
            <TextField
                value={username}
                helperText="Enter your Username"
                label="username"
                onChange={updateUsername}
            />
            <br/>
            <TextField
                value={password}
                type="password"
                helperText="Enter your Password"
                label="password"
                onChange={updatePassword}
            />
            <br/>
            <Button variant="contained" color="primary" onClick={sendAuth}>Submit</Button>
        </div>
    );
}
