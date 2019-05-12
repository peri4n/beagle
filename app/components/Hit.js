import React from 'react'
import { render } from 'react-dom'

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

export default Hit
