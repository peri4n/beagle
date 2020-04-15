const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const GitRevisionPlugin = require('git-revision-webpack-plugin');
const CompressionPlugin = require('compression-webpack-plugin');

const gitRevisionPlugin = new GitRevisionPlugin();

module.exports = () => {
    return {
        entry: './app/index.tsx',
        mode: 'development',
        output: {
            path: path.resolve(__dirname, 'dist'),
            filename: 'js/bundle.js'
        },
        devServer: {
            contentBase: './dist'
        },
        devtool: 'inline-source-map',
        plugins: [
            new HtmlWebpackPlugin({
                filename: 'index.html',
                template: './app/index.html',
                templateParameters: {
                    COMMITHASH: process.env.COMMIT_HASH || gitRevisionPlugin.commithash()
                }
            }),
            new CompressionPlugin({
                test: /\.js(\?.*)?$/i,
            })
        ],
        resolve: {
            extensions: [ '.tsx', '.ts', '.js' ],
        },
        module: {
            rules: [
                {
                    test: /\.ts(x?)$/,
                    exclude: /node_modules/,
                    use: {
                        loader: "ts-loader"
                    }
                },
            ]
        },
        optimization: {
            minimize: true
        },
    }
}
