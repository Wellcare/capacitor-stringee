# capacitor-stringee

Capacitor Plugin for Stringee Apps

## Install

```bash
npm install capacitor-stringee
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`StringeeConnect(...)`](#stringeeconnect)
* [`StringeeCall(...)`](#stringeecall)
* [`StringeeReject(...)`](#stringeereject)
* [`StringeeHangup(...)`](#stringeehangup)
* [`addListener('onConnectionConnected' | 'onConnectionDisconnected', ...)`](#addlisteneronconnectionconnected--onconnectiondisconnected)
* [`addListener('onConnectionError', ...)`](#addlisteneronconnectionerror)
* [`addListener('onRequestNewToken', ...)`](#addlisteneronrequestnewtoken)
* [`addListener('onCustomMessage', ...)`](#addlisteneroncustommessage)
* [`addListener('exception', ...)`](#addlistenerexception)
* [`addListener('onStringeeCallEvent', ...)`](#addlisteneronstringeecallevent)
* [`addListener('onAuthenticated', ...)`](#addlisteneronauthenticated)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

echo input value

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### StringeeConnect(...)

```typescript
StringeeConnect(options: { token: string; }) => Promise<{ token: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ token: string; }</code> |

**Returns:** <code>Promise&lt;{ token: string; }&gt;</code>

--------------------


### StringeeCall(...)

```typescript
StringeeCall(options: { from: string; to: string; displayName?: string; displayImage?: string; }) => Promise<void>
```

| Param         | Type                                                                                    |
| ------------- | --------------------------------------------------------------------------------------- |
| **`options`** | <code>{ from: string; to: string; displayName?: string; displayImage?: string; }</code> |

--------------------


### StringeeReject(...)

```typescript
StringeeReject(options: any) => Promise<void>
```

| Param         | Type             |
| ------------- | ---------------- |
| **`options`** | <code>any</code> |

--------------------


### StringeeHangup(...)

```typescript
StringeeHangup(options: any) => Promise<void>
```

| Param         | Type             |
| ------------- | ---------------- |
| **`options`** | <code>any</code> |

--------------------


### addListener('onConnectionConnected' | 'onConnectionDisconnected', ...)

```typescript
addListener(eventName: 'onConnectionConnected' | 'onConnectionDisconnected', listenerFunc: (data: { uid: string; isReconnecting: boolean; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                      |
| ------------------ | ------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onConnectionConnected' \| 'onConnectionDisconnected'</code>        |
| **`listenerFunc`** | <code>(data: { uid: string; isReconnecting: boolean; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onConnectionError', ...)

```typescript
addListener(eventName: 'onConnectionError', listenerFunc: (data: { code: string; error: string; message: string; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                              |
| ------------------ | --------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onConnectionError'</code>                                                  |
| **`listenerFunc`** | <code>(data: { code: string; error: string; message: string; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onRequestNewToken', ...)

```typescript
addListener(eventName: 'onRequestNewToken', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'onRequestNewToken'</code>    |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onCustomMessage', ...)

```typescript
addListener(eventName: 'onCustomMessage', listenerFunc: (data: { msg: string; from: string; message?: string; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                                             |
| ------------------ | -------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'onCustomMessage'</code>                                                   |
| **`listenerFunc`** | <code>(data: { msg: string; from: string; message?: string; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('exception', ...)

```typescript
addListener(eventName: 'exception', listenerFunc: (data: { message?: string | undefined; data?: any; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                              |
| ------------------ | ----------------------------------------------------------------- |
| **`eventName`**    | <code>'exception'</code>                                          |
| **`listenerFunc`** | <code>(data: { message?: string; data?: any; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onStringeeCallEvent', ...)

```typescript
addListener(eventName: 'onStringeeCallEvent', listenerFunc: (data: { event: string; data?: any; }) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                                           |
| ------------------ | -------------------------------------------------------------- |
| **`eventName`**    | <code>'onStringeeCallEvent'</code>                             |
| **`listenerFunc`** | <code>(data: { event: string; data?: any; }) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### addListener('onAuthenticated', ...)

```typescript
addListener(eventName: 'onAuthenticated', listenerFunc: (data: any) => void) => Promise<PluginListenerHandle> & PluginListenerHandle
```

| Param              | Type                                |
| ------------------ | ----------------------------------- |
| **`eventName`**    | <code>'onAuthenticated'</code>      |
| **`listenerFunc`** | <code>(data: any) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#pluginlistenerhandle">PluginListenerHandle</a></code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Removes all listeners

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
