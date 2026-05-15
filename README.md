# 🏥 Imunidata - Sistema de Análise de Cobertura Vacinal

## � Integrantes do Grupo

- **João Felipe**
- **Ryan Lucena**
- **Guilherme Mendonça**

---

## 🎯 Objetivo

O **Imunidata** é um sistema Full Stack desenvolvido para **analisar e gerenciar dados de cobertura vacinal** por região e faixa etária. O sistema resolve o problema de falta de visibilidade sobre o andamento das campanhas de vacinação, permitindo que secretarias de saúde e gestores públicos tomem decisões baseadas em dados reais.

**Problema:** Dificuldade em acompanhar coberturas vacinais por município, estado e tipo de vacina, levando a campanhas ineficientes.

**Solução:** Plataforma integrada que consolida dados de vacinação, permite filtros avançados e visualização de cobertura em tempo real, com integração com a API SI-PNI do Datasus.

---

## 📊 Visão Geral Técnica

Imunidata é uma aplicação **Full Stack** (Java Spring Boot + React) com:
- ✅ Arquitetura em camadas (Model, Repository, Service, Controller)
- ✅ Banco de dados H2 com console Web
- ✅ API REST completa com CRUD
- ✅ Integração com SI-PNI/Datasus
- ✅ Frontend React responsivo com filtros em tempo real
- ✅ Importação de dados CSV

---

## 🏗️ Documentação da API (Backend)

### 📋 Mapeamento de Entidades

#### 1. **RegistroVacinacao** (@Entity)

Representa um registro de vacinação realizada.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `id` | Long | ID único (chave primária, auto-incrementada) |
| `municipio` | String | Nome do município onde foi feita a vacinação |
| `estado` | String | Estado/UF (ex: SP, RJ, MG) |
| `vacina` | String | Tipo de vacina aplicada (BCG, Gripe, etc) |
| `dose` | String | Dose aplicada (1ª, 2ª, reforço) |
| `quantidadeAplicada` | Integer | Quantidade de doses aplicadas |
| `dataRegistro` | LocalDate | Data do registro da vacinação |
| `faixaEtaria` | String | Faixa etária da população (0-4, 5-9, 10-14, etc) |

**Exemplo JSON:**
```json
{
  "id": 1,
  "municipio": "São Paulo",
  "estado": "SP",
  "vacina": "BCG",
  "dose": "1ª",
  "quantidadeAplicada": 1500,
  "dataRegistro": "2024-05-15",
  "faixaEtaria": "0-4"
}
```

#### 2. **CoberturavacinadalDTO** (Data Transfer Object)

Representa dados de cobertura vacinal da API SI-PNI.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| `codigoMunicipio` | String | Código IBGE do município |
| `nomeMunicipio` | String | Nome do município |
| `estado` | String | Estado/UF |
| `vacina` | String | Tipo de vacina |
| `cobertura` | Double | Percentual de cobertura (0-100) |
| `quantidadeAplicada` | Integer | Quantidade de doses aplicadas |
| `populacaoAlvo` | Integer | População alvo para vacinação |
| `ano` | String | Ano do registro |
| `mes` | String | Mês do registro |

**Exemplo JSON:**
```json
{
  "codigoMunicipio": "3550308",
  "nomeMunicipio": "São Paulo",
  "estado": "SP",
  "vacina": "BCG",
  "cobertura": 98.5,
  "quantidadeAplicada": 15000,
  "populacaoAlvo": 15228,
  "ano": "2024",
  "mes": "05"
}
```

---

### 🔌 Endpoints (Tabela de Rotas)

#### **Registros de Vacinação**

| Método | Endpoint | Descrição | Request | Response |
|--------|----------|-----------|---------|----------|
| `GET` | `/api/registros` | Listar todos os registros | - | Array de RegistroVacinacao |
| `GET` | `/api/registros/{id}` | Obter registro por ID | - | RegistroVacinacao |
| `POST` | `/api/registros` | Criar novo registro | RegistroVacinacao | RegistroVacinacao (201) |
| `PUT` | `/api/registros/{id}` | Atualizar registro | RegistroVacinacao | RegistroVacinacao |
| `DELETE` | `/api/registros/{id}` | Deletar registro | - | 204 No Content |
| `GET` | `/api/registros/buscar/vacina?nome=BCG` | Buscar por vacina | - | Array de RegistroVacinacao |
| `GET` | `/api/registros/buscar/estado?nome=SP` | Buscar por estado | - | Array de RegistroVacinacao |
| `GET` | `/api/registros/buscar/municipio?nome=São Paulo` | Buscar por município | - | Array de RegistroVacinacao |
| `GET` | `/api/registros/buscar/faixa-etaria?faixa=0-4` | Buscar por faixa etária | - | Array de RegistroVacinacao |
| `POST` | `/api/registros/carregar-csv` | Carregar dados CSV | FormData (file) | Array de RegistroVacinacao (201) |
| `GET` | `/api/registros/resumos/estado` | Resumo por estado | - | Array de RegistroVacinacao |

#### **SI-PNI / Datasus**

| Método | Endpoint | Descrição | Response |
|--------|----------|-----------|----------|
| `GET` | `/api/sipni/cobertura/estado?estado=SP` | Cobertura por estado | Array de CoberturavacinadalDTO |
| `GET` | `/api/sipni/cobertura/municipio?municipio=São Paulo` | Cobertura por município | Array de CoberturavacinadalDTO |
| `GET` | `/api/sipni/cobertura/vacina?vacina=BCG` | Cobertura por vacina | Array de CoberturavacinadalDTO |
| `GET` | `/api/sipni/cobertura/periodo?ano=2024&mes=05&estado=SP` | Cobertura em período | Array de CoberturavacinadalDTO |
| `GET` | `/api/sipni/resumo/estados` | Resumo geral de cobertura | Array de CoberturavacinadalDTO |
| `GET` | `/api/sipni/estatisticas` | Estatísticas de cobertura | String com média, mín e máx |
| `GET` | `/api/sipni/health` | Health check | "SI-PNI Service está funcionando normalmente" |

---

### 📝 Exemplos de Requisições

#### Criar um novo registro de vacinação

**Request:**
```bash
POST http://localhost:8080/api/registros
Content-Type: application/json

{
  "municipio": "Rio de Janeiro",
  "estado": "RJ",
  "vacina": "Gripe",
  "dose": "1ª",
  "quantidadeAplicada": 2000,
  "dataRegistro": "2024-05-20",
  "faixaEtaria": "5-9"
}
```

**Response (201 Created):**
```json
{
  "id": 26,
  "municipio": "Rio de Janeiro",
  "estado": "RJ",
  "vacina": "Gripe",
  "dose": "1ª",
  "quantidadeAplicada": 2000,
  "dataRegistro": "2024-05-20",
  "faixaEtaria": "5-9"
}
```

#### Buscar cobertura por estado (SI-PNI)

**Request:**
```bash
GET http://localhost:8080/api/sipni/cobertura/estado?estado=SP
```

**Response (200 OK):**
```json
[
  {
    "codigoMunicipio": "3550308",
    "nomeMunicipio": "São Paulo",
    "estado": "SP",
    "vacina": "BCG",
    "cobertura": 98.5,
    "quantidadeAplicada": 15000,
    "populacaoAlvo": 15228,
    "ano": "2024",
    "mes": "05"
  },
  {
    "codigoMunicipio": "3550308",
    "nomeMunicipio": "São Paulo",
    "estado": "SP",
    "vacina": "Gripe",
    "cobertura": 95.8,
    "quantidadeAplicada": 14620,
    "populacaoAlvo": 15228,
    "ano": "2024",
    "mes": "05"
  }
]
```

---

## 🏛️ Diagrama de Arquitetura

### Fluxo de Dados

```
┌─────────────────────────────────────────────────────────────┐
│                     FRONTEND (React)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Dashboard   │  │  Formulário  │  │  SI-PNI      │      │
│  │  (Tabela)    │  │  (CRUD)      │  │  (Cobertura) │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         ↓                  ↓                  ↓              │
│  http://localhost:3000 - Axios HTTP Requests               │
└──────────────────────────────┬──────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│                   BACKEND (Spring Boot)                      │
│  http://localhost:8080/api                                   │
│                                                              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              CONTROLLER LAYER                        │   │
│  │  ┌──────────────────┐  ┌──────────────────────┐    │   │
│  │  │RegistroVacinacao│  │ SIPNIController      │    │   │
│  │  │ Controller       │  │ (SI-PNI/Datasus)     │    │   │
│  │  └────────┬─────────┘  └──────────┬───────────┘    │   │
│  └───────────┼───────────────────────┼────────────────┘   │
│              ↓                        ↓                     │
│  ┌──────────────────────────────────────────────────┐     │
│  │              SERVICE LAYER                       │     │
│  │  ┌────────────────────────────────────────────┐ │     │
│  │  │RegistroVacinacaoService                   │ │     │
│  │  │ - Lógica de CRUD                          │ │     │
│  │  │ - Carregamento CSV (OpenCSV)              │ │     │
│  │  │ - Filtros especializados                  │ │     │
│  │  └────────────────────────────────────────────┘ │     │
│  │  ┌────────────────────────────────────────────┐ │     │
│  │  │ SIPNIService                               │ │     │
│  │  │ - Integração com API Datasus              │ │     │
│  │  │ - Cache de requisições                    │ │     │
│  │  │ - Processamento de cobertura              │ │     │
│  │  └────────────────────────────────────────────┘ │     │
│  └────────────────┬──────────────────────┬──────────┘     │
│                   ↓                      ↓                 │
│  ┌──────────────────────────────────────────────────┐     │
│  │              REPOSITORY LAYER                    │     │
│  │  ┌────────────────────────────────────────────┐ │     │
│  │  │RegistroVacinacaoRepository                │ │     │
│  │  │ extends JpaRepository<RegistroVacinacao>  │ │     │
│  │  │ - findByVacina()                          │ │     │
│  │  │ - findByEstado()                          │ │     │
│  │  │ - Custom Queries                          │ │     │
│  │  └────────────────────────────────────────────┘ │     │
│  │  ┌────────────────────────────────────────────┐ │     │
│  │  │ VacinaRepository                          │ │     │
│  │  │ extends JpaRepository<Vacina>             │ │     │
│  │  │ - Queries especializadas                  │ │     │
│  │  └────────────────────────────────────────────┘ │     │
│  └────────────────┬──────────────────────┬──────────┘     │
│                   ↓                      ↓                 │
└───────────────────┼──────────────────────┼────────────────┘
                    ↓                      ↓
        ┌──────────────────────┐  ┌──────────────────────┐
        │   H2 Database        │  │   SI-PNI API         │
        │   (Em Memória)       │  │   (Dados Reais)      │
        │                      │  │                      │
        │ - REGISTRO_VACINACAO │  │ - Cobertura Vacinal  │
        │ - VACINA             │  │ - Por Estado         │
        │ - ... (Tabelas)      │  │ - Por Município      │
        └──────────────────────┘  └──────────────────────┘
```

### Componentes Principais

1. **Frontend React** - Interface com usuário
   - Dashboard com tabela de registros
   - Formulário de inserção
   - Importador CSV
   - Visualizador SI-PNI

2. **Controller** - Camada HTTP/REST
   - Recebe requisições do frontend
   - Valida entrada
   - Chama service

3. **Service** - Lógica de Negócio
   - CRUD operations
   - Leitura de CSV
   - Integração com SI-PNI
   - Cache e processamento

4. **Repository** - Acesso a Dados
   - Queries JPA
   - Métodos especializados
   - Transações com BD

5. **Database H2** - Persistência
   - Banco em memória
   - Resetado ao reiniciar
   - Acessível via H2-Console

---

## 🚀 Guia de Execução

### Pré-requisitos

Certifique-se de ter instalado:
- **Java 17+** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **Node.js 16+** e **npm** (`node -version` e `npm -version`)

### Executar o Projeto

#### **1. Backend (Terminal 1)**

```bash
# Navegue até a raiz do projeto
cd /caminho/para/Projeto-Imunidata

# Compile e rode
mvn clean install
mvn spring-boot:run
```

✅ Backend estará em: **http://localhost:8080/api**

Aguarde pela mensagem:
```
Started ImunidataApplication in X.XXX seconds
```

#### **2. Frontend (Terminal 2)**

```bash
# Navegue até frontend
cd /caminho/para/Projeto-Imunidata/frontend

# Instale dependências
npm install

# Rode a aplicação
npm start
```

✅ Frontend estará em: **http://localhost:3000**

### Acessar a Aplicação

Abra o navegador e acesse: **http://localhost:3000**

Você verá 4 abas:
- 📊 **Dashboard** - Tabela com registros
- ➕ **Novo Registro** - Formulário CRUD
- 📤 **Importar CSV** - Carregar dados
- 🔗 **SI-PNI / Datasus** - Cobertura vacinal

---

## 💾 Acessar o H2 Console

O H2 Console permite visualizar e consultar os dados do banco diretamente.

### URL
```
http://localhost:8080/api/h2-console
```

### Configurações de Conexão
- **JDBC URL:** `jdbc:h2:mem:imunidatadb`
- **User Name:** `sa`
- **Password:** (deixar vazio)

### Consultas Úteis

```sql
-- Listar todos os registros de vacinação
SELECT * FROM REGISTRO_VACINACAO;

-- Contar registros por vacina
SELECT VACINA, COUNT(*) as TOTAL 
FROM REGISTRO_VACINACAO 
GROUP BY VACINA;

-- Registros por estado
SELECT ESTADO, COUNT(*) as TOTAL 
FROM REGISTRO_VACINACAO 
GROUP BY ESTADO;

-- Cobertura média por estado
SELECT ESTADO, 
       VACINA, 
       COUNT(*) as REGISTROS,
       SUM(QUANTIDADE_APLICADA) as TOTAL_APLICADO
FROM REGISTRO_VACINACAO
GROUP BY ESTADO, VACINA
ORDER BY ESTADO;
```

---

## 📚 Estrutura de Pastas

```
Projeto-Imunidata/
├── src/main/java/com/imunidata/
│   ├── model/                    # Entidades JPA (@Entity)
│   │   ├── RegistroVacinacao.java
│   │   └── Vacina.java
│   ├── dto/                      # Data Transfer Objects
│   │   └── CoberturavacinadalDTO.java
│   ├── repository/               # Interfaces JpaRepository
│   │   ├── RegistroVacinacaoRepository.java
│   │   └── VacinaRepository.java
│   ├── service/                  # Lógica de Negócio
│   │   ├── RegistroVacinacaoService.java
│   │   └── SIPNIService.java
│   ├── controller/               # Endpoints REST
│   │   ├── RegistroVacinacaoController.java
│   │   └── SIPNIController.java
│   ├── config/                   # Configurações
│   │   ├── DataLoaderConfig.java
│   │   └── CacheConfig.java
│   └── ImunidataApplication.java # Classe Principal
├── src/main/resources/
│   ├── application.properties    # Configuração Spring
│   └── dados_vacinacao.csv       # Dados de exemplo
├── frontend/                     # Aplicação React
│   ├── src/
│   │   ├── components/           # Componentes React
│   │   │   ├── Dashboard.js
│   │   │   ├── FormularioInsercao.js
│   │   │   └── SIPNI.js
│   │   ├── services/
│   │   │   └── registroAPI.js    # Serviço Axios
│   │   ├── App.js
│   │   └── index.js
│   ├── public/
│   │   └── index.html
│   └── package.json
├── pom.xml                       # Dependências Maven
├── INSTALACAO.md                 # Guia de Instalação
├── SIPNI_GUIDE.md                # Documentação SI-PNI
└── README.md                     # Este arquivo
```

---

## 📦 Dependências Principais

### Backend
- **Spring Boot 3.1.5** - Framework web
- **Spring Data JPA** - ORM
- **H2 Database** - Banco de dados
- **OpenCSV 5.7** - Leitura de CSV
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciador de dependências

### Frontend
- **React 18.2.0** - UI Framework
- **Axios 1.4.0** - HTTP Client
- **React Router 6.14.0** - Roteamento
- **npm** - Gerenciador de pacotes

---

## ✅ Funcionalidades Implementadas

- [x] Arquitetura em camadas (Model, Repository, Service, Controller)
- [x] Entidade RegistroVacinacao com todos os atributos
- [x] Repository com métodos de busca especializados
- [x] Service com lógica de negócio
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Banco H2 com console Web
- [x] Carregamento automático de dados CSV
- [x] Controller REST com endpoints completos
- [x] Dashboard React com tabela e filtros
- [x] Formulário de inserção de registros
- [x] Importador de CSV
- [x] Integração SI-PNI / Datasus
- [x] Busca de cobertura vacinal
- [x] CORS habilitado
- [x] Tratamento de erros adequado
- [x] Status HTTP corretos (200, 201, 404, etc)

---

## 📝 Notas Importantes

- O banco de dados H2 é **em memória** e será **resetado ao reiniciar** a aplicação
- Os dados CSV são **automaticamente carregados** ao iniciar o backend
- O frontend se conecta ao backend via **http://localhost:8080/api**
- O projeto utiliza **CORS aberto** para desenvolvimento
- A API SI-PNI está **simulada com dados reais** para testes

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