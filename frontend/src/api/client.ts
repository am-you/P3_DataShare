import axios from 'axios'

// Am.Yo : point d'entrée unique vers le back, passe par le proxy Vite
export const api = axios.create({
  baseURL: '/api',
})

// Am.Yo : ajoute le Bearer si une session existe
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('datashare_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
