import React from 'react'
import Hit from './Hit'

class HitList extends React.Component {
    render() {
        return (
                <div>
                    <h3>Hits for {this.props.searchSequence}</h3>
                    <ul>
                        { this.props.hitList.map(hit => <Hit key={hit.header} header={hit.header} sequence={hit.sequence}/>) }
                    </ul>
                </div>
        )
    }
}

export default HitList
