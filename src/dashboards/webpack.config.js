const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: './src/ts/index.ts',
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.s[ac]ss$/i,
        use: [
          // Creates `style` nodes from JS strings
          "style-loader",
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
            }
          },          // Compiles Sass to CSS
          "sass-loader",
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
    }),
  ],
};
