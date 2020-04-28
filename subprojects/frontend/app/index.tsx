import React from "react";
import * as ReactDOM from 'react-dom';
import {App} from "./components/App";
import {createStore} from "redux";
import {Provider} from 'react-redux'
import {rootReducer} from "./store";

const store = createStore(rootReducer)

ReactDOM.render(
    <React.StrictMode>
        <Provider store={store}>
            <App/>
        </Provider>
    </React.StrictMode>
    , document.getElementById("app-container"));
