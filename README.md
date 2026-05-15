# 🏥 Imunidata - Sistema de Análise de Cobertura Vacinal

## 📋 Visão Geral

Imunidata é uma aplicação **Full Stack** (Java Spring Boot + React) desenvolvida para análise e gerenciamento de dados de cobertura vacinal por região e faixa etária. O sistema foi projetado para apoiar a tomada de decisão em secretarias de saúde e unidades de pronto atendimento.

## 🎯 Objetivos

- ✅ Consultar e analisar dados reais sobre cobertura vacinal
- ✅ Filtrar informações por vacina, estado e município
- ✅ Gerenciar registros de vacinação (CRUD completo)
- ✅ Importar dados de arquivos CSV
- ✅ Fornecer resumos por estado e região

## 🏗️ Arquitetura do Projeto

### Backend (Spring Boot)

O backend segue a **arquitetura em camadas** conforme solicitado:

```
src/main/java/com/imunidata/
├── model/                    # @Entity - RegistroVacinacao
├── repository/              # JpaRepository - RegistroVacinacaoRepository
├── service/                 # Lógica de negócio - RegistroVacinacaoService
├── controller/              # REST Endpoints - RegistroVacinacaoController
├── config/                  # Configurações - DataLoaderConfig
└── ImunidataApplication.java # Classe principal
```

**Estrutura da Entidade `RegistroVacinacao`:**

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | Long | Identificador único |
| municipio | String | Município onde foi feita a vacinação |
| estado | String | Estado/UF |
| vacina | String | Tipo de vacina (BCG, Gripe, etc) |
| dose | String | Dose aplicada (1ª, 2ª, reforço) |
| quantidadeAplicada | Integer | Quantidade de vacinas aplicadas |
| dataRegistro | LocalDate | Data do registro |
| faixaEtaria | String | Faixa etária da população (0-4, 5-9, etc) |

### Frontend (React)

```
frontend/src/
├── components/
│   ├── Dashboard.js         # Lista e filtra registros
│   ├── Dashboard.css
│   ├── FormularioInsercao.js # Cadastro de novos registros
│   └── FormularioInsercao.css
├── services/
│   └── registroAPI.js        # Serviço de API (Axios)
├── App.js                    # Componente principal
├── App.css
└── index.js
```

## 🚀 Como Executar

### Pré-requisitos

- Java 17+
- Node.js 16+
- Maven 3.8+
- npm ou yarn

### 1. Backend - Spring Boot

```bash
# Navegue até a raiz do projeto
cd /workspaces/Projeto-Imunidata

# Compile e execute com Maven
mvn clean install
mvn spring-boot:run
```

O backend estará disponível em: `http://localhost:8080/api`

**Endpoints principais:**
- `GET /api/registros` - Listar todos os registros
- `GET /api/registros/{id}` - Obter registro específico
- `POST /api/registros` - Criar novo registro
- `PUT /api/registros/{id}` - Atualizar registro
- `DELETE /api/registros/{id}` - Deletar registro
- `GET /api/registros/buscar/vacina?nome=BCG` - Buscar por vacina
- `GET /api/registros/buscar/estado?nome=SP` - Buscar por estado
- `POST /api/registros/carregar-csv` - Carregar dados CSV
- `GET /api/h2-console` - Acessar H2-Console

**Endpoints SI-PNI / Datasus:**
- `GET /api/sipni/cobertura/estado?estado=SP` - Cobertura por estado
- `GET /api/sipni/cobertura/municipio?municipio=São Paulo` - Cobertura por município
- `GET /api/sipni/cobertura/vacina?vacina=BCG` - Cobertura por vacina
- `GET /api/sipni/cobertura/periodo?ano=2024&mes=05&estado=SP` - Cobertura em período
- `GET /api/sipni/resumo/estados` - Resumo de cobertura por estado
- `GET /api/sipni/estatisticas` - Estatísticas gerais de cobertura
- `GET /api/sipni/health` - Health check SI-PNI

### 2. Frontend - React

Em outro terminal:

```bash
# Navegue até a pasta frontend
cd /workspaces/Projeto-Imunidata/frontend

# Instale as dependências
npm install

# Execute a aplicação
npm start
```

O frontend estará disponível em: `http://localhost:3000`

## 📊 Funcionalidades

### Dashboard de Listagem
- Tabela organizada com histórico completo de vacinação
- Visualização de todos os campos importantes
- Ordenação por colunas
- Ação de deleção de registros

### Filtros em Tempo Real
- Filtro por tipo de vacina (busca dinâmica)
- Filtro por estado (dropdown)
- Filtro combinado (vacina + estado)
- Botão para limpar todos os filtros

### Formulário de Inserção
- Cadastro de novos registros de vacinação
- Validação de campos obrigatórios
- Seleção de vacinas predefinidas
- Seleção de doses (1ª, 2ª, reforço)
- Seleção de faixa etária
- Feedback visual de sucesso/erro

### Importação de CSV
- Carregamento de arquivos CSV
- Suporte ao formato: `municipio,estado,vacina,dose,quantidadeAplicada,dataRegistro,faixaEtaria`
- Formato de data esperado: `dd/MM/yyyy`
- Carregamento automático de dados ao iniciar a aplicação

### H2-Console
- Acesso direto ao banco de dados
- URL: `http://localhost:8080/api/h2-console`
- Usuário: `sa`
- Senha: (deixe em branco)

### SI-PNI / Datasus Integration
- **Integração com API SI-PNI (Sistema de Informações do Programa Nacional de Imunizações)**
- Busca de cobertura vacinal por estado, município e vacina
- Consulta de dados em períodos específicos
- Resumos e estatísticas de cobertura
- Tabela interativa com visualização de coberturas em barras de progresso
- Cache automático de requisições para melhor performance
- Dados simulados da estrutura real da API Datasus para testes

## 📁 Estrutura de Dados CSV

O arquivo CSV deve seguir este formato:

```csv
municipio,estado,vacina,dose,quantidadeAplicada,dataRegistro,faixaEtaria
São Paulo,SP,BCG,1ª,1500,01/01/2024,0-4
Rio de Janeiro,RJ,Gripe,1ª,1200,02/01/2024,5-9
```

Um arquivo de exemplo `dados_vacinacao.csv` está incluído em `src/main/resources/`.

## 🔧 Configurações

### application.properties (Backend)

```properties
server.port=8080
server.servlet.context-path=/api

# H2 Database
spring.datasource.url=jdbc:h2:mem:imunidatadb
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
```

### Configuração de CORS (Frontend)

A API está configurada para aceitar requisições do frontend em `http://localhost:3000`.

## 📦 Dependências Principais

### Backend
- Spring Boot 3.1.5
- Spring Data JPA
- H2 Database
- OpenCSV 5.7
- Lombok

### Frontend
- React 18.2.0
- Axios 1.4.0
- React Router 6.14.0

## ✅ Checklist de Funcionalidades

- [x] Entidade RegistroVacinacao com todos os atributos
- [x] Repository com métodos de busca especializados
- [x] Service com lógica de negócio e leitura de CSV
- [x] Controller com endpoints REST completos
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Banco de dados H2 com acesso via console
- [x] Dashboard React com tabela organizada
- [x] Filtros em tempo real (vacina e estado)
- [x] Formulário de inserção de registros
- [x] Importação de dados CSV
- [x] Resumos por estado
- [x] Tratamento de erros adequado
- [x] Status HTTP corretos (200, 201, 404)
- [x] **Integração SI-PNI / Datasus**
- [x] **Busca de cobertura por estado, município e vacina**
- [x] **Consulta de dados em períodos específicos**
- [x] **Resumos e estatísticas de cobertura**
- [x] **Cache de requisições**
- [x] **Interface React com filtros interativos**

## 🐛 Troubleshooting

### Frontend não conecta ao Backend
- Verifique se o backend está rodando em `http://localhost:8080`
- Verifique a configuração de CORS no Controller
- Abra o console do navegador (F12) para ver erros

### Erro ao carregar CSV
- Verifique o formato das datas: `dd/MM/yyyy`
- Verifique se o arquivo tem todas as colunas obrigatórias
- Verifique se não há linhas vazias no CSV

### Porta 8080 ou 3000 já em uso
```bash
# Encontre o processo usando a porta
lsof -i :8080
lsof -i :3000

# Mate o processo
kill -9 <PID>
```

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Axios Documentation](https://axios-http.com)
- [OpenCSV Documentation](https://opencsv.sourceforge.net)

## 📝 Notas

- O banco de dados H2 é em memória e será resetado ao reiniciar a aplicação
- Os dados CSV são automaticamente carregados ao iniciar o backend
- A aplicação suporta múltiplos filtros simultâneos no frontend

## 👨‍💼 Desenvolvido por

**Equipe Imunidata** - Projeto acadêmico de análise de cobertura vacinal

---

**Última atualização:** 15 de maio de 2026