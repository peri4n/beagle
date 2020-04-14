import React from 'react'
import {MuiThemeProvider} from "@material-ui/core/styles";
import {theme} from '../theme'
import {Dashboard} from "./dashboard/Dashboard";

export const App: React.FC = () => {
    return (
        <React.StrictMode>
            <MuiThemeProvider theme={theme}>
                <Dashboard/>
            </MuiThemeProvider>
        </React.StrictMode>
    )
}
