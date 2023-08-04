import fs from 'fs'
import path from 'path'
import { NuxtOptions } from '@nuxt/types'
import { config } from 'dotenv'
import {
  axiosConfig,
  buildConfg,
  dayjsConfig,
  headConfig,
  i18nConfig,
  toastConfig,
  vuetifyConfig
} from './src/configs/index'
config({ path: './.env' })

export default {
  target: 'static',
  ssr: false,
  components: true,
  publicRuntimeConfig: {
    'nuxt-module-data-layer': {
      accountBaseURL: process.env.ACCOUNT_BASE_URL,
      baseURL: process.env.API_ENDPOINT,
      xTenantId: process.env.X_TENTANT_ID || 'wellcare-plugin'
    },
    stringee: {
      secret: process.env.STRINGEE_SECRET,
      iss: process.env.STRINGEE_ISS
    }
  },
  srcDir: process.cwd() + '/src/',
  dir: {
    assets: 'assets',
    layouts: 'layouts',
    middleware: 'middleware',
    pages: 'pages',
    static: 'static',
    store: 'store'
  },
  plugins: [],
  css: ['~/assets/styles/app'],
  modules: [
    '@nuxtjs/axios',
    'nuxt-user-agent',
    '@nuxtjs/dayjs',
    '@nuxtjs/i18n',
    '@nuxtjs/toast',
    'cookie-universal-nuxt'
  ],
  buildModules: [
    '@nuxtjs/composition-api/module',
    '@nuxtjs/eslint-module',
    '@nuxtjs/vuetify',
    '@nuxt/typescript-build',
    '@nuxtjs/stylelint-module',
    'nuxt-typed-vuex',
    ['@wellcare/vue-component', { prefix: 'w', level: 1 }]
  ],
  build: buildConfg,
  head: headConfig,
  server: {
    host: process.env.APP_HOST || '0.0.0.0',
    port: process.env.APP_PORT || '8080',
    https: {
      key: fs.readFileSync(path.resolve(__dirname, 'localhost.key')),
      cert: fs.readFileSync(path.resolve(__dirname, 'localhost.crt'))
    }
  },
  axios: axiosConfig,
  vuetify: vuetifyConfig,
  i18n: i18nConfig,
  toast: toastConfig,
  // https://typescript.nuxtjs.org/guide/lint.html
  typescript: {
    typeCheck: {
      eslint: {
        files: './**/*.{ts,vue}'
      }
    }
  },
  dayjs: dayjsConfig,
  // https://github.com/nuxt-community/eslint-module
  eslint: {
    fix: true
  },
  // https://github.com/nuxt-community/stylelint-module
  stylelint: {
    fix: true
  },
  generate: {
    interval: 3000
  }
} as Partial<NuxtOptions>
