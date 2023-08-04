<template>
  <v-app>
    <v-main>
      <Nuxt keep-alive />
    </v-main>
    <w-bottom-menu :menu-items="menuItems" :max="4" />
  </v-app>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api'

export default defineComponent({
  transition: 'page',
  setup() {
    const menuItems = [
      {
        text: 'Main',
        icon: '$homeOutline',
        activeIcon: '$home',
        to: '/'
      },
      {
        text: 'Debug',
        icon: '$shieldBugOutline',
        activeIcon: '$shieldBug',
        to: 'debug'
      },
      {
        text: 'Account',
        icon: '$accountCircleOutline',
        activeIcon: '$accountCircle',
        to: 'account'
      },
      {
        text: 'IOS',
        icon: '$accountCircleOutline',
        activeIcon: '$accountCircle',
        to: 'ios-implement'
      }
    ]
    return { menuItems }
  }
})
</script>
