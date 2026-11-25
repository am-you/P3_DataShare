import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { api } from '@/api/client'

const TOKEN_KEY = 'datashare_token'

// Am.Yo : store de session, jeton dans sessionStorage
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(sessionStorage.getItem(TOKEN_KEY))
  const error = ref<string | null>(null)
  const loading = ref(false)

  const isAuthenticated = computed(() => !!token.value)

  function setToken(value: string | null) {
    token.value = value
    if (value) {
      sessionStorage.setItem(TOKEN_KEY, value)
    } else {
      sessionStorage.removeItem(TOKEN_KEY)
    }
  }

  async function register(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      await api.post('/auth/register', { email, password })
    } catch (err: unknown) {
      error.value = readApiMessage(err, "Impossible de créer le compte")
      throw err
    } finally {
      loading.value = false
    }
  }

  async function login(email: string, password: string) {
    loading.value = true
    error.value = null
    try {
      const { data } = await api.post<{ token: string; expiresIn: number }>('/auth/login', {
        email,
        password,
      })
      setToken(data.token)
    } catch (err: unknown) {
      error.value = readApiMessage(err, 'Email ou mot de passe incorrect')
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    setToken(null)
  }

  return { token, error, loading, isAuthenticated, register, login, logout }
})

function readApiMessage(err: unknown, fallback: string): string {
  if (typeof err === 'object' && err !== null && 'response' in err) {
    const response = (err as { response?: { data?: { message?: string } } }).response
    if (response?.data?.message) {
      return response.data.message
    }
  }
  return fallback
}
