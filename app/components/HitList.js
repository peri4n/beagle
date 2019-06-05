import React from 'react'
import Hit from './Hit'
import PropTypes from 'prop-types'

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

HitList.propTypes = {
    searchSequence: PropTypes.string,
    hitList: PropTypes.array
}

export default HitList
