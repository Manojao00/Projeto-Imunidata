import React, { useState, useEffect, useImperativeHandle, useCallback } from 'react';
import { registroAPI } from '../services/registroAPI';
import axios from 'axios';
import './Dashboard.css';

// ── Constantes ────────────────────────────────────────────────────────────────
const ESTADOS_BR = ['AC','AL','AP','AM','BA','CE','DF','ES','GO','MA','MT','MS','MG','PA','PB','PR','PE','PI','RJ','RN','RS','RO','RR','SC','SP','SE','TO'];
const VACINAS    = ['BCG','Gripe','Poliomielite','Sarampo','Difteria','Hepatite B','HPV','Dengue','COVID-19'];
const DOSES      = ['1ª','2ª','3ª','Reforço','Única','—'];
const POR_PAGINA = 100;

// ── Componente de Paginação ───────────────────────────────────────────────────
function Paginacao({ total, pagina, onMudar }) {
  const totalPags = Math.ceil(total / POR_PAGINA);
  if (totalPags <= 1) return null;

  // Janela de páginas: mostra até 7 botões ao redor da atual
  const inicio = Math.max(1, pagina - 3);
  const fim    = Math.min(totalPags, pagina + 3);
  const paginas = [];
  for (let p = inicio; p <= fim; p++) paginas.push(p);

  return (
    <div className="paginacao">
      <span className="pag-info">
        Página <strong>{pagina}</strong> de <strong>{totalPags}</strong>
        &nbsp;·&nbsp;{total} registros
      </span>
      <div className="pag-botoes">
        <button onClick={() => onMudar(1)}       disabled={pagina === 1}        className="pag-btn">«</button>
        <button onClick={() => onMudar(pagina-1)} disabled={pagina === 1}       className="pag-btn">‹</button>
        {inicio > 1 && <span className="pag-gap">…</span>}
        {paginas.map(p =>
          <button key={p} onClick={() => onMudar(p)}
                  className={`pag-btn ${p === pagina ? 'ativo' : ''}`}>{p}</button>
        )}
        {fim < totalPags && <span className="pag-gap">…</span>}
        <button onClick={() => onMudar(pagina+1)} disabled={pagina === totalPags} className="pag-btn">›</button>
        <button onClick={() => onMudar(totalPags)} disabled={pagina === totalPags} className="pag-btn">»</button>
      </div>
    </div>
  );
}

// ── Sexo legível ──────────────────────────────────────────────────────────────
function sexoLabel(s) {
  if (!s) return '—';
  const v = s.trim().toUpperCase();
  if (v === 'F' || v === 'FEMININO')   return '♀ Feminino';
  if (v === 'M' || v === 'MASCULINO')  return '♂ Masculino';
  return s;
}

// ── Modal de Edição ───────────────────────────────────────────────────────────
function ModalEdicao({ registro, onSalvar, onFechar }) {
  const [form, setForm] = useState({
    municipio:          registro.municipio          || '',
    estado:             registro.estado             || '',
    vacina:             registro.vacina             || '',
    dose:               registro.dose               || '',
    quantidadeAplicada: registro.quantidadeAplicada || '',
    dataRegistro:       registro.dataRegistro       || '',
    idade:              registro.idade              || '',
  });
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro]         = useState(null);

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSalvar = async () => {
    if (!form.municipio || !form.estado || !form.vacina || !form.quantidadeAplicada || !form.dataRegistro) {
      setErro('Preencha todos os campos obrigatórios (*)'); return;
    }
    try {
      setSalvando(true); setErro(null);
      await registroAPI.atualizar(registro.id, { ...form, quantidadeAplicada: parseInt(form.quantidadeAplicada) });
      onSalvar();
    } catch (err) {
      setErro('Erro ao salvar: ' + err.message);
    } finally { setSalvando(false); }
  };

  return (
    <div className="modal-overlay" onClick={e => { if (e.target.className === 'modal-overlay') onFechar(); }}>
      <div className="modal-box">
        <div className="modal-header">
          <h3>✏️ Editar Registro #{registro.id}</h3>
          <button className="modal-fechar" onClick={onFechar}>✕</button>
        </div>
        {erro && <div className="modal-erro">{erro}</div>}
        <div className="modal-form">
          <div className="modal-row">
            <div className="modal-campo">
              <label>Município *</label>
              <input name="municipio" value={form.municipio} onChange={handleChange} placeholder="Ex: São Paulo" />
            </div>
            <div className="modal-campo">
              <label>Estado *</label>
              <select name="estado" value={form.estado} onChange={handleChange}>
                <option value="">Selecione</option>
                {ESTADOS_BR.map(uf => <option key={uf} value={uf}>{uf}</option>)}
              </select>
            </div>
          </div>
          <div className="modal-row">
            <div className="modal-campo">
              <label>Vacina *</label>
              <input name="vacina" value={form.vacina} onChange={handleChange} placeholder="Ex: BCG" list="lista-vacinas" />
              <datalist id="lista-vacinas">{VACINAS.map(v => <option key={v} value={v} />)}</datalist>
            </div>
            <div className="modal-campo">
              <label>Dose</label>
              <select name="dose" value={form.dose} onChange={handleChange}>
                {DOSES.map(d => <option key={d} value={d}>{d}</option>)}
              </select>
            </div>
          </div>
          <div className="modal-row">
            <div className="modal-campo">
              <label>Quantidade Aplicada *</label>
              <input name="quantidadeAplicada" type="number" min="0" value={form.quantidadeAplicada} onChange={handleChange} />
            </div>
            <div className="modal-campo">
              <label>Data do Registro *</label>
              <input name="dataRegistro" type="date" value={form.dataRegistro} onChange={handleChange} />
            </div>
          </div>
          <div className="modal-row">
            <div className="modal-campo">
              <label>Idade</label>
              <input name="idade" value={form.idade} onChange={handleChange} placeholder="Ex: 30, 0-4, 60+" />
            </div>
          </div>
        </div>
        <div className="modal-footer">
          <button className="btn-cancelar" onClick={onFechar} disabled={salvando}>Cancelar</button>
          <button className="btn-salvar" onClick={handleSalvar} disabled={salvando}>
            {salvando ? '⏳ Salvando...' : '💾 Salvar'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Dashboard Principal ───────────────────────────────────────────────────────
const Dashboard = React.forwardRef((props, ref) => {
  // Dados brutos
  const [registros,     setRegistros]     = useState([]);
  const [dadosAPI,      setDadosAPI]      = useState([]);
  const [carregandoCSV, setCarregandoCSV] = useState(true);
  const [carregandoAPI, setCarregandoAPI] = useState(true);

  // Filtros gerais (ambas as abas)
  const [filtroVacina,    setFiltroVacina]    = useState('');
  const [filtroEstado,    setFiltroEstado]    = useState('');
  const [filtroMunicipio, setFiltroMunicipio] = useState('');

  // Filtro por data — agora funciona nas DUAS abas
  const [modoData,    setModoData]    = useState('nenhum'); // 'nenhum' | 'data' | 'periodo'
  const [dataExata,   setDataExata]   = useState('');
  const [dataInicio,  setDataInicio]  = useState('');
  const [dataFim,     setDataFim]     = useState('');
  const [buscandoData, setBuscandoData] = useState(false);

  // Paginação separada por aba
  const [paginaAPI, setPaginaAPI] = useState(1);
  const [paginaCSV, setPaginaCSV] = useState(1);

  // UI
  const [abaAtiva,     setAbaAtiva]     = useState('datasus');
  const [registroEdit, setRegistroEdit] = useState(null);
  const [erro,         setErro]         = useState(null);
  const [msgSucesso,   setMsgSucesso]   = useState(null);

  useImperativeHandle(ref, () => ({ recarregar: carregarCSV }));
  useEffect(() => { carregarCSV(); carregarDatasus(); }, []);

  // Resetar página ao mudar filtros
  useEffect(() => { setPaginaAPI(1); }, [filtroVacina, filtroEstado, filtroMunicipio, dadosAPI]);
  useEffect(() => { setPaginaCSV(1); }, [filtroVacina, filtroEstado, filtroMunicipio, registros]);

  // ── Loaders ──────────────────────────────────────────────────────────────────
  const carregarCSV = async () => {
    try {
      setCarregandoCSV(true);
      const res = await registroAPI.obterTodos();
      setRegistros(res.data); setErro(null);
    } catch (err) { setErro('Erro ao carregar registros: ' + err.message); }
    finally { setCarregandoCSV(false); }
  };

  const carregarDatasus = async () => {
    try {
      setCarregandoAPI(true);
      const res = await axios.get('/api/sipni/resumo/estados');
      setDadosAPI(res.data);
    } catch (err) { console.error('Datasus:', err.message); }
    finally { setCarregandoAPI(false); }
  };

  // ── Busca por data — aplica nas DUAS fontes ───────────────────────────────
  const buscarPorData = async () => {
    if (modoData === 'data' && !dataExata) return;
    if (modoData === 'periodo' && (!dataInicio || !dataFim)) return;
    try {
      setBuscandoData(true);

      // --- Banco local: vai ao backend ---
      let resCSV;
      if (modoData === 'data') {
        resCSV = await registroAPI.buscarPorData(dataExata);
      } else {
        resCSV = await registroAPI.buscarPorPeriodo(dataInicio, dataFim, filtroEstado || null);
      }
      setRegistros(resCSV.data);
      setPaginaCSV(1);

      // --- Datasus: filtra localmente pelo campo data_vacina ---
      setDadosAPI(prev => {
        const filtrado = prev.length > 0 ? prev : dadosAPI; // usa cache
        return filtrarAPIporData(filtrado);
      });
      setPaginaAPI(1);

    } catch (err) { setErro('Erro na busca por data: ' + err.message); }
    finally { setBuscandoData(false); }
  };

  // Filtra array do Datasus por data_vacina ("2026-04-16 00:00:00-03")
  const filtrarAPIporData = useCallback((lista) => {
    if (modoData === 'nenhum') return lista;
    return lista.filter(item => {
      const dv = item.data_vacina;
      if (!dv || dv.length < 10) return false;
      const dataItem = dv.substring(0, 10); // "yyyy-MM-dd"
      if (modoData === 'data')    return dataItem === dataExata;
      if (modoData === 'periodo') return dataItem >= dataInicio && dataItem <= dataFim;
      return true;
    });
  }, [modoData, dataExata, dataInicio, dataFim]);

  const limparFiltroData = () => {
    setModoData('nenhum'); setDataExata(''); setDataInicio(''); setDataFim('');
    carregarCSV(); carregarDatasus();
  };

  // ── Edição / Deleção ──────────────────────────────────────────────────────
  const salvarEdicao = async () => {
    setRegistroEdit(null); await carregarCSV();
    setMsgSucesso('Registro atualizado!'); setTimeout(() => setMsgSucesso(null), 3000);
  };

  const deletarRegistro = async (id) => {
    if (!window.confirm('Deletar este registro?')) return;
    try {
      await registroAPI.deletar(id); await carregarCSV();
      setMsgSucesso('Registro deletado.'); setTimeout(() => setMsgSucesso(null), 3000);
    } catch (err) { setErro('Erro ao deletar: ' + err.message); }
  };

  // ── Filtros combinados ────────────────────────────────────────────────────
  const filtrarCSV = r => {
    const mv = !filtroVacina    || r.vacina?.toLowerCase().includes(filtroVacina.toLowerCase());
    const me = !filtroEstado    || r.estado === filtroEstado;
    const mm = !filtroMunicipio || r.municipio?.toLowerCase().includes(filtroMunicipio.toLowerCase());
    return mv && me && mm;
  };

  const filtrarAPIbase = r => {
    const mv = !filtroVacina    || r.descricao_vacina?.toLowerCase().includes(filtroVacina.toLowerCase())
                                || r.sigla_vacina?.toLowerCase().includes(filtroVacina.toLowerCase());
    const me = !filtroEstado    || r.sigla_uf_paciente === filtroEstado;
    const mm = !filtroMunicipio || r.nome_municipio_paciente?.toLowerCase().includes(filtroMunicipio.toLowerCase());
    return mv && me && mm;
  };

  const csvFiltrados = registros.filter(filtrarCSV);
  // Datasus: filtro geral + filtro de data local
  const apiFiltrados = filtrarAPIporData(dadosAPI.filter(filtrarAPIbase));

  // ── Paginação ─────────────────────────────────────────────────────────────
  const csvPagina = csvFiltrados.slice((paginaCSV - 1) * POR_PAGINA, paginaCSV * POR_PAGINA);
  const apiPagina = apiFiltrados.slice((paginaAPI - 1) * POR_PAGINA, paginaAPI * POR_PAGINA);

  const todosEstados = [...new Set([
    ...registros.map(r => r.estado),
    ...dadosAPI.map(r => r.sigla_uf_paciente),
  ].filter(Boolean))].sort();

  const limparFiltros = () => { setFiltroVacina(''); setFiltroEstado(''); setFiltroMunicipio(''); };

  // ── Render ────────────────────────────────────────────────────────────────
  return (
    <div className="dashboard-container">

      {/* Header */}
      <div className="dashboard-header">
        <h2>📊 Dashboard de Vacinação</h2>
        <div className="dashboard-stats">
          <span className="stat-badge">🏥 Datasus: <strong>{carregandoAPI ? '…' : dadosAPI.length}</strong></span>
          <span className="stat-badge">📁 Banco local: <strong>{carregandoCSV ? '…' : registros.length}</strong></span>
        </div>
      </div>

      {erro       && <div className="erro-mensagem">{erro}<button className="fechar-aviso" onClick={() => setErro(null)}>✕</button></div>}
      {msgSucesso && <div className="sucesso-mensagem">{msgSucesso}</div>}

      {/* ── Filtros Gerais ── */}
      <div className="filtros-container">
        <div className="filtro-grupo">
          <label>Vacina:</label>
          <input type="text" placeholder="Ex: influenza, BCG…" value={filtroVacina}
                 onChange={e => setFiltroVacina(e.target.value)} className="filtro-input" />
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
          <input type="text" placeholder="Ex: São Paulo…" value={filtroMunicipio}
                 onChange={e => setFiltroMunicipio(e.target.value)} className="filtro-input" />
        </div>
        <button onClick={limparFiltros} className="btn-limpar">Limpar</button>
        <button onClick={() => { carregarCSV(); carregarDatasus(); limparFiltroData(); }} className="btn-atualizar">↻ Atualizar</button>
      </div>

      {/* ── Filtro por Data — vale para AMBAS as abas ── */}
      <div className="filtro-data-container">
        <div className="filtro-data-header">
          <span className="filtro-data-titulo">📅 Filtrar por Data <span className="filtro-data-scope">(Datasus + Banco Local)</span></span>
          <div className="filtro-data-modos">
            {[['nenhum','Sem filtro'],['data','Data exata'],['periodo','Período']].map(([val, label]) => (
              <label key={val} className={modoData === val ? 'modo-ativo' : ''}>
                <input type="radio" value={val} checked={modoData === val}
                       onChange={() => { setModoData(val); if (val === 'nenhum') limparFiltroData(); }} />
                {label}
              </label>
            ))}
          </div>
        </div>

        {modoData === 'data' && (
          <div className="filtro-data-campos">
            <div className="filtro-grupo">
              <label>Data:</label>
              <input type="date" value={dataExata} onChange={e => setDataExata(e.target.value)} className="filtro-input" />
            </div>
            <button className="btn-buscar-data" onClick={buscarPorData} disabled={buscandoData || !dataExata}>
              {buscandoData ? '⏳' : '🔍 Buscar'}
            </button>
            <button className="btn-limpar-data" onClick={limparFiltroData}>✕ Limpar</button>
          </div>
        )}

        {modoData === 'periodo' && (
          <div className="filtro-data-campos">
            <div className="filtro-grupo">
              <label>De:</label>
              <input type="date" value={dataInicio} onChange={e => setDataInicio(e.target.value)} className="filtro-input" />
            </div>
            <div className="filtro-grupo">
              <label>Até:</label>
              <input type="date" value={dataFim} onChange={e => setDataFim(e.target.value)} className="filtro-input" />
            </div>
            <button className="btn-buscar-data" onClick={buscarPorData} disabled={buscandoData || !dataInicio || !dataFim}>
              {buscandoData ? '⏳' : '🔍 Buscar'}
            </button>
            <button className="btn-limpar-data" onClick={limparFiltroData}>✕ Limpar</button>
          </div>
        )}
      </div>

      {/* ── Abas ── */}
      <div className="dashboard-abas">
        <button className={`aba-btn ${abaAtiva === 'datasus' ? 'ativo' : ''}`} onClick={() => setAbaAtiva('datasus')}>
          🔗 SI-PNI / Datasus ({carregandoAPI ? '…' : apiFiltrados.length})
        </button>
        <button className={`aba-btn ${abaAtiva === 'csv' ? 'ativo' : ''}`} onClick={() => setAbaAtiva('csv')}>
          📁 Banco Local ({carregandoCSV ? '…' : csvFiltrados.length})
        </button>
      </div>

      {/* ══ Tabela Datasus ══ */}
      {abaAtiva === 'datasus' && (
        <div className="tabela-container">
          {carregandoAPI ? (
            <div className="carregando-msg">⏳ Carregando dados da API Datasus…</div>
          ) : apiFiltrados.length === 0 ? (
            <div className="sem-dados-msg">Nenhum dado encontrado. Tente outros filtros.</div>
          ) : (
            <>
              <Paginacao total={apiFiltrados.length} pagina={paginaAPI} onMudar={p => { setPaginaAPI(p); window.scrollTo(0,0); }} />
              <table className="tabela-registros">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Município</th>
                    <th>Estado</th>
                    <th>Vacina</th>
                    <th>Dose</th>
                    <th>Sexo</th>
                    <th>Data</th>
                    <th>Idade</th>
                  </tr>
                </thead>
                <tbody>
                  {apiPagina.map((item, idx) => (
                    <tr key={idx}>
                      <td className="col-num">{(paginaAPI - 1) * POR_PAGINA + idx + 1}</td>
                      <td>{item.nome_municipio_paciente || item.municipio || '—'}</td>
                      <td><span className="badge-estado">{item.sigla_uf_paciente || item.estado || '—'}</span></td>
                      <td>
                        <span className="vacina-cell" title={item.descricao_vacina}>
                          <strong>{item.sigla_vacina || '—'}</strong>
                          {item.descricao_vacina && <small className="vacina-sub"> {item.descricao_vacina}</small>}
                        </span>
                      </td>
                      <td>{item.descricao_dose_vacina || '—'}</td>
                      <td>
                        <span className={`badge-sexo ${(item.tipo_sexo_paciente || '').toUpperCase() === 'F' ? 'fem' : (item.tipo_sexo_paciente || '').toUpperCase() === 'M' ? 'mas' : ''}`}>
                          {sexoLabel(item.tipo_sexo_paciente)}
                        </span>
                      </td>
                      <td className="col-data">{item.data_vacina ? item.data_vacina.substring(0, 10) : `${item.mes}/${item.ano}`}</td>
                      <td>{item.numero_idade_paciente ?? '—'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <Paginacao total={apiFiltrados.length} pagina={paginaAPI} onMudar={p => { setPaginaAPI(p); window.scrollTo(0,0); }} />
            </>
          )}
        </div>
      )}

      {/* ══ Tabela Banco Local ══ */}
      {abaAtiva === 'csv' && (
        <div className="tabela-container">
          {carregandoCSV ? (
            <div className="carregando-msg">⏳ Carregando registros locais…</div>
          ) : csvFiltrados.length === 0 ? (
            <div className="sem-dados-msg">
              Nenhum registro local encontrado. Use "Importar CSV" ou "Novo Registro".
            </div>
          ) : (
            <>
              <Paginacao total={csvFiltrados.length} pagina={paginaCSV} onMudar={p => { setPaginaCSV(p); window.scrollTo(0,0); }} />
              <table className="tabela-registros">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Município</th>
                    <th>Estado</th>
                    <th>Vacina</th>
                    <th>Dose</th>
                    <th>Qtd Aplicada</th>
                    <th>Data</th>
                    <th>Idade</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {csvPagina.map(r => (
                    <tr key={r.id}>
                      <td className="col-num">{r.id}</td>
                      <td>{r.municipio}</td>
                      <td><span className="badge-estado">{r.estado}</span></td>
                      <td>{r.vacina}</td>
                      <td>{r.dose}</td>
                      <td>{r.quantidadeAplicada?.toLocaleString('pt-BR')}</td>
                      <td className="col-data">
                        {r.dataRegistro ? new Date(r.dataRegistro + 'T00:00:00').toLocaleDateString('pt-BR') : '—'}
                      </td>
                      <td>{r.idade || '—'}</td>
                      <td className="acoes-cell">
                        <button onClick={() => setRegistroEdit(r)} className="btn-editar" title="Editar">✏️</button>
                        <button onClick={() => deletarRegistro(r.id)} className="btn-deletar" title="Deletar">🗑️</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <Paginacao total={csvFiltrados.length} pagina={paginaCSV} onMudar={p => { setPaginaCSV(p); window.scrollTo(0,0); }} />
            </>
          )}
        </div>
      )}

      {/* Modal */}
      {registroEdit && (
        <ModalEdicao registro={registroEdit} onSalvar={salvarEdicao} onFechar={() => setRegistroEdit(null)} />
      )}
    </div>
  );
});

export default Dashboard;
