import { registerPlugin } from '@capacitor/core'

import type { CapacitorStringeePlugin } from './definitions'

const CapacitorStringee = registerPlugin<CapacitorStringeePlugin>(
  'CapacitorStringee',
  {
    web: () => import('./web').then((m) => new m.CapacitorStringeeWeb())
  }
)

export * from './definitions'
export { CapacitorStringee }
// a