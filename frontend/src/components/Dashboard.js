import React, { useState, useEffect, useImperativeHandle } from 'react';
import { registroAPI } from '../services/registroAPI';
import axios from 'axios';
import './Dashboard.css';

const Dashboard = React.forwardRef((props, ref) => {
  // ── Dados CSV (H2) ──────────────────────────────────────────────
  const [registros, setRegistros]           = useState([]);
  const [carregandoCSV, setCarregandoCSV]   = useState(true);
  // ── Dados Datasus ────────────────────────────────────────────────
  const [dadosAPI, setDadosAPI]             = useState([]);
  const [carregandoAPI, setCarregandoAPI]   = useState(true);
  // ── Filtros ──────────────────────────────────────────────────────
  const [filtroVacina, setFiltroVacina]     = useState('');
  const [filtroEstado, setFiltroEstado]     = useState('');
  const [filtroMunicipio, setFiltroMunicipio] = useState('');
  // ── UI ───────────────────────────────────────────────────────────
  const [erro, setErro]                     = useState(null);
  const [abaAtiva, setAbaAtiva]             = useState('datasus'); // 'csv' | 'datasus'

  // Expor método recarregar para o App.js
  useImperativeHandle(ref, () => ({ recarregar: carregarCSV }));

  useEffect(() => { carregarCSV(); carregarDatasus(); }, []);

  // ── Carrega registros do banco H2 (CSV importado) ────────────────
  const carregarCSV = async () => {
    try {
      setCarregandoCSV(true);
      const response = await registroAPI.obterTodos();
      setRegistros(response.data);
      setErro(null);
    } catch (err) {
      setErro('Erro ao carregar registros locais: ' + err.message);
    } finally {
      setCarregandoCSV(false);
    }
  };

  // ── Carrega dados da API Datasus ─────────────────────────────────
  const carregarDatasus = async () => {
    try {
      setCarregandoAPI(true);
      const response = await axios.get('/api/sipni/resumo/estados');
      setDadosAPI(response.data);
    } catch (err) {
      console.error('Erro ao carregar Datasus:', err.message);
    } finally {
      setCarregandoAPI(false);
    }
  };

  const deletarRegistro = async (id) => {
    if (!window.confirm('Deletar este registro?')) return;
    try {
      await registroAPI.deletar(id);
      carregarCSV();
    } catch (err) {
      setErro('Erro ao deletar: ' + err.message);
    }
  };

  // ── Filtros aplicados ────────────────────────────────────────────
  const csvFiltrados = registros.filter(r => {
    const mv = !filtroVacina || r.vacina?.toLowerCase().includes(filtroVacina.toLowerCase());
    const me = !filtroEstado || r.estado === filtroEstado;
    const mm = !filtroMunicipio || r.municipio?.toLowerCase().includes(filtroMunicipio.toLowerCase());
    return mv && me && mm;
  });

  const apiFiltrados = dadosAPI.filter(r => {
    const mv = !filtroVacina || r.descricao_vacina?.toLowerCase().includes(filtroVacina.toLowerCase())
                             || r.sigla_vacina?.toLowerCase().includes(filtroVacina.toLowerCase());
    const me = !filtroEstado || r.sigla_uf_paciente === filtroEstado;
    const mm = !filtroMunicipio || r.nome_municipio_paciente?.toLowerCase().includes(filtroMunicipio.toLowerCase());
    return mv && me && mm;
  });

  // Estados únicos para o select
  const estadosCSV    = [...new Set(registros.map(r => r.estado).filter(Boolean))].sort();
  const estadosAPI    = [...new Set(dadosAPI.map(r => r.sigla_uf_paciente).filter(Boolean))].sort();
  const todosEstados  = [...new Set([...estadosCSV, ...estadosAPI])].sort();

  const limparFiltros = () => { setFiltroVacina(''); setFiltroEstado(''); setFiltroMunicipio(''); };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>📊 Dashboard de Vacinação</h2>
        <div className="dashboard-stats">
          <span className="stat-badge">
            🏥 Datasus: <strong>{carregandoAPI ? '…' : dadosAPI.length}</strong> registros
          </span>
          <span className="stat-badge">
            📁 Banco local: <strong>{carregandoCSV ? '…' : registros.length}</strong> registros
          </span>
        </div>
      </div>

      {erro && <div className="erro-mensagem">{erro}</div>}

      {/* ── Filtros ── */}
      <div className="filtros-container">
        <div className="filtro-grupo">
          <label>Vacina:</label>
          <input
            type="text"
            placeholder="Ex: influenza, BCG..."
            value={filtroVacina}
            onChange={e => setFiltroVacina(e.target.value)}
            className="filtro-input"
          />
        </div>
        <div className="filtro-grupo">
          <label>Estado:</label>
          <select value={filtroEstado} onChange={e => setFiltroEstado(e.target.value)} className="filtro-select">
            <option value="">Todos</option>
            {todosEstados.map(e => <option key={e} value={e}>{e}</option>)}
          </select>
        </div>
        <div className="filtro-grupo">
          <label>Município:</label>
          <input
            type="text"
            placeholder="Ex: São Paulo..."
            value={filtroMunicipio}
            onChange={e => setFiltroMunicipio(e.target.value)}
            className="filtro-input"
          />
        </div>
        <button onClick={limparFiltros} className="btn-limpar">Limpar</button>
        <button onClick={() => { carregarCSV(); carregarDatasus(); }} className="btn-atualizar">↻ Atualizar</button>
      </div>

      {/* ── Abas ── */}
      <div className="dashboard-abas">
        <button
          className={`aba-btn ${abaAtiva === 'datasus' ? 'ativo' : ''}`}
          onClick={() => setAbaAtiva('datasus')}
        >
          🔗 SI-PNI / Datasus ({carregandoAPI ? '…' : apiFiltrados.length})
        </button>
        <button
          className={`aba-btn ${abaAtiva === 'csv' ? 'ativo' : ''}`}
          onClick={() => setAbaAtiva('csv')}
        >
          📁 Banco Local ({carregandoCSV ? '…' : csvFiltrados.length})
        </button>
      </div>

      {/* ── Tabela Datasus ── */}
      {abaAtiva === 'datasus' && (
        <div className="tabela-container">
          {carregandoAPI ? (
            <div className="carregando-msg">⏳ Carregando dados da API Datasus...</div>
          ) : apiFiltrados.length === 0 ? (
            <div className="sem-dados-msg">Nenhum dado encontrado. Tente outros filtros.</div>
          ) : (
            <table className="tabela-registros">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Município</th>
                  <th>Estado</th>
                  <th>Vacina</th>
                  <th>Dose</th>
                  <th>Quantidade</th>
                  <th>Data</th>
                  <th>Idade</th>
                </tr>
              </thead>
              <tbody>
                {apiFiltrados.map((item, idx) => (
                  <tr key={idx}>
                    <td>{idx + 1}</td>
                    <td>{item.nome_municipio_paciente || item.municipio || '—'}</td>
                    <td><span className="badge-estado">{item.sigla_uf_paciente || item.estado || '—'}</span></td>
                    <td title={item.descricao_vacina}>{item.sigla_vacina || '—'} <small>{item.descricao_vacina ? `(${item.descricao_vacina})` : ''}</small></td>
                    <td>{item.descricao_dose_vacina || '—'}</td>
                    <td>{item.quantidadeAplicada ?? 1}</td>
                    <td>{item.data_vacina ? item.data_vacina.substring(0, 10) : `${item.mes}/${item.ano}`}</td>
                    <td>{item.numero_idade_paciente || item.faixaEtaria || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}

      {/* ── Tabela CSV / Banco Local ── */}
      {abaAtiva === 'csv' && (
        <div className="tabela-container">
          {carregandoCSV ? (
            <div className="carregando-msg">⏳ Carregando registros locais...</div>
          ) : csvFiltrados.length === 0 ? (
            <div className="sem-dados-msg">
              Nenhum registro local. Use a aba "Importar CSV" ou "Novo Registro" para adicionar dados.
            </div>
          ) : (
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
                  <th>Idade</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {csvFiltrados.map(r => (
                  <tr key={r.id}>
                    <td>{r.id}</td>
                    <td>{r.municipio}</td>
                    <td><span className="badge-estado">{r.estado}</span></td>
                    <td>{r.vacina}</td>
                    <td>{r.dose}</td>
                    <td>{r.quantidadeAplicada?.toLocaleString('pt-BR')}</td>
                    <td>{r.dataRegistro ? new Date(r.dataRegistro + 'T00:00:00').toLocaleDateString('pt-BR') : '—'}</td>
                    <td>{r.idade || '—'}</td>
                    <td>
                      <button onClick={() => deletarRegistro(r.id)} className="btn-deletar" title="Deletar">🗑️</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
});

export default Dashboard;
