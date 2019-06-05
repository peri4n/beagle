import React from 'react'

class FileUpload extends React.Component {

    constructor() {
        super();
        this.fileInput = React.createRef();
        this.sendFile = this.sendFile.bind(this);
    }

    sendFile(event) {
        event.preventDefault();

        const files = this.fileInput.files;
        const formData = new FormData();

        for (var i = 0; i < files.length; i++) {
            formData.append('file', files[i]);
        }

        fetch('http://localhost:8080/upload', {
            method: 'POST',
            body: formData
        }).catch(error => alert('There has been a problem with your fetch operation: ', error.message));
    }

    render() {
        return (
            <form onSubmit={this.sendFile}>
                File: <input ref={(input) => this.fileInput = input} type="file" multiple/><br/>
                <input type="submit" value="Submit"/>
            </form>
        )
    }
}

export default FileUpload
