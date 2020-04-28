import React from "react"
import {MuiThemeProvider} from "@material-ui/core/styles";
import {theme} from "../theme"
import {Dashboard} from "./dashboard/Dashboard";
import {BrowserRouter as Router, Switch, Route} from "react-router-dom"
import {Login} from "./login/Login";

export const App: React.FC = () => {
    return (
        <MuiThemeProvider theme={theme}>
            <Router>
                <Switch>
                    <Route path="/" exact component={Login}/>
                    <Route path="/dashboard">
                        <Dashboard/>
                    </Route>
                </Switch>
            </Router>
        </MuiThemeProvider>
    )
}
