var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
//const WebpackCdnPlugin = require('webpack-cdn-plugin');

module.exports = {
    entry: "./src/scripts/main.tsx",
    output: {
        filename: "bundle.js",
        path: path.resolve(__dirname, 'dist/'),
        publicPath: "/"
    },

    devtool: "inline-source-map",
    
    resolve: {
        extensions: [".ts", ".tsx", ".js", ".jsx", ".json"]
    },
    
    module: {
        rules: [
            { test: /\.tsx?$/, loader: "awesome-typescript-loader" },
            { enforce: "pre", test: /\.js$/, loader: "source-map-loader" }
        ]
    },

    externals: {
        "react": "React",
        "react-dom": "ReactDOM"
    },

    plugins: [
        new HtmlWebpackPlugin({
            filename: "index.html",
            template: "./src/index.html"
        })
    ],

    watch: true,

    devServer: {
        contentBase: "/home/app/dist/",
        watchContentBase: true,
        port: 80,
        host: "0.0.0.0",
        disableHostCheck: true
    }
};