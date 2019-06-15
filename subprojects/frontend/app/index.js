import React, { useState } from 'react'
import { render } from 'react-dom'
import SearchForm from './components/SearchForm'
import FileUpload from './components/FileUpload'
import HitList from './components/HitList'

import { makeStyles} from '@material-ui/core/styles'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';


export default function App() {

    const [searchSequence, setSearchSequence] = useState('')
    const [hits, setHits] = useState([])
        
    const useStyles = makeStyles(theme => ({
            root: {
                flexGrow: 1,
            },
            menuButton: {
                marginRight: theme.spacing(2),
            },
            title: {
                flexGrow: 1,
            },
        }));

    const classes = useStyles()

    function  clearHits() {
        setHits([])
    }

    function addHit(hit) {
        setHits(prevHits => [hit, ...prevHits])
    }

    return (
        <div>
            <AppBar>
                <Toolbar>
                    <IconButton edge="start" className={classes.menuButton} color="inherit" aria-label="Menu">
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" className={classes.title}>
                        News
                    </Typography>
                    <Button color="inherit">Login</Button>
                </Toolbar>
            </AppBar>
            <div style={{ marginTop: 100 }}>
                <FileUpload />
                <SearchForm updateSearchSequence={setSearchSequence} clearHits={clearHits} addHit={addHit}/>
                <HitList searchSequence={searchSequence} hitList={hits}/>
            </div>
        </div>
    )
}

render(<App/>, document.getElementById('app-container'))
