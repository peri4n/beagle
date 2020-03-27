import React from 'react'
import {Hit, HitProps} from './Hit'
import {Typography} from "@material-ui/core";

export interface SearchResultProps {
    searchSequence: string;
    hitList: HitProps[];
}

export const SearchResult: React.FC<SearchResultProps> = (props: SearchResultProps) => {
    return (
        <div>
            <Typography variant='h3'>Hits for {props.searchSequence}</Typography>
            <ul>
                {props.hitList.map((hit: HitProps, index: number) =>
                    <Hit key={index}
                         header={hit.header}
                         sequence={hit.sequence}/>)}
            </ul>
        </div>
    )
}
