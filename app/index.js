import React from 'react'
import { render } from 'react-dom'

class App extends React.Component {
    render() {
        return (
            <form>
                Identifier: <input type="text" name="identifier"/><br/>
                Sequence: <input type="text" name="sequence"/><br/>
                <input type="submit" value="Submit"/>
            </form>
        );

    }
}

render(<App/>, document.getElementById('app-container'))
