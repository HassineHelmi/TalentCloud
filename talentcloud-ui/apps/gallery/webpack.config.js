const ModuleFederationPlugin = require('webpack/lib/container/ModuleFederationPlugin');
const mf = require('@angular-architects/module-federation/webpack');
const path = require('path');

const sharedMappings = new mf.SharedMappings();
sharedMappings.register(path.join(__dirname, '../../tsconfig.base.json'), [
  '@mf-app/shared/data-store',
]);

module.exports = {
  output: {
    uniqueName: 'gallery',
    publicPath: 'auto',
  },
  optimization: {
    runtimeChunk: false,
    minimize: false,
  },
  resolve: {
    alias: {
      ...sharedMappings.getAliases(),
    },
  },
  plugins: [
    new ModuleFederationPlugin({
      name: 'gallery',
      filename: 'remoteEntry.js',
      exposes: {
        './Module': 'apps/gallery/src/app/remote-entry/entry.module.ts',
      },
      shared: {
        '@angular/core': {
          singleton: true,
          strictVersion: true,
          // removed requiredVersion, as strictVersion will handle it
        },
        '@angular/common': {
          singleton: true,
          strictVersion: true,
          // removed requiredVersion, as strictVersion will handle it
        },
        '@angular/common/http': {
          singleton: true,
          strictVersion: true,
          // removed requiredVersion, as strictVersion will handle it
        },
        '@angular/router': {
          singleton: true,
          strictVersion: true,
          // removed requiredVersion, as strictVersion will handle it
        },
        ...sharedMappings.getDescriptors(),
      },
    }),
    sharedMappings.getPlugin(),
  ],
};
