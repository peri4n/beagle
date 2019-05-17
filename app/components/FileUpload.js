import React from 'react'

class FileUpload extends React.Component {

    constructor() {
        super()
        this.fileInput = React.createRef()
        this.sendFile = this.sendFile.bind(this)
    }

    sendFile(event) {
        event.preventDefault();

        let [ file ] = this.fileInput.files
        const reader = new FileReader()

        // register callback for when the file is completely read
        reader.onload = event => {
            fetch('http://localhost:8080/upload', {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ 'text': event.target.result })
            }).catch(error => alert('There has been a problem with your fetch operation: ', error.message))
        }

        // read the file
        reader.readAsText(file)
    }

    render() {
        return (
            <form onSubmit={this.sendFile}>
                File: <input ref={(input) => this.fileInput = input} type="file" /><br/>
                <input type="submit" value="Submit"/>
            </form>
        )
    }
}

export default FileUpload
