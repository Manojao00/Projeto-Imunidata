import axios from 'axios';

// Se desejar usar o proxy configurado no package.json, o caminho será relativo ao backend.
// Caso a API seja externa, mantenha a URL completa.
const API_URL = 'https://apidadosabertos.saude.gov.br/vacinacao/doses-aplicadas-pni-2026?limit=1000';

export const vacinaAPI = {
  // Retorna o array de registros da API pública
  obterTodas: () => axios.get(API_URL),
};
