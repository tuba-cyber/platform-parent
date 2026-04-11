const HtmlWebpackPlugin = require("html-webpack-plugin");
const { ModuleFederationPlugin } = require("webpack").container;

module.exports = (env, argv) => {
  const isDev = argv.mode === "development";
  return {
    entry: "./src/index.ts",
    mode: isDev ? "development" : "production",
    devServer: {
      port: 3002,
      historyApiFallback: true,
      hot: true,
      headers: { "Access-Control-Allow-Origin": "*" },
    },
    output: { publicPath: "auto", filename: "[name].[contenthash].js", clean: true },
    resolve: { extensions: [".ts", ".tsx", ".js", ".jsx"] },
    module: {
      rules: [
        {
          test: /\.(ts|tsx|js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: "babel-loader",
            options: {
              presets: [
                "@babel/preset-env",
                ["@babel/preset-react", { runtime: "automatic" }],
                "@babel/preset-typescript",
              ],
            },
          },
        },
        { test: /\.css$/, use: ["style-loader", "css-loader", "postcss-loader"] },
      ],
    },
    plugins: [
      new ModuleFederationPlugin({
        name: "platformHr",
        filename: "remoteEntry.js",
        exposes: { "./HrRoutes": "./src/HrRoutes" },
        shared: {
          react: { singleton: true, requiredVersion: "^18.2.0" },
          "react-dom": { singleton: true, requiredVersion: "^18.2.0" },
          "react-router-dom": { singleton: true, requiredVersion: "^6.20.0" },
          zustand: { singleton: true, requiredVersion: "^4.4.0" },
          axios: { singleton: true, requiredVersion: "^1.6.0" },
        },
      }),
      new HtmlWebpackPlugin({ template: "./public/index.html" }),
    ],
  };
};
