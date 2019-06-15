import React, { useState } from 'react'
import PropTypes from 'prop-types'

export default function SearchForm(props) {

    const [searchSequence, setSearchSequence] = useState('')

    const search = (event) => {
        event.preventDefault()
        props.clearHits()
        fetch('http://localhost:8080/search', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ 'sequence': searchSequence })
        })
            .then(response => response.json())
            .then(hits => hits.sequences.forEach( hit => props.addHit(hit)))
            .catch(error => alert('There has been a problem with your fetch operation: ', error.message))
    }

    const updateSequence = (event) => {
        const sequence = event.target.value
        setSearchSequence(sequence)
        props.updateSearchSequence(sequence)
    }

    return (
        <form onSubmit={search}>
            Sequence: <input type="text" onChange={updateSequence}/><br/>
            <input type="submit" value="Submit"/>
        </form>
    )
}

SearchForm.propTypes = {
    clearHits: PropTypes.func.isRequired,
    addHit: PropTypes.func.isRequired,
    updateSearchSequence: PropTypes.func.isRequired
}
