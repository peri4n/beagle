import {systemReducer} from "./system/reducer";
import {combineReducers} from "redux";

export const rootReducer = combineReducers({
    system: systemReducer,
})


export type RootState = ReturnType<typeof rootReducer>
