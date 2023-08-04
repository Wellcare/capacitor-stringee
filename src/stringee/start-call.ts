// import "https://cdn.stringee.com/sdk/web/latest/stringee-web-sdk.min.js"
declare var StringeeClient: any
declare var StringeeCall: any

export default function StartCall(
  token: string,
  callFrom: string,
  callTo: string
) {
  const client = new StringeeClient()
  client.connect(token)
  client.on('connect', (_res: any) => {
    console.log('connect to stringee server')
  })
  return new Promise((resolve, _reject) => {
    client.on('authen', (res: any) => {
      if (res.r === 0) {
        resolve(new StringeeCall(client, callFrom, callTo))
      }
    })
  })
  // const call = new StringeeCall(client, callFrom, callTo)
  // return call
}
