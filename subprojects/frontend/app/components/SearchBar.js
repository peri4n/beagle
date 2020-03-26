import React, {useState} from 'react'
import PropTypes from 'prop-types'

import FileUploadDialog from './FileUploadDialog'

import {fade, makeStyles} from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import Button from '@material-ui/core/Button'
import IconButton from '@material-ui/core/IconButton'
import MenuIcon from '@material-ui/icons/Menu'
import SearchIcon from '@material-ui/icons/Search'
import InputBase from '@material-ui/core/InputBase'

export default function SearchBar(props) {

    const useStyles = makeStyles(theme => ({
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

    const classes = useStyles()

    const [searchSequence, setSearchSequence] = useState('')

    function search(event) {
        event.preventDefault()
        props.clearHits()
        fetch('http://localhost:8080/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({'sequence': searchSequence})
        })
            .then(response => response.json())
            .then(hits => hits.sequences.forEach(hit => props.addHit(hit)))
            .catch(error => alert('There has been a problem with your fetch operation: '))
    }

    function handleEnter(event) {
        if (event.key === 'Enter') {
            search(event)
        }
    }

    function updateSequence(event) {
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
                    <Button color="inherit" onClick={search}>Search</Button>
                    <FileUploadDialog/>
                </Toolbar>
            </AppBar>
            {/* This toolbar is needed so that content isn't hidden by the first. */ }
            <Toolbar/>
        </React.Fragment>
    )
}

SearchBar.propTypes = {
    clearHits: PropTypes.func.isRequired,
    addHit: PropTypes.func.isRequired,
    updateSearchSequence: PropTypes.func.isRequired
}
