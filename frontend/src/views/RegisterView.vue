<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const localError = ref<string | null>(null)

async function onSubmit() {
  localError.value = null
  if (password.value !== passwordConfirm.value) {
    localError.value = 'Les mots de passe ne correspondent pas'
    return
  }
  if (password.value.length < 8) {
    localError.value = 'Le mot de passe doit faire au moins 8 caractères'
    return
  }
  try {
    await auth.register(email.value, password.value)
    await auth.login(email.value, password.value)
    await router.push('/')
  } catch {
    // message déjà dans auth.error
  }
}
</script>

<template>
  <main class="auth">
    <h1>Créer un compte</h1>
    <form @submit.prevent="onSubmit">
      <label>
        Email
        <input v-model="email" type="email" autocomplete="username" required />
      </label>
      <label>
        Mot de passe
        <input v-model="password" type="password" autocomplete="new-password" required />
      </label>
      <label>
        Vérification du mot de passe
        <input v-model="passwordConfirm" type="password" autocomplete="new-password" required />
      </label>
      <p v-if="localError || auth.error" class="error">{{ localError || auth.error }}</p>
      <button type="submit" :disabled="auth.loading">Créer mon compte</button>
    </form>
    <RouterLink to="/login">J'ai déjà un compte</RouterLink>
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
