const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

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
    
    plugins: [
        new HtmlWebpackPlugin({
            filename: "index.html",
            template: "./src/index.html"
        }),
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: '[name].css'
        })
    ],

    module: {
        rules: [
            { test: /\.tsx?$/, loader: "awesome-typescript-loader" },
            { enforce: "pre", test: /\.js$/, loader: "source-map-loader" },
            {
                test: /\.css$/, use: [
                    { loader: MiniCssExtractPlugin.loader },
                    { loader: 'css-loader', options: {
                        modules: true,
                        localIdentName: "[path][name]__[local]--[hash:base64:5]"/*, exportOnlyLocals: true*/
                    } }
                ],
            },
            { test: /\.(jpe?g|png|gif|svg|ico)$/, use: {
                loader: 'file-loader',
                options: {
                    limit: 8192,
                    name: '[path][name].[ext]'
                }
            } }
        ]
    },

    externals: {
        "react": "React",
        "react-dom": "ReactDOM"
    },

    watch: true,

    devServer: {
        contentBase: "/home/app/dist/",
        watchContentBase: true,
        port: 80,
        host: "0.0.0.0",
        disableHostCheck: true
    }
};