import { CapacitorConfig } from '@capacitor/cli'

const config: CapacitorConfig = {
  appId: 'vn.wellcare.capacitor.stringee.example',
  appName: 'CapacitorStringeeExample',
  webDir: 'dist',
  bundledWebRuntime: false,
  plugins: {},
  cordova: {},
  server: {
    iosScheme: 'nuxtmobile'
  }
}

export default config
