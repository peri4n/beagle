import React from 'react'
import { render } from 'react-dom'

class App extends React.Component {

    constructor() {
        super()
        this.renderFile = this.renderFile.bind(this)
    }

    sendFile(file) {
        const reader = new FileReader()
        reader.onload = event => {
            fetch('http://localhost:8080/upload', {
                method: 'POST',
                headers: {
                    "Content-Type": "text/*"
                },
                body: event.target.result
            }).then(response => response.json())
            .then(json => console.log(json))
            .catch(error => console.log('There has been a problem with your fetch operation: ', error.message))
        }
        reader.readAsText(file)
    }

    renderFile(event) {
        event.preventDefault();
        let file = document.getElementById('fasta').files.item(0)
        this.sendFile(file)
    }

    render() {
        return (
            <form onSubmit={this.renderFile}>
                File: <input id="fasta" type="file" name="fasta-file"/><br/>
                <input type="submit" value="Submit"/>
            </form>
        );

    }
}

render(<App/>, document.getElementById('app-container'))
