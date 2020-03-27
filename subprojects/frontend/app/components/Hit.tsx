import React from 'react'

import ListItem from '@material-ui/core/ListItem'
import ListItemText from '@material-ui/core/ListItemText'

export interface HitProps {
    header: string;
    sequence: string;
}

export const Hit: React.FC<HitProps> = (props: HitProps) => {
    return (
        <ListItem alignItems="flex-start">
            <ListItemText
                primary={props.header}
                secondary={props.sequence}
            />
        </ListItem>
    )
}
