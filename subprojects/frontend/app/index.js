import React, { useState } from 'react'
import { render } from 'react-dom'
import SearchBar from './components/SearchBar'
import FileUpload from './components/FileUpload'
import HitList from './components/HitList'

export default function App() {

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
            <div style={{ marginTop: 100 }}>
                <FileUpload />
                <HitList searchSequence={searchSequence} hitList={hits}/>
            </div>
        </div>
    )
}

render(<App/>, document.getElementById('app-container'))
