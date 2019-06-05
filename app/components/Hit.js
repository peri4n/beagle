import React from 'react'
import PropTypes from 'prop-types'

class Hit extends React.Component {
    render() {
        return (
            <div>
                <h3>{this.props.header}</h3>
                <p>{this.props.sequence}</p>
            </div>
        )
    }
}

Hit.propTypes = {
    header: PropTypes.string,
    sequence: PropTypes.string
}

export default Hit

