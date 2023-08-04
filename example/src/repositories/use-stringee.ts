import { ref, useContext } from '@nuxtjs/composition-api'
import jwt from 'jsonwebtoken'

export function useStringee() {
  const { $config } = useContext()
  const { secret, iss } = $config.stringee
  const token = ref('')
  token.value = jwt.sign(
    {
      jti: iss + '_' + new Date().getTime().toString(),
      iss,
      exp: Math.floor(
        new Date(new Date().getTime() + 24 * 60 * 60 * 1000).getTime() / 1000
      ),
      userId: '387283'
    },
    secret
  )
  return { token }
}
