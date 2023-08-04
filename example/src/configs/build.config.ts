import ESLintPlugin from 'eslint-webpack-plugin'

export const buildConfg: any = {
  extend(config: any, ctx: any) {
    if (ctx.isDev && ctx.isClient && config.module) {
      config?.plugins?.push(
        new ESLintPlugin({
          extensions: ['vue', 'ts']
        })
      )
    }
    config.node = {
      fs: 'empty'
    }
    // Extend only webpack config for client-bundle
    if (ctx.isClient) {
      config.devtool = 'source-map'
    }
    config?.module?.rules.push({
      test: /\.mjs$/,
      include: /node_modules/,
      type: 'javascript/auto'
    })
    config.module.rules.push({
      test: /\.s(c|a)ss$/,
      exclude: /(node_modules)/,
      use: [
        'vue-style-loader',
        // Creates `style` nodes from JS strings
        'style-loader',
        // Translates CSS into CommonJS
        'css-loader',
        // Compiles Sass to CSS
        'sass-loader'
      ]
    })
  },
  // extractCSS and parallel can not be enabled at the same time. See https://github.com/nuxt/nuxt.js/issues/4965
  // Parallel must be disabled on webpack-optimization-config as well.
  // parallel: process.env.NODE_ENV !== 'production',
  // extractCSS: process.env.NODE_ENV === 'production',
  // postcss: {},
  transpile: [/typed-vuex/, 'vee-validate/dist/rules', '@wellcare/vue-authen'],
  babel: {
    plugins: [
      // ['@babel/plugin-proposal-private-methods', { loose: true }]
      // ['@babel/plugin-proposal-private-property-in-object', { loose: true }]
    ]
  }
}
