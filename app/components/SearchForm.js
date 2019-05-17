import React from 'react'
import { render } from 'react-dom'

class SearchForm extends React.Component {

    constructor() {
        super()
        this.searchSequence = this.searchSequence.bind(this)
        this.updateSequence = this.updateSequence.bind(this)
        this.state = {
            'sequence': ''
        }
    }

    searchSequence(event) {
        event.preventDefault()
        this.props.clearHits()
        console.log(`Searching for ${this.state.sequence}.`)
        fetch('http://localhost:8080/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 'sequence': this.state.sequence })
        })
            .then(response => response.json())
            .then(hits => hits.sequences.forEach( hit => this.props.addHit(JSON.parse(hit))))
            .catch(error => console.log('There has been a problem with your fetch operation: ', error.message))
    }

    updateSequence(event) {
        const sequence = event.target.value
        this.setState({
            'sequence': sequence
        })
        this.props.updateSearchSequence(sequence)
    }

    render() {
        return (
            <form onSubmit={this.searchSequence}>
                Sequence: <input type="text" onChange={this.updateSequence}/><br/>
                <input type="submit" value="Submit"/>
            </form>
        );

    }
}

export default SearchForm
