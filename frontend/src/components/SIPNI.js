import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SIPNI.css';

const ESTADOS_BR = [
  'AC','AL','AP','AM','BA','CE','DF','ES','GO','MA',
  'MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN',
  'RS','RO','RR','SC','SP','SE','TO'
];

function SIPNI() {
  const [tipoFiltro, setTipoFiltro]     = useState('estado');
  const [filtroValor, setFiltroValor]   = useState('SP');
  const [dadosSIPNI, setDadosSIPNI]     = useState([]);
  const [carregando, setCarregando]     = useState(false);
  const [erro, setErro]                 = useState(null);
  const [estatisticas, setEstatisticas] = useState(null);
  const [ano, setAno]                   = useState('2026');
  const [mes, setMes]                   = useState('04');
  // Filtros locais sobre os dados já carregados
  const [filtroLocalVacina, setFiltroLocalVacina]   = useState('');
  const [filtroLocalMunicipio, setFiltroLocalMunicipio] = useState('');

  const API_BASE = '/api/sipni';

  const buscarDados = async () => {
    try {
      setCarregando(true);
      setErro(null);
      setDadosSIPNI([]);

      let url = '';
      if (tipoFiltro === 'estado') {
        url = `${API_BASE}/cobertura/estado?estado=${encodeURIComponent(filtroValor)}`;
      } else if (tipoFiltro === 'municipio') {
        url = `${API_BASE}/cobertura/municipio?municipio=${encodeURIComponent(filtroValor)}`;
      } else if (tipoFiltro === 'vacina') {
        url = `${API_BASE}/cobertura/vacina?vacina=${encodeURIComponent(filtroValor)}`;
      } else if (tipoFiltro === 'periodo') {
        url = `${API_BASE}/cobertura/periodo?ano=${ano}&mes=${mes}&estado=${encodeURIComponent(filtroValor)}`;
      } else if (tipoFiltro === 'todos') {
        url = `${API_BASE}/resumo/estados`;
      }

      const response = await axios.get(url);
      setDadosSIPNI(response.data);
    } catch (err) {
      setErro('Erro ao buscar dados SI-PNI: ' + err.message);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticas = async () => {
    try {
      const response = await axios.get(`${API_BASE}/estatisticas`);
      // A API retorna string JSON — parsear se necessário
      const raw = response.data;
      setEstatisticas(typeof raw === 'string' ? JSON.parse(raw) : raw);
    } catch (err) {
      console.error('Erro ao carregar estatísticas:', err);
    }
  };

  useEffect(() => {
    buscarDados();
    carregarEstatisticas();
  }, []);

  // Filtro local sobre dados já retornados
  const dadosFiltrados = dadosSIPNI.filter(item => {
    const mv = !filtroLocalVacina || (item.descricao_vacina || '').toLowerCase().includes(filtroLocalVacina.toLowerCase())
                                  || (item.sigla_vacina || '').toLowerCase().includes(filtroLocalVacina.toLowerCase());
    const mm = !filtroLocalMunicipio || (item.nome_municipio_paciente || '').toLowerCase().includes(filtroLocalMunicipio.toLowerCase());
    return mv && mm;
  });

  return (
    <div className="sipni-container">
      <div className="sipni-header">
        <h2>🏥 SI-PNI / Datasus</h2>
        <p>Dados em tempo real do Programa Nacional de Imunizações</p>
      </div>

      {/* Estatísticas */}
      {estatisticas && (
        <div className="sipni-stats-row">
          <div className="sipni-stat-card">
            <span className="stat-label">Total de registros</span>
            <strong>{estatisticas.totalRegistros?.toLocaleString('pt-BR') ?? '—'}</strong>
          </div>
          <div className="sipni-stat-card">
            <span className="stat-label">Estados cobertos</span>
            <strong>{estatisticas.totalEstados ?? '—'}</strong>
          </div>
          <div className="sipni-stat-card">
            <span className="stat-label">Tipos de vacina</span>
            <strong>{estatisticas.totalVacinas ?? '—'}</strong>
          </div>
        </div>
      )}

      {erro && <div className="sipni-erro">{erro}</div>}

      {/* ── Filtros de busca ── */}
      <div className="sipni-filtros">
        <div className="filtro-grupo">
          <label>Tipo de busca:</label>
          <select value={tipoFiltro} onChange={e => { setTipoFiltro(e.target.value); setFiltroValor('SP'); }} className="filtro-select">
            <option value="todos">Todos os dados</option>
            <option value="estado">Por Estado</option>
            <option value="municipio">Por Município</option>
            <option value="vacina">Por Vacina</option>
            <option value="periodo">Por Período</option>
          </select>
        </div>

        {tipoFiltro === 'estado' && (
          <div className="filtro-grupo">
            <label>Estado (sigla):</label>
            <select value={filtroValor} onChange={e => setFiltroValor(e.target.value)} className="filtro-select">
              {ESTADOS_BR.map(uf => <option key={uf} value={uf}>{uf}</option>)}
            </select>
          </div>
        )}

        {tipoFiltro === 'municipio' && (
          <div className="filtro-grupo">
            <label>Município:</label>
            <input type="text" value={filtroValor} onChange={e => setFiltroValor(e.target.value)}
              placeholder="Ex: SAO PAULO" className="filtro-input" />
          </div>
        )}

        {tipoFiltro === 'vacina' && (
          <div className="filtro-grupo">
            <label>Vacina (nome ou sigla):</label>
            <input type="text" value={filtroValor} onChange={e => setFiltroValor(e.target.value)}
              placeholder="Ex: influenza, BCG, DNG..." className="filtro-input" />
          </div>
        )}

        {tipoFiltro === 'periodo' && (
          <>
            <div className="filtro-grupo">
              <label>Ano:</label>
              <input type="number" value={ano} onChange={e => setAno(e.target.value)} className="filtro-input" style={{width:90}} />
            </div>
            <div className="filtro-grupo">
              <label>Mês:</label>
              <select value={mes} onChange={e => setMes(e.target.value)} className="filtro-select">
                {['01','02','03','04','05','06','07','08','09','10','11','12'].map((m,i) =>
                  <option key={m} value={m}>{['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'][i]}</option>
                )}
              </select>
            </div>
            <div className="filtro-grupo">
              <label>Estado:</label>
              <select value={filtroValor} onChange={e => setFiltroValor(e.target.value)} className="filtro-select">
                {ESTADOS_BR.map(uf => <option key={uf} value={uf}>{uf}</option>)}
              </select>
            </div>
          </>
        )}

        <button onClick={buscarDados} className="btn-buscar" disabled={carregando}>
          {carregando ? '⏳ Buscando...' : '🔍 Buscar'}
        </button>
      </div>

      {/* ── Filtros locais sobre resultado ── */}
      {dadosSIPNI.length > 0 && (
        <div className="sipni-filtros-locais">
          <input type="text" placeholder="Filtrar por vacina..." value={filtroLocalVacina}
            onChange={e => setFiltroLocalVacina(e.target.value)} className="filtro-input" />
          <input type="text" placeholder="Filtrar por município..." value={filtroLocalMunicipio}
            onChange={e => setFiltroLocalMunicipio(e.target.value)} className="filtro-input" />
          {(filtroLocalVacina || filtroLocalMunicipio) &&
            <button onClick={() => { setFiltroLocalVacina(''); setFiltroLocalMunicipio(''); }} className="btn-limpar">✕ Limpar</button>
          }
          <span className="contagem-filtro">{dadosFiltrados.length} de {dadosSIPNI.length} registros</span>
        </div>
      )}

      {/* ── Tabela ── */}
      {carregando && <div className="carregando">⏳ Carregando dados da API SI-PNI / Datasus...</div>}

      {!carregando && dadosFiltrados.length > 0 && (
        <div className="sipni-tabela-container">
          <table className="sipni-tabela">
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
              {dadosFiltrados.map((item, idx) => (
                <tr key={idx}>
                  <td>{idx + 1}</td>
                  <td>{item.nome_municipio_paciente || item.municipio || '—'}</td>
                  <td><span className="badge-uf">{item.sigla_uf_paciente || item.estado || '—'}</span></td>
                  <td>
                    <span title={item.descricao_vacina}>
                      <strong>{item.sigla_vacina || '—'}</strong>
                      {item.descricao_vacina && <small className="vacina-desc"> {item.descricao_vacina}</small>}
                    </span>
                  </td>
                  <td>{item.descricao_dose_vacina || '—'}</td>
                  <td>{item.quantidadeAplicada ?? 1}</td>
                  <td>{item.data_vacina ? item.data_vacina.substring(0, 10) : (item.mes && item.ano ? `${item.mes}/${item.ano}` : '—')}</td>
                  <td>{item.numero_idade_paciente ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {!carregando && dadosSIPNI.length === 0 && !erro && (
        <div className="sem-dados">
          <p>Nenhum dado encontrado. Selecione um filtro e clique em "Buscar".</p>
        </div>
      )}
    </div>
  );
}

export default SIPNI;
