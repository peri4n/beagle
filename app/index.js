import React from 'react'
import { render } from 'react-dom'

class App extends React.Component {

    printFile(file) {
        const reader = new FileReader()
        reader.onload = function(evt) {
            console.log(evt.target.result)
        }
        reader.readAsText(file)
    }

    test(event) {
        event.preventDefault();
        let file = document.getElementById('fasta').files.item(0)
        console.log(file)
        this.printFile(file)
    }

    render() {
        return (
            <form onSubmit={this.test.bind(this)}>
                File: <input id="fasta" type="file" name="fasta-file"/><br/>
                <input type="submit" value="Submit"/>
            </form>
        );

    }
}

render(<App/>, document.getElementById('app-container'))
