const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const GitRevisionPlugin = require('git-revision-webpack-plugin');

const gitRevisionPlugin = new GitRevisionPlugin();

const config = (env, argv) => {

    console.log('Environment: ', process.env)
    console.log('Arguments: ', argv)

    return {
        entry: ['babel-polyfill', './app/index.js'],
        mode: 'development',
        output: {
            path: path.resolve(__dirname, 'dist'),
            filename: 'js/bundle.js'
        },
        devServer: {
            contentBase: './dist'
        },
        plugins: [
            new HtmlWebpackPlugin({
                filename: 'index.html',
                template: './app/index.html',
                templateParameters: {
                    COMMITHASH: process.env.COMMIT_HASH || gitRevisionPlugin.commithash()
                }
            })
        ],
        module: {
            rules: [
                {
                    test: /\.jsx?$/,
                    exclude: /node_modules/,
                    use: {
                        loader: 'babel-loader',
                    }
                },
            ]
        }
    }
}

module.exports = config
