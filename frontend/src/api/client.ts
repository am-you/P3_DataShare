import axios from 'axios'

// Am.Yo : point d'entrée unique vers le back, passe par le proxy Vite
export const api = axios.create({
  baseURL: '/api',
})
