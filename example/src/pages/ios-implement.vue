<template>
  <div>
    <v-container>
      <v-text-field v-model="token" label="token"></v-text-field>
      <v-btn @click="connect">Connect</v-btn>
      <v-text-field v-model="callFrom" label="call from"></v-text-field>
      <v-text-field v-model="callTo" label="call to"></v-text-field>
      <v-btn :disabled="isAuth" color="primary" @click="call">Call</v-btn>
      <v-btn :disabled="isAuth" color="error" @click="reject">Reject</v-btn>
      <p>Status: {{ status }}</p>
      <w-debug-log />
    </v-container>
  </div>
</template>
<script lang="ts">
import { defineComponent, onMounted, ref } from '@nuxtjs/composition-api'
import { CapacitorStringee } from '@wellcare/capacitor-stringee'
import { useLog } from '@wellcare/vue-component'

export default defineComponent({
  setup() {
    const { log } = useLog()
    const token = ref(
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InN0cmluZ2VlLWFwaTt2PTEifQ.eyJqdGkiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCXzE2ODY1NTc0NTIwNzgiLCJpc3MiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCIiwiZXhwIjoxNjg2NjQzODUyMDc4LCJ1c2VySWQiOiIzODcyODMiLCJpYXQiOjE2ODY1NTc0NTJ9.l6SIoq-n0CPpDSST-K_5w_RdSuB_2O75TWcDh4-DsDM'
    )
    const callFrom = ref('387283')
    const callTo = ref('84377038734')
    const status = ref('')
    const isAuth = ref(false)
    const StringeePlugin = CapacitorStringee
    const connect = () => {
      if (process.client) StringeePlugin.StringeeConnect({ token: token.value })
    }

    const call = () => {
      if (process.client)
        StringeePlugin.StringeeCall({
          from: callFrom.value,
          to: callTo.value,
          displayName: 'NGUYEN THAI ANH KHOA',
          displayImage:
            'https://raw.githubusercontent.com/Ashwinvalento/cartoon-avatar/master/lib/images/female/5.png'
        })
    }

    const reject = () => {
      if (process.client) StringeePlugin.StringeeReject()
    }

    onMounted(() => {
      if (process.client) {
        StringeePlugin.addListener(
          'onConnectionConnected',
          onConnectionConnected
        )
        StringeePlugin.addListener(
          'onConnectionDisconnected',
          onConnectionDisconnected
        )
        StringeePlugin.addListener('onIncomingCall', onIncomingCall)
        StringeePlugin.addListener('onConnectionError', onConnectionError)
        StringeePlugin.addListener('onRequestNewToken', onRequestNewToken)
        StringeePlugin.addListener('onCustomMessage', onCustomMessage)
        StringeePlugin.addListener('onTopicMessage', onTopicMessage)
      }
    })

    const onConnectionConnected = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onConnectionConnected ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onConnectionDisconnected = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onConnectionDisconnected ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onIncomingCall = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onIncomingCall ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onRequestNewToken = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onRequestNewToken ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onConnectionError = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onConnectionError ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onCustomMessage = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onCustomMessage ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }

    const onTopicMessage = (data: any) => {
      const debugPrint = {
        context: 'Stringee',
        message: `onTopicMessage ${JSON.stringify(data)}`,
        data
      }
      log(debugPrint)
      console.log('[wellcare]', debugPrint)
    }
    return {
      token,
      callFrom,
      callTo,
      status,
      isAuth,
      connect,
      call,
      reject
    }
  },
  head() {
    return {
      title: 'Stringee'
    }
  }
})
</script>
