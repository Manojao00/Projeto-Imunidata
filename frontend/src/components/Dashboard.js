import React, { useState, useEffect } from 'react';
import { registroAPI } from '../services/registroAPI';
import './Dashboard.css';

function Dashboard() {
  const [registros, setRegistros] = useState([]);
  const [filtroVacina, setFiltroVacina] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [vacinasDisponiveis, setVacinasDisponiveis] = useState([]);
  const [estadosDisponiveis, setEstadosDisponiveis] = useState([]);

  // Carregar dados iniciais
  useEffect(() => {
    carregarRegistros();
  }, []);

  // Extrair vacinas e estados únicos
  useEffect(() => {
    const vacinas = [...new Set(registros.map(r => r.vacina))].sort();
    const estados = [...new Set(registros.map(r => r.estado))].sort();
    setVacinasDisponiveis(vacinas);
    setEstadosDisponiveis(estados);
  }, [registros]);

  const carregarRegistros = async () => {
    try {
      setCarregando(true);
      const response = await registroAPI.obterTodos();
      setRegistros(response.data);
      setErro(null);
    } catch (err) {
      setErro('Erro ao carregar registros: ' + err.message);
      console.error('Erro:', err);
    } finally {
      setCarregando(false);
    }
  };

  // Aplicar filtros
  const registrosFiltrados = registros.filter(registro => {
    const matchVacina = !filtroVacina || registro.vacina.toLowerCase().includes(filtroVacina.toLowerCase());
    const matchEstado = !filtroEstado || registro.estado === filtroEstado;
    return matchVacina && matchEstado;
  });

  const deletarRegistro = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar este registro?')) {
      try {
        await registroAPI.deletar(id);
        carregarRegistros();
      } catch (err) {
        setErro('Erro ao deletar registro: ' + err.message);
      }
    }
  };

  if (carregando) {
    return <div className="dashboard-container"><p>Carregando dados...</p></div>;
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Dashboard de Vacinação</h2>
        <p className="total-registros">Total de registros: {registrosFiltrados.length}</p>
      </div>

      {erro && <div className="erro-mensagem">{erro}</div>}

      <div className="filtros-container">
        <div className="filtro-grupo">
          <label htmlFor="filtro-vacina">Filtrar por Vacina:</label>
          <input
            id="filtro-vacina"
            type="text"
            placeholder="Digite a vacina..."
            value={filtroVacina}
            onChange={(e) => setFiltroVacina(e.target.value)}
            className="filtro-input"
          />
        </div>

        <div className="filtro-grupo">
          <label htmlFor="filtro-estado">Filtrar por Estado:</label>
          <select
            id="filtro-estado"
            value={filtroEstado}
            onChange={(e) => setFiltroEstado(e.target.value)}
            className="filtro-select"
          >
            <option value="">Todos os estados</option>
            {estadosDisponiveis.map(estado => (
              <option key={estado} value={estado}>{estado}</option>
            ))}
          </select>
        </div>

        <button onClick={() => { setFiltroVacina(''); setFiltroEstado(''); }} className="btn-limpar">
          Limpar Filtros
        </button>
      </div>

      <div className="tabela-container">
        <table className="tabela-registros">
          <thead>
            <tr>
              <th>ID</th>
              <th>Município</th>
              <th>Estado</th>
              <th>Vacina</th>
              <th>Dose</th>
              <th>Quantidade Aplicada</th>
              <th>Data do Registro</th>
              <th>Faixa Etária</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {registrosFiltrados.length > 0 ? (
              registrosFiltrados.map((registro) => (
                <tr key={registro.id}>
                  <td>{registro.id}</td>
                  <td>{registro.municipio}</td>
                  <td>{registro.estado}</td>
                  <td>{registro.vacina}</td>
                  <td>{registro.dose}</td>
                  <td>{registro.quantidadeAplicada}</td>
                  <td>{new Date(registro.dataRegistro).toLocaleDateString('pt-BR')}</td>
                  <td>{registro.faixaEtaria}</td>
                  <td>
                    <button
                      onClick={() => deletarRegistro(registro.id)}
                      className="btn-deletar"
                      title="Deletar registro"
                    >
                      Deletar
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center' }}>
                  Nenhum registro encontrado
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Dashboard;
