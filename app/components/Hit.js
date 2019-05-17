import React from 'react'

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
