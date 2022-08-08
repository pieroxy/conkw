const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = (env, argv) => 
{
  const devMode = argv.mode !== "production";
  return {
    entry: './src/ts/index.ts',
    module: {
      rules: [
        {
          test: /\.ts$/,
          include: path.resolve(__dirname, 'src/ts'),
          use: 'ts-loader',
          exclude: /node_modules/,
        },
        {
          test: /\.s[ac]ss$/i,
          include: path.resolve(__dirname, 'src/css'),
          use: [
            // Creates `style` nodes from JS strings
            devMode ? "style-loader":
            {
              loader: MiniCssExtractPlugin.loader,
              options: {
                // you can specify a publicPath here
                // by default it uses publicPath in webpackOptions.output
                publicPath: "./",
              },
            },
            // Translates CSS into CommonJS
            {
              loader: 'css-loader',
              options: {
                url: {
                  filter: (url, resourcePath) => {
                    // resourcePath - path to css file
      
                    // Don't handle `img.png` urls
                    if (url.indexOf("/fonts/")==0) {
                      return false;
                    }
                    return true;
                  },
                },
                sourceMap: true
              }
            },          
            // Compiles Sass to CSS
            {
              loader: "sass-loader",
              options: {
                sourceMap: true
              }
            }
            ,
          ],
        },
      ],
    },
    resolve: {
      extensions: ['.ts', '.js'],
    },
    output: {
      filename: 'bundle-[contenthash:6].js',
      path: path.resolve(__dirname, 'dist'),
      clean: true
    },
    plugins: [
      new HtmlWebpackPlugin({
        title: 'ConkW Dashboards',
        template: './src/html/index.html',
  
      }),
      new MiniCssExtractPlugin({
        filename:"bundle-[contenthash:6].css"
      })
    ],
  }
}