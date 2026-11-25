<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')

async function onSubmit() {
  try {
    await auth.login(email.value, password.value)
    await router.push('/')
  } catch {
    // message déjà dans auth.error
  }
}
</script>

<template>
  <main class="auth">
    <h1>Connexion</h1>
    <form @submit.prevent="onSubmit">
      <label>
        Email
        <input v-model="email" type="email" autocomplete="username" required />
      </label>
      <label>
        Mot de passe
        <input v-model="password" type="password" autocomplete="current-password" required />
      </label>
      <p v-if="auth.error" class="error">{{ auth.error }}</p>
      <button type="submit" :disabled="auth.loading">Connexion</button>
    </form>
    <RouterLink to="/register">Créer un compte</RouterLink>
  </main>
</template>

<style scoped>
.auth {
  max-width: 24rem;
  margin: 3rem auto;
  display: grid;
  gap: 1rem;
}
form {
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
