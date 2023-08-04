export const headConfig = {
  meta: [
    {
      name: 'viewport',
      content:
        'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0, minimal-ui'
    }
  ],
  link: [
    { rel: 'preconnect', href: 'https://www.google-analytics.com' },
    { rel: 'preconnect', href: 'https://www.googleadservices.com' }
  ],
  script: [
    {
      src: 'https://cdn.stringee.com/sdk/web/latest/stringee-web-sdk.min.js',
      type: 'text/javascript'
    }
  ]
}
