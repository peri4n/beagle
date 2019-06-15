import React from 'react'
import PropTypes from 'prop-types'

import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'

export default function Hit(props) {
    return (
        <ListItem alignItems="flex-start">
            <ListItemText
                primary={props.header}
                secondary={props.sequence}
            />
        </ListItem>
    )
}

Hit.propTypes = {
    header: PropTypes.string,
    sequence: PropTypes.string
}

