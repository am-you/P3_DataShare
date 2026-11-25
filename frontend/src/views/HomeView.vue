<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import UploadModal from '@/components/UploadModal.vue'

const auth = useAuthStore()
const router = useRouter()
const showUpload = ref(false)

function logout() {
  auth.logout()
  router.push('/login')
}

function openUpload() {
  if (!auth.isAuthenticated) {
    router.push('/login')
    return
  }
  showUpload.value = true
}
</script>

<template>
  <main>
    <header>
      <button v-if="auth.isAuthenticated" type="button" @click="logout">Déconnexion</button>
      <RouterLink v-else to="/login">Se connecter</RouterLink>
    </header>
    <h1>Tu veux partager un fichier ?</h1>
    <button type="button" @click="openUpload">Ajouter un fichier</button>
    <UploadModal v-model:open="showUpload" />
  </main>
</template>

<style scoped>
header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 2rem;
}
</style>
