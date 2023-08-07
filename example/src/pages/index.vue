<template>
  <div>
    <v-container>
      <p>Stringee version 2.0</p>
      <w-text-field-copy v-model="token" label="token"></w-text-field-copy>
      <v-btn @click="connect">Connect</v-btn>
      <v-text-field v-model="callFrom" label="call from"></v-text-field>
      <v-text-field v-model="callTo" label="call to"></v-text-field>
      <v-btn :disabled="!isAuth" color="primary" @click="call">Call</v-btn>
      <v-btn :disabled="!isAuth" color="error" @click="reject">Reject</v-btn>
      <p>Status: {{ status }}</p>
      <w-debug-log />
    </v-container>
  </div>
</template>
<script lang="ts">
import { defineComponent, ref } from '@nuxtjs/composition-api'
import { CapacitorStringee } from '@wellcare/capacitor-stringee'
import { useLog } from '@wellcare/vue-component'
// import { useStringee } from '../repositories/use-stringee'

export default defineComponent({
  setup() {
    const { log } = useLog()
    // const { token } = useStringee()
    const token = ref(
      'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InN0cmluZ2VlLWFwaTt2PTEifQ.eyJqdGkiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCXzE2OTEzOTY3MDY0NjgiLCJpc3MiOiJTS3NXTXlPZjhqUm51c3dPS1pEOXI5alZtVHE2bXB2MzhCIiwiZXhwIjoxNjkyNDgzMTA2MDAwLCJ1c2VySWQiOiIzODcyODMiLCJpYXQiOjE2OTEzOTY3MDZ9.J7xpGuL_nGWlVgnMpr1e26f1a3JySyqtGIilkRAZD78'
    )
    const callFrom = ref('387283')
    const callTo = ref('user-64c1c7c8f3e1c16f553aa42b')
    const status = ref('')
    const isAuth = ref(true)
    const StringeePlugin = CapacitorStringee
    const connect = () => {
      if (process.client)
        StringeePlugin.StringeeConnect(
          {
            token: token.value
          },
          onClientEvent
        )
    }
    const call = () => {
      if (process.client)
        StringeePlugin.StringeeCall(
          {
            from: callFrom.value,
            to: callTo.value,
            displayName: 'User',
            displayImage: 'https://i.pravatar.cc/300'
          },
          onCallEvent
        )
    }
    const reject = () => {
      if (process.client)
        StringeePlugin.StringeeReject((data: any) => {
          log({ context: 'Stringee', message: `onReject ${data.event}`, data })
        })
    }
    const onClientEvent = (data: any) => {
      log({ context: 'Stringee', message: `onClientEvent ${data.event}`, data })
      if (data.event === 'authen') isAuth.value = true
    }
    const onCallEvent = (data: any) => {
      log({ context: 'Stringee', message: `onCallEvent ${data.event}`, data })
      if (data.event === 'signaling-state') {
        status.value = data.data.reason
      }
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
