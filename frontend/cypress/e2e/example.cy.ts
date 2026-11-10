// https://on.cypress.io/api

describe("Page d'accueil", () => {
  it('affiche le titre', () => {
    cy.visit('/')
    cy.contains('h1', 'Tu veux partager un fichier ?')
  })
})
