import React, { useState } from 'react';
import { registroAPI } from '../services/registroAPI';
import './FormularioInsercao.css';

function FormularioInsercao({ onSucesso }) {
  const [formData, setFormData] = useState({
    municipio: '',
    estado: '',
    vacina: '',
    dose: '1ª',
    quantidadeAplicada: '',
    dataRegistro: '',
    faixaEtaria: '0-4',
  });

  const [carregando, setCarregando] = useState(false);
  const [mensagem, setMensagem] = useState(null);
  const [tipoMensagem, setTipoMensagem] = useState(''); // 'sucesso' ou 'erro'

  const vacinasDisponiveis = ['BCG', 'Gripe', 'Poliomielite', 'Sarampo', 'Difteria', 'Hepatite B', 'HPV'];
  const dosesDisponiveis = ['1ª', '2ª', 'reforço'];
  const faixasEtarias = ['0-4', '5-9', '10-14', '15-19', '20-29', '30-39', '40-49', '50-59', '60+'];
  const estadosBrasil = ['AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validação básica
    if (!formData.municipio.trim() || !formData.estado || !formData.vacina || !formData.quantidadeAplicada || !formData.dataRegistro) {
      setMensagem('Por favor, preencha todos os campos obrigatórios');
      setTipoMensagem('erro');
      return;
    }

    try {
      setCarregando(true);
      setMensagem(null);
      
      const dados = {
        ...formData,
        quantidadeAplicada: parseInt(formData.quantidadeAplicada),
      };

      await registroAPI.criar(dados);
      
      setMensagem('Registro criado com sucesso!');
      setTipoMensagem('sucesso');
      
      // Limpar formulário
      setFormData({
        municipio: '',
        estado: '',
        vacina: '',
        dose: '1ª',
        quantidadeAplicada: '',
        dataRegistro: '',
        faixaEtaria: '0-4',
      });

      // Callback para atualizar a lista
      if (onSucesso) {
        onSucesso();
      }

      // Limpar mensagem após 3 segundos
      setTimeout(() => setMensagem(null), 3000);
    } catch (err) {
      setMensagem('Erro ao criar registro: ' + err.message);
      setTipoMensagem('erro');
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="formulario-container">
      <div className="formulario-header">
        <h2>Cadastrar Nova Vacinação</h2>
        <p>Preencha o formulário abaixo para registrar uma nova aplicação de vacina</p>
      </div>

      {mensagem && (
        <div className={`mensagem ${tipoMensagem}`}>
          {mensagem}
        </div>
      )}

      <form onSubmit={handleSubmit} className="formulario">
        <div className="form-grupo">
          <label htmlFor="municipio">Município *</label>
          <input
            type="text"
            id="municipio"
            name="municipio"
            value={formData.municipio}
            onChange={handleChange}
            placeholder="Ex: São Paulo"
            required
          />
        </div>

        <div className="form-grupo">
          <label htmlFor="estado">Estado *</label>
          <select
            id="estado"
            name="estado"
            value={formData.estado}
            onChange={handleChange}
            required
          >
            <option value="">Selecione um estado</option>
            {estadosBrasil.map(estado => (
              <option key={estado} value={estado}>{estado}</option>
            ))}
          </select>
        </div>

        <div className="form-grupo">
          <label htmlFor="vacina">Vacina *</label>
          <select
            id="vacina"
            name="vacina"
            value={formData.vacina}
            onChange={handleChange}
            required
          >
            <option value="">Selecione uma vacina</option>
            {vacinasDisponiveis.map(vacina => (
              <option key={vacina} value={vacina}>{vacina}</option>
            ))}
          </select>
        </div>

        <div className="form-grupo">
          <label htmlFor="dose">Dose *</label>
          <select
            id="dose"
            name="dose"
            value={formData.dose}
            onChange={handleChange}
            required
          >
            {dosesDisponiveis.map(dose => (
              <option key={dose} value={dose}>{dose}</option>
            ))}
          </select>
        </div>

        <div className="form-grupo">
          <label htmlFor="quantidadeAplicada">Quantidade Aplicada *</label>
          <input
            type="number"
            id="quantidadeAplicada"
            name="quantidadeAplicada"
            value={formData.quantidadeAplicada}
            onChange={handleChange}
            placeholder="Ex: 100"
            min="1"
            required
          />
        </div>

        <div className="form-grupo">
          <label htmlFor="dataRegistro">Data do Registro *</label>
          <input
            type="date"
            id="dataRegistro"
            name="dataRegistro"
            value={formData.dataRegistro}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-grupo">
          <label htmlFor="faixaEtaria">Faixa Etária</label>
          <select
            id="faixaEtaria"
            name="faixaEtaria"
            value={formData.faixaEtaria}
            onChange={handleChange}
          >
            {faixasEtarias.map(faixa => (
              <option key={faixa} value={faixa}>{faixa}</option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn-enviar" disabled={carregando}>
          {carregando ? 'Enviando...' : 'Enviar Registro'}
        </button>
      </form>
    </div>
  );
}

export default FormularioInsercao;
