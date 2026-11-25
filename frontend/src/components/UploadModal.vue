<script setup lang="ts">
import { ref } from 'vue'
import { api } from '@/api/client'

const open = defineModel<boolean>('open', { required: true })

const emit = defineEmits<{
  uploaded: [downloadUrl: string]
}>()

const file = ref<File | null>(null)
const password = ref('')
const expirationDays = ref(7)
const error = ref<string | null>(null)
const loading = ref(false)
const downloadUrl = ref<string | null>(null)

function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  file.value = input.files?.[0] ?? null
  error.value = null
  downloadUrl.value = null
}

async function submit() {
  error.value = null
  downloadUrl.value = null
  if (!file.value) {
    error.value = 'Choisis un fichier'
    return
  }
  if (file.value.size > 1024 * 1024 * 1024) {
    error.value = 'La taille des fichiers est limitée à 1 Go'
    return
  }

  const form = new FormData()
  form.append('file', file.value)
  form.append('expirationDays', String(expirationDays.value))
  if (password.value.trim()) {
    form.append('password', password.value.trim())
  }

  loading.value = true
  try {
    const { data } = await api.post<{ downloadUrl: string }>('/files', form)
    downloadUrl.value = data.downloadUrl
    emit('uploaded', data.downloadUrl)
  } catch (err: unknown) {
    error.value = readMessage(err)
  } finally {
    loading.value = false
  }
}

function close() {
  open.value = false
}

async function copyLink() {
  if (downloadUrl.value) {
    await navigator.clipboard.writeText(downloadUrl.value)
  }
}

function readMessage(err: unknown): string {
  if (typeof err === 'object' && err !== null && 'response' in err) {
    const response = (err as { response?: { data?: { message?: string }; status?: number } }).response
    if (response?.status === 413) {
      return 'La taille des fichiers est limitée à 1 Go'
    }
    if (response?.data?.message) {
      return response.data.message
    }
  }
  return "Échec de l'envoi"
}
</script>

<template>
  <div v-if="open" class="backdrop" @click.self="close">
    <section class="modal" role="dialog" aria-modal="true" aria-labelledby="upload-title">
      <h2 id="upload-title">Ajouter un fichier</h2>

      <template v-if="!downloadUrl">
        <label>
          Fichier
          <input type="file" @change="onFileChange" />
        </label>
        <p v-if="file">{{ file.name }}</p>

        <label>
          Mot de passe (optionnel)
          <input v-model="password" type="password" minlength="6" />
        </label>

        <label>
          Expiration
          <select v-model.number="expirationDays">
            <option :value="1">Une journée</option>
            <option :value="2">2 jours</option>
            <option :value="3">3 jours</option>
            <option :value="7">Une semaine</option>
          </select>
        </label>

        <p v-if="error" class="error">{{ error }}</p>
        <button type="button" :disabled="loading" @click="submit">Téléverser</button>
      </template>

      <template v-else>
        <p>Félicitations, ton fichier sera conservé chez nous pendant la durée choisie !</p>
        <p class="link">{{ downloadUrl }}</p>
        <button type="button" @click="copyLink">Copier le lien</button>
        <button type="button" @click="close">Fermer</button>
      </template>
    </section>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: grid;
  place-items: center;
  padding: 1rem;
}
.modal {
  background: #fff;
  width: min(28rem, 100%);
  padding: 1.25rem;
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
.link {
  word-break: break-all;
  font-size: 0.9rem;
}
</style>
