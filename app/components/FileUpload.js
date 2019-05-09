import React from 'react'

class FileUpload extends React.Component {

    constructor() {
        super()
        this.fileInput = React.createRef()
        this.renderFile = this.renderFile.bind(this)
    }

    sendFile(file) {
        const reader = new FileReader()

        // register callback for when the file is completely read
        reader.onload = event => {
            fetch('http://localhost:8080/upload', {
                method: 'POST',
                headers: {
                    "Content-Type": "text/*"
                },
                body: event.target.result
            }).then(response => {
                console.log(response)
                return response.json()
            }).then(json => console.log(json))
            .catch(error => console.log('There has been a problem with your fetch operation: ', error.message))
        }

        // read the file
        reader.readAsText(file)
    }

    renderFile(event) {
        event.preventDefault();
        let file = this.fileInput.files.item(0)
        this.sendFile(file)
    }

    render() {
        return (
            <form onSubmit={this.renderFile}>
                File: <input ref={(input) => this.fileInput = input} type="file" /><br/>
                <input type="submit" value="Submit"/>
            </form>
        )
    }
}

export default FileUpload
