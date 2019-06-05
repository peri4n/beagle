import React from 'react'
import { render } from 'react-dom'
import SearchForm from './components/SearchForm'
import FileUpload from './components/FileUpload'
import HitList from './components/HitList'

class App extends React.Component {

    constructor() {
        super()
        this.updateSearchSequence = this.updateSearchSequence.bind(this)
        this.addHit = this.addHit.bind(this)
        this.clearHits = this.clearHits.bind(this)
        this.state = {
            'search_sequence': '',
            'hits': []
        }
    }

    updateSearchSequence(sequence) {
        this.setState({
            'search_sequence': sequence
        })
    }

    clearHits() {
        this.setState({
            'hits': []
        })
    }

    addHit(hit) {
        this.setState(prevState => ({
            hits: [hit, ...prevState.hits]
        }))
    }

    render() {
        return (
            <div>
                <FileUpload />
                <SearchForm updateSearchSequence={this.updateSearchSequence} clearHits={this.clearHits} addHit={this.addHit}/>
                <HitList searchSequence={this.state.search_sequence} hitList={this.state.hits}/>
            </div>
        )
    }
}

render(<App/>, document.getElementById('app-container'))
