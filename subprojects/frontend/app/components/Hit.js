import React from 'react'
import PropTypes from 'prop-types'

export default function Hit(props) {
    return (
        <div>
            <h3>{props.header}</h3>
            <p>{props.sequence}</p>
        </div>
    )
}

Hit.propTypes = {
    header: PropTypes.string,
    sequence: PropTypes.string
}

