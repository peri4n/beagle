import {Typography} from "@material-ui/core";
import React from "react";
import Link from "@material-ui/core/Link";

export const Copyright: React.FC = () => {
    return (
        <Typography variant="body2" color="textSecondary" align="center">
            {'Copyright © '}
            <Link color="inherit" href="https://beagle.io">
                Beagle
            </Link>{' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}

