import React, { useState } from 'react'

import Button from '@material-ui/core/Button'
import TextField from '@material-ui/core/TextField'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogContentText from '@material-ui/core/DialogContentText'
import DialogTitle from '@material-ui/core/DialogTitle'

export default function FileUploadDialog() {

    const [visible, setVisible] = useState(false);

    function handleClickOpen() {
        setVisible(true);
    }

    function handleClose() {
        setVisible(false);
    }

    let fileInput

    function sendFile(event) {
        event.preventDefault();

        const files = fileInput.files;
        const formData = new FormData();

        for (var i = 0; i < files.length; i++) {
            formData.append('file', files[i]);
        }

        fetch('http://localhost:8080/upload', {
            method: 'POST',
            body: formData
        }).catch(error => alert('There has been a problem with your fetch operation: '));

        handleClose()
    }

    return (
        <div>
            <Button color="inherit" onClick={handleClickOpen}>Upload</Button>
            <Dialog open={visible} onClose={handleClose} aria-labelledby="form-dialog-title">
                <DialogTitle id="form-dialog-title">Upload FASTA file</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Upload the FASTA file you want to index.
                    </DialogContentText>
                    <TextField
                        autoFocus
                        margin="dense"
                        id="fasta-file"
                        label="Fasta File"
                        type="file"
                        fullWidth
                        inputRef={(input) => fileInput = input}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={sendFile} color="primary">
                        Upload
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}
