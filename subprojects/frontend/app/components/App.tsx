import React, { useState } from 'react'
import {SearchBar} from './SearchBar'
import {SearchResult} from './SearchResult'
import {Typography} from "@material-ui/core";
import {HitProps} from "./Hit";

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
        <div>
            <SearchBar updateSearchSequence={setSearchSequence} clearHits={clearHits} addHit={addHit}/>
            <Typography>Welcome to beagle</Typography>
            <SearchResult searchSequence={searchSequence} hitList={hits}/>
        </div>
    )
}
