import React, {ChangeEvent, useState} from 'react'

import {FileUpload} from './FileUpload'

import {fade, makeStyles, Theme} from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import Button from '@material-ui/core/Button'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import SearchIcon from '@material-ui/icons/Search'
import InputBase from '@material-ui/core/InputBase'
import useTheme from "@material-ui/core/styles/useTheme";
import {HitProps} from "./Hit";

export interface SearchBarProps {
    updateSearchSequence: (sequence: string) => void
    clearHits: () => void
    addHit: (hit: HitProps) => void
}

interface SearchResults {
    sequences: HitProps[]
}

export const SearchBar: React.FC<SearchBarProps> = (props: SearchBarProps) => {

    const useStyles = makeStyles((theme: Theme) => ({
        root: {
            flexGrow: 1,
        },
        menuButton: {
            marginRight: theme.spacing(2),
        },
        inputRoot: {
            color: 'inherit',
        },
        inputInput: {
            padding: theme.spacing(1, 1, 1, 7),
            transition: theme.transitions.create('width'),
            width: '100%',
            [theme.breakpoints.up('md')]: {
                width: '700px',
            },
        },
        search: {
            position: 'relative',
            borderRadius: theme.shape.borderRadius,
            backgroundColor: fade(theme.palette.common.white, 0.15),
            '&:hover': {
                backgroundColor: fade(theme.palette.common.white, 0.25),
            },
            marginRight: theme.spacing(2),
            marginLeft: 0,
            width: '100%',
            [theme.breakpoints.up('sm')]: {
                marginLeft: theme.spacing(3),
                width: 'auto',
            },
        },
        searchIcon: {
            width: theme.spacing(7),
            height: '100%',
            position: 'absolute',
            pointerEvents: 'none',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
        },
    }));

    const classes = useStyles(useTheme())

    const [searchSequence, setSearchSequence] = useState('')

    function handleSearchButton(event: React.MouseEvent<HTMLButtonElement, MouseEvent>) {
        event.preventDefault()
        search()
    }

    function formatSearchResults(result: any): SearchResults {
        return { sequences: result.sequences};
    }

    function search() {
        props.clearHits()
        fetch('http://localhost:8080/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({'sequence': searchSequence})
        }).then(response => response.json())
            .then(formatSearchResults)
            .then(hits => hits.sequences.forEach(hit => props.addHit(hit)))
            .catch(_ => alert('There has been a problem with your fetch operation: '))
    }

    function handleEnter(event: React.KeyboardEvent<HTMLDivElement>) {
        if (event.key === 'Enter') {
            search()
        }
    }

    function updateSequence(event: ChangeEvent<HTMLInputElement>) {
        const sequence = event.target.value
        setSearchSequence(sequence)
        props.updateSearchSequence(sequence)
    }

    return (
        <React.Fragment>
            <AppBar position="fixed">
                <Toolbar>
                    <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="Menu">
                        <MenuIcon/>
                    </IconButton>
                    <Typography variant="h6">
                        Beagle
                    </Typography>
                    <div className={classes.search} onKeyPress={handleEnter}>
                        <div className={classes.searchIcon}>
                            <SearchIcon/>
                        </div>
                        <InputBase
                            placeholder="Search…"
                            classes={{
                                root: classes.inputRoot,
                                input: classes.inputInput,
                            }}
                            inputProps={{'aria-label': 'Search'}}
                            onChange={updateSequence}
                        />
                    </div>
                    <Button color="inherit" onClick={handleSearchButton}>Search</Button>
                    <FileUpload/>
                </Toolbar>
            </AppBar>
            {/* This toolbar is needed so that content isn't hidden by the first. */}
            <Toolbar/>
        </React.Fragment>
    )
}
