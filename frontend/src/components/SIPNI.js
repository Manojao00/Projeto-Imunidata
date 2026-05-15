import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SIPNI.css';

function SIPNI() {
  const [tipoFiltro, setTipoFiltro] = useState('estado');
  const [filtroValor, setFiltroValor] = useState('SP');
  const [dadosSIPNI, setDadosSIPNI] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState(null);
  const [estatisticas, setEstatisticas] = useState('');
  const [ano, setAno] = useState('2024');
  const [mes, setMes] = useState('05');

  const API_BASE = 'http://localhost:8080/api/sipni';

  const buscarDados = async () => {
    try {
      setCarregando(true);
      setErro(null);
      
      let url = '';
      
      if (tipoFiltro === 'estado') {
        url = `${API_BASE}/cobertura/estado?estado=${filtroValor}`;
      } else if (tipoFiltro === 'municipio') {
        url = `${API_BASE}/cobertura/municipio?municipio=${filtroValor}`;
      } else if (tipoFiltro === 'vacina') {
        url = `${API_BASE}/cobertura/vacina?vacina=${filtroValor}`;
      } else if (tipoFiltro === 'periodo') {
        url = `${API_BASE}/cobertura/periodo?ano=${ano}&mes=${mes}&estado=${filtroValor}`;
      }

      const response = await axios.get(url);
      setDadosSIPNI(response.data);
    } catch (err) {
      setErro('Erro ao buscar dados SI-PNI: ' + err.message);
      console.error('Erro:', err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticas = async () => {
    try {
      const response = await axios.get(`${API_BASE}/estatisticas`);
      setEstatisticas(response.data);
    } catch (err) {
      console.error('Erro ao carregar estatísticas:', err);
    }
  };

  useEffect(() => {
    buscarDados();
    carregarEstatisticas();
  }, []);

  const calcularCoberturaMedia = () => {
    if (dadosSIPNI.length === 0) return 0;
    const soma = dadosSIPNI.reduce((acc, item) => acc + item.cobertura, 0);
    return (soma / dadosSIPNI.length).toFixed(2);
  };

  const obterCorCobertura = (cobertura) => {
    if (cobertura >= 95) return '#28a745';
    if (cobertura >= 85) return '#ffc107';
    return '#dc3545';
  };

  return (
    <div className="sipni-container">
      <div className="sipni-header">
        <h2>🏥 Integração SI-PNI / Datasus</h2>
        <p>Sistema de Informações do Programa Nacional de Imunizações</p>
      </div>

      {erro && <div className="sipni-erro">{erro}</div>}

      <div className="sipni-card estatisticas">
        <h3>📊 Estatísticas Gerais</h3>
        <p>{estatisticas}</p>
      </div>

      <div className="sipni-filtros">
        <div className="filtro-grupo">
          <label htmlFor="tipo-filtro">Tipo de Busca:</label>
          <select
            id="tipo-filtro"
            value={tipoFiltro}
            onChange={(e) => setTipoFiltro(e.target.value)}
            className="filtro-select"
          >
            <option value="estado">Por Estado</option>
            <option value="municipio">Por Município</option>
            <option value="vacina">Por Vacina</option>
            <option value="periodo">Por Período</option>
          </select>
        </div>

        {tipoFiltro === 'estado' && (
          <div className="filtro-grupo">
            <label htmlFor="filtro-estado">Estado:</label>
            <select
              id="filtro-estado"
              value={filtroValor}
              onChange={(e) => setFiltroValor(e.target.value)}
              className="filtro-select"
            >
              <option value="SP">São Paulo</option>
              <option value="RJ">Rio de Janeiro</option>
              <option value="MG">Minas Gerais</option>
              <option value="DF">Distrito Federal</option>
              <option value="BA">Bahia</option>
            </select>
          </div>
        )}

        {tipoFiltro === 'municipio' && (
          <div className="filtro-grupo">
            <label htmlFor="filtro-municipio">Município:</label>
            <input
              id="filtro-municipio"
              type="text"
              value={filtroValor}
              onChange={(e) => setFiltroValor(e.target.value)}
              placeholder="Ex: São Paulo"
              className="filtro-input"
            />
          </div>
        )}

        {tipoFiltro === 'vacina' && (
          <div className="filtro-grupo">
            <label htmlFor="filtro-vacina">Vacina:</label>
            <select
              id="filtro-vacina"
              value={filtroValor}
              onChange={(e) => setFiltroValor(e.target.value)}
              className="filtro-select"
            >
              <option value="BCG">BCG</option>
              <option value="Poliomielite">Poliomielite</option>
              <option value="Gripe">Gripe</option>
              <option value="Sarampo">Sarampo</option>
              <option value="Difteria">Difteria</option>
              <option value="HPV">HPV</option>
            </select>
          </div>
        )}

        {tipoFiltro === 'periodo' && (
          <>
            <div className="filtro-grupo">
              <label htmlFor="filtro-ano">Ano:</label>
              <input
                id="filtro-ano"
                type="number"
                value={ano}
                onChange={(e) => setAno(e.target.value)}
                className="filtro-input"
              />
            </div>
            <div className="filtro-grupo">
              <label htmlFor="filtro-mes">Mês:</label>
              <select
                id="filtro-mes"
                value={mes}
                onChange={(e) => setMes(e.target.value)}
                className="filtro-select"
              >
                <option value="01">Janeiro</option>
                <option value="02">Fevereiro</option>
                <option value="03">Março</option>
                <option value="04">Abril</option>
                <option value="05">Maio</option>
                <option value="06">Junho</option>
                <option value="07">Julho</option>
                <option value="08">Agosto</option>
                <option value="09">Setembro</option>
                <option value="10">Outubro</option>
                <option value="11">Novembro</option>
                <option value="12">Dezembro</option>
              </select>
            </div>
            <div className="filtro-grupo">
              <label htmlFor="filtro-estado-periodo">Estado:</label>
              <select
                id="filtro-estado-periodo"
                value={filtroValor}
                onChange={(e) => setFiltroValor(e.target.value)}
                className="filtro-select"
              >
                <option value="SP">São Paulo</option>
                <option value="RJ">Rio de Janeiro</option>
                <option value="MG">Minas Gerais</option>
                <option value="DF">Distrito Federal</option>
              </select>
            </div>
          </>
        )}

        <button onClick={buscarDados} className="btn-buscar" disabled={carregando}>
          {carregando ? 'Buscando...' : 'Buscar'}
        </button>
      </div>

      {dadosSIPNI.length > 0 && (
        <>
          <div className="sipni-resumo">
            <div className="resumo-item">
              <span>Total de Registros:</span>
              <strong>{dadosSIPNI.length}</strong>
            </div>
            <div className="resumo-item">
              <span>Cobertura Média:</span>
              <strong>{calcularCoberturaMedia()}%</strong>
            </div>
          </div>

          <div className="sipni-tabela-container">
            <table className="sipni-tabela">
              <thead>
                <tr>
                  <th>Município</th>
                  <th>Estado</th>
                  <th>Vacina</th>
                  <th>Cobertura</th>
                  <th>Aplicadas</th>
                  <th>População Alvo</th>
                  <th>Data</th>
                </tr>
              </thead>
              <tbody>
                {dadosSIPNI.map((item, index) => (
                  <tr key={index}>
                    <td>{item.nomeMunicipio}</td>
                    <td>{item.estado}</td>
                    <td>{item.vacina}</td>
                    <td>
                      <div className="cobertura-bar">
                        <div
                          className="cobertura-preenchido"
                          style={{
                            width: `${item.cobertura}%`,
                            backgroundColor: obterCorCobertura(item.cobertura)
                          }}
                        >
                          {item.cobertura.toFixed(1)}%
                        </div>
                      </div>
                    </td>
                    <td>{item.quantidadeAplicada.toLocaleString('pt-BR')}</td>
                    <td>{item.populacaoAlvo.toLocaleString('pt-BR')}</td>
                    <td>{item.mes}/{item.ano}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {!carregando && dadosSIPNI.length === 0 && !erro && (
        <div className="sem-dados">
          <p>Nenhum dado encontrado. Clique em "Buscar" para carregar dados.</p>
        </div>
      )}

      {carregando && (
        <div className="carregando">
          <p>Carregando dados da API SI-PNI...</p>
        </div>
      )}
    </div>
  );
}

export default SIPNI;
