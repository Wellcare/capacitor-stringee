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
* [`addListener('onConnectionConnected' | 'onConnectionDisconnected', ...)`](#addlisteneronconnectionconnected--onconnectiondisconnected)
* [`addListener('onConnectionError', ...)`](#addlisteneronconnectionerror)
* [`addListener('onRequestNewToken', ...)`](#addlisteneronrequestnewtoken)
* [`addListener('onCustomMessage', ...)`](#addlisteneroncustommessage)
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


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
