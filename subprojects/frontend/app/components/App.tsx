import React, { useState } from 'react'
import {SearchBar} from './SearchBar'
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import {SearchResult} from './SearchResult'
import {Typography} from "@material-ui/core";
import {HitProps} from "./Hit";
import {MuiThemeProvider} from "@material-ui/core/styles";
import {theme} from '../theme'
import Divider from "@material-ui/core/Divider";
import List from "@material-ui/core/List";
import Drawer from "@material-ui/core/Drawer";
import IconButton from "@material-ui/core/IconButton";
import {mainListItems, secondaryListItems} from "./dashboard/drawer_items";
import clsx from "clsx";
import {Dashboard} from "./dashboard/Dashboard";

export const App: React.FC = () => {

    const [searchSequence, setSearchSequence] = useState('')
    const [hits, setHits] = useState<HitProps[]>([])


    function clearHits() {
        setHits([])
    }

    function addHit(hit: HitProps) {
        setHits(prevHits => [hit, ...prevHits])
    }

    return (
        <React.StrictMode>
            <MuiThemeProvider theme={theme}>
                <Dashboard />
                {/*<SearchBar updateSearchSequence={setSearchSequence} clearHits={clearHits} addHit={addHit}/>*/}
                {/*<Typography>Welcome to beagle</Typography>*/}
                {/*<SearchResult searchSequence={searchSequence} hitList={hits}/>*/}
            </MuiThemeProvider>
        </React.StrictMode>
    )
}
