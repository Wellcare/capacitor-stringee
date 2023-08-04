import { WebPlugin } from '@capacitor/core'

// import type { CapacitorStringeePlugin } from './definitions'
// import StartCall from './stringee/start-call'

declare var StringeeClient: any
declare var StringeeCall: any
export class CapacitorStringeeWeb
  extends WebPlugin
  //implements CapacitorStringeePlugin
{
  #client: any
  #call: any
  #isAuth: boolean = false
  StringeeConnect(data: { token: string }, listenerFunc: (data: any) => void) {
    const dataEmit = {
      event: 'none',
      data: {}
    }
    this.#client = new StringeeClient()
    this.#client.connect(data.token)
    this.#client.on('connect', () => {
      // this.notifyListeners('stringee-log', 'connect')
      dataEmit.event = 'connect'
      listenerFunc(dataEmit)
    })
    this.#client.on('authen', (res: any) => {
      if (res.r === 0) {
        this.#isAuth = true
        // this.notifyListeners('stringee-log', 'authen')
        dataEmit.event = 'authen'
        listenerFunc(dataEmit)
      } else {
        console.error(res.message)
      }
    })
    this.#client.on('disconnect', () => {
      this.#isAuth = false
      // this.notifyListeners('stringee-log', 'disconnect')
      dataEmit.event = 'disconnect'
      listenerFunc(dataEmit)
    })
    this.#client.on('incomingcall', (res: any) => {
      // this.notifyListeners('incoming-call', res)
      dataEmit.event = 'incomingcall'
      dataEmit.data = res
      listenerFunc(dataEmit)
    })
    this.#client.on('otherdeviceauthen', (data: any) => {
      dataEmit.event = 'otherdevice-authen'
      dataEmit.data = data
      // this.notifyListeners('otherdevice-authen', data)
    })
  }
  StringeeCall(
    data: {
      callFrom: string
      callTo: string
    },
    listenerFunc: (data: any) => void
  ) {
    // const emitEvent = (name: string, data: any) => {
    //   this.notifyListeners(name, data)
    // }
    const dataEmit = {
      event: 'none',
      data: {}
    }
    if (this.#isAuth && this.#client) {
      this.#call = new StringeeCall(this.#client, data.callFrom, data.callTo)
      this.#call.makeCall((res: any) => {
        if (res.r !== 0) console.error(res.message)
      })

      this.#call.on('error', (info: any) => {
        console.error(info)
        // this.notifyListeners('error', info)
        dataEmit.event = 'error'
        dataEmit.data = info
        listenerFunc(dataEmit)
      })

      this.#call.on('addlocalstream', (stream: any) => {
        // this.notifyListeners('add-localstream', stream)
        dataEmit.event = 'add-localstream'
        dataEmit.data = stream
        listenerFunc(dataEmit)
      })
      this.#call.on('addremotestream', (stream: any) => {
        // this.notifyListeners('add-remotestream', stream)
        dataEmit.event = 'add-remotestream'
        dataEmit.data = stream
        listenerFunc(dataEmit)
      })

      this.#call.on('signalingstate', (state: any) => {
        // console.log(state, ' line 64')
        // this.notifyListeners('signaling-state', state)
        dataEmit.event = 'signaling-state'
        dataEmit.data = state
        listenerFunc(dataEmit)
      })

      this.#call.on('mediastate', (state: any) => {
        // this.notifyListeners('media-state', state)
        dataEmit.event = 'media-state'
        dataEmit.data = state
        listenerFunc(dataEmit)
      })

      this.#call.on('info', (info: any) => {
        // this.notifyListeners('info', info)
        dataEmit.event = 'info'
        dataEmit.data = info
        listenerFunc(dataEmit)
      })

      this.#call.on('otherdevice', (data: any) => {
        // this.notifyListeners('otherdevice', data)
        dataEmit.event = 'otherdevice'
        dataEmit.data = data
        listenerFunc(dataEmit)
      })
    }
  }
  StringeeReject(listenerFunc: (data: any) => void): void {
    if (this.#call) {
      this.#call.reject((res: any) => {
        // this.notifyListeners('call-reject', res)
        const dataEmit = {
          event: 'call-reject',
          data: res
        }
        listenerFunc(dataEmit)
      })
    }
  }
  StringeeHangup(listenerFunc: (data: any) => void): void {
    if (this.#call) {
      this.#call.hangup((res: any) => {
        // console.log("hangup res", res);
        const dataEmit = {
          event: 'call-hangup',
          data: res
        }
        listenerFunc(dataEmit)
      })
    }
  }
  // async testStringeePlugin(
  //   token: string,
  //   callFrom: string,
  //   callTo: string
  // ): Promise<void> {
  //   const emitEvent = (name: string, data: any) => {
  //     this.notifyListeners(name, data)
  //   }
  //   const call: any = await StartCall(token, callFrom, callTo)

  //   call.makeCall((res: any) => {
  //     if (res.r !== 0) console.log('call-status', res.message)
  //   })

  //   call.on('error', function (info: any) {
  //     console.error(info)
  //     emitEvent('error', info)
  //   })

  //   call.on('addlocalstream', function (stream: any) {
  //     console.log('stringee stream', stream)
  //     emitEvent('addlocal-stream', stream)
  //   })

  //   call.on('signalingstate', function (state: any) {
  //     emitEvent('signaling-state', state)
  //   })

  //   call.on('mediastate', function (state: any) {
  //     emitEvent('media-state ', state)
  //   })

  //   call.on('info', function (info: any) {
  //     emitEvent('info', info)
  //   })
  // }
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('[web] echo - options ', options)
    options.value = 'web: hello, world'
    return options
  }
}

const CapacitorStringee = new CapacitorStringeeWeb()

export { CapacitorStringee }
