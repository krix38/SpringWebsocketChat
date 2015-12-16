var webpack = require('webpack');

module.exports = {
	entry: "./js/Main.js",
	output: {
		filename: "./js/Bundle.min.js"
		},
	node: {
		net: "empty"
	},
	module: {
      loaders: [
        {
          test: /\.jsx?$/,
          exclude: /(node_modules|bower_components)/,
          loader: 'babel',
          query: {
            presets: ['react', 'es2015']
          }
        }
      ]
    },
	plugins: [
		new webpack.optimize.UglifyJsPlugin({
			compress: {
				warnings: false,
			},
		}),
		new webpack.IgnorePlugin(/package\.json$/)
	]
}
