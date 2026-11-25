<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '@/api/client'

type DownloadInfo = {
  name: string
  sizeBytes: number
  mimeType: string
  expiresAt: string
  protected: boolean
}

const route = useRoute()
const token = computed(() => String(route.params.token ?? ''))

const info = ref<DownloadInfo | null>(null)
const password = ref('')
const error = ref<string | null>(null)
const loading = ref(false)

const daysLeft = computed(() => {
  if (!info.value) return null
  const ms = new Date(info.value.expiresAt).getTime() - Date.now()
  return Math.max(0, Math.ceil(ms / (1000 * 60 * 60 * 24)))
})

onMounted(loadInfo)

async function loadInfo() {
  error.value = null
  try {
    const { data } = await api.get<DownloadInfo>(`/download/${token.value}`)
    info.value = data
  } catch (err: unknown) {
    error.value = readMessage(err, 'Lien invalide')
  }
}

async function download() {
  if (!info.value) return
  loading.value = true
  error.value = null
  try {
    const body = info.value.protected ? { password: password.value } : {}
    const response = await api.post(`/download/${token.value}`, body, {
      responseType: 'blob',
      validateStatus: () => true,
    })
    if (response.status >= 400) {
      error.value = await readBlobError(response.data, response.status)
      return
    }
    const url = URL.createObjectURL(response.data)
    const a = document.createElement('a')
    a.href = url
    a.download = info.value.name
    a.click()
    URL.revokeObjectURL(url)
  } catch (err: unknown) {
    error.value = readMessage(err, 'Téléchargement impossible')
  } finally {
    loading.value = false
  }
}

async function readBlobError(data: Blob, status: number): Promise<string> {
  if (status === 410) {
    return "Ce fichier n'est plus disponible en téléchargement car il a expiré"
  }
  try {
    const text = await data.text()
    const json = JSON.parse(text) as { message?: string }
    return json.message ?? 'Téléchargement impossible'
  } catch {
    return 'Téléchargement impossible'
  }
}

function formatSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} o`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} Ko`
  return `${(bytes / (1024 * 1024)).toFixed(1)} Mo`
}

function readMessage(err: unknown, fallback: string): string {
  if (typeof err === 'object' && err !== null && 'response' in err) {
    const response = (err as { response?: { data?: Blob | { message?: string }; status?: number } })
      .response
    if (response?.status === 410) {
      return "Ce fichier n'est plus disponible en téléchargement car il a expiré"
    }
    if (response?.data && !(response.data instanceof Blob) && response.data.message) {
      return response.data.message
    }
  }
  return fallback
}
</script>

<template>
  <main class="download">
    <h1>Télécharger un fichier</h1>

    <p v-if="error && !info" class="error">{{ error }}</p>

    <template v-else-if="info">
      <p><strong>{{ info.name }}</strong></p>
      <p>{{ formatSize(info.sizeBytes) }}</p>
      <p v-if="daysLeft === 1">Ce fichier expirera demain</p>
      <p v-else>Ce fichier expirera dans {{ daysLeft }} jours</p>

      <label v-if="info.protected">
        Mot de passe
        <input v-model="password" type="password" required />
      </label>

      <p v-if="error" class="error">{{ error }}</p>
      <button type="button" :disabled="loading" @click="download">Télécharger</button>
    </template>
  </main>
</template>

<style scoped>
.download {
  max-width: 28rem;
  margin: 3rem auto;
  display: grid;
  gap: 0.75rem;
}
label {
  display: grid;
  gap: 0.25rem;
}
.error {
  color: #b00020;
}
</style>
