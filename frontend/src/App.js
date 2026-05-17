import React, { useState, useRef } from 'react';
import Dashboard from './components/Dashboard';
import FormularioInsercao from './components/FormularioInsercao';
import SIPNI from './components/SIPNI';
import { registroAPI } from './services/registroAPI';
import './App.css';

function App() {
  const [abaSelecionada, setAbaSelecionada] = useState('dashboard');
  const dashboardRef = useRef(null);

  const handleSucessoFormulario = () => {
    // Atualizar dashboard quando um novo registro é criado
    if (dashboardRef.current) {
      dashboardRef.current.recarregar();
    }
    // Alternar para aba de dashboard
    setAbaSelecionada('dashboard');
  };

  const handleCarregarCSV = async (e) => {
    const arquivo = e.target.files[0];
    if (!arquivo) return;

    try {
      const response = await registroAPI.carregarCSV(arquivo);
      alert(`${response.data.length} registros carregados com sucesso!`);
      if (dashboardRef.current) {
        dashboardRef.current.recarregar();
      }
    } catch (err) {
      alert('Erro ao carregar arquivo: ' + err.message);
    }
    
    // Limpar input
    e.target.value = '';
  };

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <div className="logo-section">
            <h1>🏥 Imunidata</h1>
            <p>Sistema de Análise de Cobertura Vacinal</p>
          </div>
          <nav className="header-nav">
            <a href="http://localhost:8080/api/h2-console" target="_blank" rel="noopener noreferrer" className="link-h2">
              H2-Console
            </a>
          </nav>
        </div>
      </header>

      <div className="app-container">
        <div className="tabs-container">
          <div className="tabs">
            <button
              className={`tab-button ${abaSelecionada === 'dashboard' ? 'ativo' : ''}`}
              onClick={() => setAbaSelecionada('dashboard')}
            >
              📊 Dashboard
            </button>
            <button
              className={`tab-button ${abaSelecionada === 'formulario' ? 'ativo' : ''}`}
              onClick={() => setAbaSelecionada('formulario')}
            >
              ➕ Novo Registro
            </button>
            <button
              className={`tab-button ${abaSelecionada === 'importar' ? 'ativo' : ''}`}
              onClick={() => setAbaSelecionada('importar')}
            >
              📤 Importar CSV
            </button>
            <button
              className={`tab-button ${abaSelecionada === 'sipni' ? 'ativo' : ''}`}
              onClick={() => setAbaSelecionada('sipni')}
            >
              🔗 SI-PNI / Datasus
            </button>
          </div>
        </div>

        <div className="conteudo-container">
          {abaSelecionada === 'dashboard' && (
            <div className="tab-content">
              <Dashboard ref={dashboardRef} />
            </div>
          )}

          {abaSelecionada === 'formulario' && (
            <div className="tab-content">
              <FormularioInsercao onSucesso={handleSucessoFormulario} />
            </div>
          )}

          {abaSelecionada === 'importar' && (
            <div className="tab-content">
              <div className="importar-container">
                <div className="importar-header">
                  <h2>Importar Dados CSV</h2>
                  <p>Carregue um arquivo CSV com os dados de vacinação</p>
                </div>
                <div className="importar-area">
                  <input
                    type="file"
                    accept=".csv"
                    onChange={handleCarregarCSV}
                    id="arquivo-csv"
                    className="file-input"
                  />
                  <label htmlFor="arquivo-csv" className="file-label">
                    <span className="file-icon">📁</span>
                    <span>Clique aqui ou arraste um arquivo CSV</span>
                  </label>
                  <p className="formato-esperado">
                    Formato esperado: municipio, estado, vacina, dose, quantidadeAplicada, dataRegistro (dd/MM/yyyy), idade — Também aceita CSV do Datasus (separado por ponto-e-vírgula)
                  </p>
                </div>
              </div>
            </div>
          )}

          {abaSelecionada === 'sipni' && (
            <div className="tab-content">
              <SIPNI />
            </div>
          )}
        </div>
      </div>

      <footer className="app-footer">
        <p>&copy; 2024 Imunidata - Sistema de Análise de Cobertura Vacinal | Desenvolvido para apoiar a tomada de decisão em saúde pública</p>
      </footer>
    </div>
  );
}

export default App;
