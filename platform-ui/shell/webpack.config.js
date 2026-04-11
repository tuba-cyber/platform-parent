const HtmlWebpackPlugin = require("html-webpack-plugin");
const { ModuleFederationPlugin } = require("webpack").container;
const path = require("path");

module.exports = (env, argv) => {
  const isDev = argv.mode === "development";

  return {
    entry: "./src/index.ts",
    mode: isDev ? "development" : "production",
    devServer: {
      port: 3000,
      historyApiFallback: true,
      hot: true,
    },
    output: {
      publicPath: "auto",
      filename: "[name].[contenthash].js",
      clean: true,
    },
    resolve: {
      extensions: [".ts", ".tsx", ".js", ".jsx"],
    },
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
        {
          test: /\.css$/,
          use: ["style-loader", "css-loader", "postcss-loader"],
        },
      ],
    },
    plugins: [
      new ModuleFederationPlugin({
        name: "shell",
        remotes: {
          platformCo: isDev
            ? "platformCo@http://localhost:3001/remoteEntry.js"
            : "platformCo@/co/remoteEntry.js",
          platformHr: isDev
            ? "platformHr@http://localhost:3002/remoteEntry.js"
            : "platformHr@/hr/remoteEntry.js",
        },
        shared: {
          react: { singleton: true, requiredVersion: "^18.2.0" },
          "react-dom": { singleton: true, requiredVersion: "^18.2.0" },
          "react-router-dom": { singleton: true, requiredVersion: "^6.20.0" },
          zustand: { singleton: true, requiredVersion: "^4.4.0" },
          axios: { singleton: true, requiredVersion: "^1.6.0" },
        },
      }),
      new HtmlWebpackPlugin({
        template: "./public/index.html",
      }),
    ],
  };
};
