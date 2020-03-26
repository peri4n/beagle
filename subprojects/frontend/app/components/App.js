import React, { useState } from 'react'
import SearchBar from './SearchBar'
import HitList from './HitList'
import {Typography} from "@material-ui/core";

export default function App(){

    const [searchSequence, setSearchSequence] = useState('')
    const [hits, setHits] = useState([])


    function clearHits() {
        setHits([])
    }

    function addHit(hit) {
        setHits(prevHits => [hit, ...prevHits])
    }

    return (
        <div>
            <SearchBar updateSearchSequence={setSearchSequence} clearHits={clearHits} addHit={addHit}/>
            <Typography>Welcome to beagle</Typography>
            <div>
                <HitList searchSequence={searchSequence} hitList={hits}/>
            </div>
        </div>
    )
}
