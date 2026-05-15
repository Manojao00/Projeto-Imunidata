import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/registros';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Funções para operações CRUD
export const registroAPI = {
  // Obter todos os registros
  obterTodos: () => api.get('/'),

  // Obter um registro por ID
  obterPorId: (id) => api.get(`/${id}`),

  // Criar um novo registro
  criar: (dados) => api.post('/', dados),

  // Atualizar um registro
  atualizar: (id, dados) => api.put(`/${id}`, dados),

  // Deletar um registro
  deletar: (id) => api.delete(`/${id}`),

  // Buscar por vacina
  buscarPorVacina: (nome) => api.get(`/buscar/vacina?nome=${nome}`),

  // Buscar por estado
  buscarPorEstado: (nome) => api.get(`/buscar/estado?nome=${nome}`),

  // Buscar por município
  buscarPorMunicipio: (nome) => api.get(`/buscar/municipio?nome=${nome}`),

  // Buscar por faixa etária
  buscarPorFaixaEtaria: (faixa) => api.get(`/buscar/faixa-etaria?faixa=${faixa}`),

  // Buscar por estado e vacina
  buscarPorEstadoEVacina: (estado, vacina) => 
    api.get(`/buscar/estado-vacina?estado=${estado}&vacina=${vacina}`),

  // Carregar dados do CSV
  carregarCSV: (arquivo) => {
    const formData = new FormData();
    formData.append('file', arquivo);
    return api.post('/carregar-csv', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },

  // Obter resumo por estado
  obterResumosPorEstado: () => api.get('/resumos/estado'),
};

export default api;
