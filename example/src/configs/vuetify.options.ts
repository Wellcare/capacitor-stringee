// import colors from 'vuetify/es5/util/colors'
import { IconsOptions } from 'vuetify/types/services/icons'
import {
  mdiWeatherSunny,
  mdiWeatherNight,
  mdiAccountCircle,
  mdiArrowLeft,
  mdiClose,
  mdiEmoticon,
  mdiHeart,
  mdiInformation,
  mdiMagnify,
  mdiHome,
  mdiShieldBugOutline,
  mdiHomeOutline,
  mdiShieldBug,
  mdiAccountCircleOutline,
  mdiMore,
  mdiDotsHorizontal,
  mdiDotsHorizontalCircle,
  mdiFilter,
  mdiContentCopy
} from '@mdi/js'

export default {
  icons: {
    iconfont: 'mdiSvg', // default
    values: {
      more: mdiMore,
      dotsHorizontal: mdiDotsHorizontal,
      filter: mdiFilter,
      contentCopy: mdiContentCopy,
      dotsHorizontalOutline: mdiDotsHorizontalCircle,
      accountCircle: mdiAccountCircle,
      accountCircleOutline: mdiAccountCircleOutline,
      arrowLeft: mdiArrowLeft,
      close: mdiClose,
      emoticon: mdiEmoticon,
      shieldBugOutline: mdiShieldBugOutline,
      heart: mdiHeart,
      home: mdiHome,
      shieldBug: mdiShieldBug,
      homeOutline: mdiHomeOutline,
      information: mdiInformation,
      magnify: mdiMagnify,
      weatherNight: mdiWeatherNight,
      weatherSunny: mdiWeatherSunny
    }
  } as IconsOptions,
  theme: {
    dark: false,
    default: false,
    disable: false,
    options: {
      customProperties: true
    },
    themes: {
      light: {
        primary: '#009688',
        secondary: '#ff9800',
        accent: '#ff5722',
        error: '#f44336',
        warning: '#ffc107',
        info: '#2196f3',
        success: '#4caf50'
      },
      dark: {
        primary: '#009688',
        secondary: '#ff9800',
        accent: '#ff5722',
        error: '#f44336',
        warning: '#ffc107',
        info: '#2196f3',
        success: '#4caf50'
      }
    }
  }
}
