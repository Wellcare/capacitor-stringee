import { PluginListenerHandle } from '@capacitor/core'

export interface CapacitorStringeePlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>
  /*
  StringeeConnect(token: string, listenerFunc: (data: any) => void): void
  StringeeCall(
    data: {
      callFrom: string
      callTo: string
    },
    listenerFunc?: (data: any) => void
  ): void
  StringeeReject(listenerFunc: (data: any) => void): void
  StringeeHangup(listenerFunc: (data: any) => void): void
  // listener for connect event
  // bug detect: seem capacitor only notify only one event, so I will temporary comment it
  */
  StringeeConnect(options: { token: string }): Promise<{ token: string }>

  StringeeCall(options: {
    from: string
    to: string
    displayName?: string
    displayImage?: string
  }): Promise<void>

  StringeeReject(options: any): Promise<void>

  addListener(
    eventName: 'onConnectionConnected' | 'onConnectionDisconnected',
    listenerFunc: (data: { uid: string; isReconnecting: boolean }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  addListener(
    eventName: 'onConnectionError',
    listenerFunc: (data: {
      code: string
      error: string
      message: string
    }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  addListener(
    eventName: 'onRequestNewToken',
    listenerFunc: (data: any) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  addListener(
    eventName: 'onCustomMessage',
    listenerFunc: (data: {
      msg: string
      from: string
      message?: string
    }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  /**
   * Removes all listeners
   */
  removeAllListeners(): Promise<void>;
}
