import axios from 'axios';

const api = axios.create({
  baseURL: '/api/registros',
  headers: { 'Content-Type': 'application/json' },
});

export const registroAPI = {
  obterTodos:            ()           => api.get('/'),
  obterPorId:            (id)         => api.get(`/${id}`),
  criar:                 (dados)      => api.post('/', dados),
  atualizar:             (id, dados)  => api.put(`/${id}`, dados),
  deletar:               (id)         => api.delete(`/${id}`),
  buscarPorVacina:       (nome)       => api.get(`/buscar/vacina?nome=${nome}`),
  buscarPorEstado:       (nome)       => api.get(`/buscar/estado?nome=${nome}`),
  buscarPorMunicipio:    (nome)       => api.get(`/buscar/municipio?nome=${nome}`),
  buscarPorIdade:        (idade)      => api.get(`/buscar/idade?idade=${idade}`),
  buscarPorEstadoEVacina:(estado, vacina) => api.get(`/buscar/estado-vacina?estado=${estado}&vacina=${vacina}`),
  buscarPorData:         (data)       => api.get(`/buscar/data?data=${data}`),
  buscarPorPeriodo:      (inicio, fim, estado) => {
    let url = `/buscar/periodo?inicio=${inicio}&fim=${fim}`;
    if (estado) url += `&estado=${estado}`;
    return api.get(url);
  },
  carregarCSV: (arquivo) => {
    const formData = new FormData();
    formData.append('file', arquivo);
    return api.post('/carregar-csv', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  obterResumosPorEstado: () => api.get('/resumos/estado'),
};

export default api;
