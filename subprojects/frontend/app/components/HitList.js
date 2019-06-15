import React from 'react'
import Hit from './Hit'
import PropTypes from 'prop-types'

export default function HitList(props) {
    return (
        <div>
            <h3>Hits for {props.searchSequence}</h3>
            <ul>
                { props.hitList.map(hit => <Hit key={hit.header} header={hit.header} sequence={hit.sequence}/>) }
            </ul>
        </div>
    )
}

HitList.propTypes = {
    searchSequence: PropTypes.string,
    hitList: PropTypes.array
}
