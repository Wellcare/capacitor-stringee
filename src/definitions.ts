import { PluginListenerHandle } from '@capacitor/core'

export interface CapacitorStringeePlugin {
  /**
   * echo input value
   */
  echo(options: { value: string }): Promise<{ value: string }>

  StringeeConnect(options: { token: string }): Promise<{ token: string }>

  StringeeCall(options: {
    from: string
    to: string
    displayName?: string
    displayImage?: string
  }): Promise<void>

  StringeeReject(options: any): Promise<void>
  StringeeHangup(options: any): Promise<void>

  mute(): Promise<void>
  unmute(): Promise<void>

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

  addListener(
    eventName: 'exception',
    listenerFunc: (data: { message?: string; data?: any }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  addListener(
    eventName: 'onStringeeCallEvent',
    listenerFunc: (data: { event: string, data?: any }) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  addListener(
    eventName: 'onAuthenticated',
    listenerFunc: (data: any) => void
  ): Promise<PluginListenerHandle> & PluginListenerHandle

  /**
   * Removes all listeners
   */
  removeAllListeners(): Promise<void>
}
