import { WebPlugin } from '@capacitor/core'
import type { CapacitorStringeePlugin } from './definitions'

declare var StringeeClient: any
declare var StringeeCall: any
export class CapacitorStringeeWeb
  extends WebPlugin
  implements CapacitorStringeePlugin
{
  #client: any
  #call: any
  #isAuth: boolean = false

  private sendPluginEvent(event: string, data: any) {
    this.notifyListeners(event, data)
  }

  StringeeConnect(data: { token: string }) {
    return new Promise<{ token: string }>((resolve, _reject) => {
      this.#client = new StringeeClient()
      this.#client.connect(data.token)
      this.#client.on('connect', (data: any) => {
        this.notifyListeners('onConnectionConnected', data)
      })
      this.#client.on('authen', (res: any) => {
        if (res.r === 0) {
          this.#isAuth = true
          this.notifyListeners('onAuthenticated', res)
        } else {
          this.notifyListeners('exception', { message: 'Unauthenticated' })
        }
      })
      this.#client.on('disconnect', (data: any) => {
        this.#isAuth = false
        this.notifyListeners('onConnectionDisconnected', data)
      })
      this.#client.on('otherdeviceauthen', (data: any) => {
        this.notifyListeners('exception', {
          message: 'Other device authen',
          data
        })
      })

      this.#client.on('requestnewtoken', (data: any) => {
        this.sendPluginEvent('onRequestNewToken', data)
      })

      resolve({ token: data.token })
    })
  }

  mute(): Promise<void> {
    throw new Error('Method not implemented.')
  }
  unmute(): Promise<void> {
    throw new Error('Method not implemented.')
  }

  StringeeCall(data: {
    from: string
    to: string
    displayName?: string
    displayImage?: string
  }) {
    return new Promise<void>((resolve, reject) => {
      const dataEmit = {
        event: 'none',
        data: {}
      }
      if (this.#isAuth && this.#client) {
        this.#call = new StringeeCall(this.#client, data.from, data.to, false)

        this.#call.on('error', (info: any) => {
          this.sendPluginEvent('exception', info)
        })

        this.#call.on('addlocalstream', (stream: any) => {
          dataEmit.event = 'add-localstream'
          dataEmit.data = stream
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })
        this.#call.on('addremotestream', (stream: any) => {
          dataEmit.event = 'add-remotestream'
          dataEmit.data = stream
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })

        this.#call.on('signalingstate', (state: any) => {
          dataEmit.event = 'signaling-state'
          dataEmit.data = state
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })

        this.#call.on('mediastate', (state: any) => {
          dataEmit.event = 'media-state'
          dataEmit.data = state
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })

        this.#call.on('info', (info: any) => {
          dataEmit.event = 'info'
          dataEmit.data = info
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })

        this.#call.on('otherdevice', (data: any) => {
          dataEmit.event = 'otherdevice'
          dataEmit.data = data
          this.sendPluginEvent('onStringeeCallEvent', dataEmit)
        })

        this.#call.makeCall((res: any) => {
          if (res.r !== 0) {
            this.sendPluginEvent('exception', {
              message: res.message,
              data: res
            })
            reject()
          } else resolve()
        })
      }
    })
  }

  StringeeReject() {
    return new Promise<void>((resolve, reject) => {
      if (this.#call) {
        this.#call.reject((res: any) => {
          resolve(res)
        })
      } else {
        reject('No instance')
      }
    })
  }

  StringeeHangup() {
    return new Promise<void>((resolve, reject) => {
      if (this.#call) {
        this.#call.hangup((res: any) => {
          this.sendPluginEvent('onStringeeDidHangup', res)
          resolve(res)
        })
      } else {
        reject('No instance')
      }
    })
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('[web] echo - options ', options)
    options.value = 'web: hello, world'
    return options
  }
}

const CapacitorStringee = new CapacitorStringeeWeb()

export { CapacitorStringee }
