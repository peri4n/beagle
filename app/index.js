import React from 'react'
import { render } from 'react-dom'
import SearchForm from './components/SearchForm'
import FileUpload from './components/FileUpload'

class App extends React.Component {

    render() {
        return (
            <div>
                <FileUpload />
                <SearchForm />
            </div>
        )
    }
}

render(<App/>, document.getElementById('app-container'))
