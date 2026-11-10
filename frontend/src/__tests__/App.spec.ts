import { describe, it, expect } from 'vitest'

import { mount, flushPromises } from '@vue/test-utils'
import App from '../App.vue'
import router from '../router'

describe('App', () => {
  it("affiche la page d'accueil", async () => {
    router.push('/')
    await router.isReady()
    const wrapper = mount(App, { global: { plugins: [router] } })
    await flushPromises()
    expect(wrapper.text()).toContain('Tu veux partager un fichier ?')
  })
})
