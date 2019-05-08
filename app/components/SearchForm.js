import React from 'react'
import { render } from 'react-dom'

class SearchForm extends React.Component {

    constructor() {
        super()
        this.searchSequence = this.searchSequence.bind(this)
        this.updateSequence = this.updateSequence.bind(this)
        this.state = {
            "sequence": ""
        }
    }

    searchSequence(event) {
        event.preventDefault()
        fetch('http://localhost:8080/search', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: this.state.sequence
        }).then(response => {
            console.log(response)
            console.log('first try')
            return response.json()
        }).then(json => {
            console.log(json)
            console.log('second try')
        })
        .catch(error => console.log('There has been a problem with your fetch operation: ', error.message))
    }

    updateSequence(event) {
        this.setState({
            "sequence": event.target.value
        })
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
