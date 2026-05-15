# 🚀 Guia de Instalação e Execução - Imunidata

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

### 1. Java Development Kit (JDK) 17+
```bash
# Verificar se tem Java instalado
java -version

# Se não tiver, baixe em: https://www.oracle.com/java/technologies/downloads/
```

### 2. Maven 3.8+
```bash
# Verificar se tem Maven instalado
mvn -version

# Se não tiver, baixe em: https://maven.apache.org/download.cgi
```

### 3. Node.js 16+ e npm
```bash
# Verificar se tem Node instalado
node -version
npm -version

# Se não tiver, baixe em: https://nodejs.org/
```

### 4. Git (opcional, para clonar o repositório)
```bash
git --version
```

---

## 📂 Passo 1: Preparar o Projeto

### Clone o repositório (ou extraia o arquivo):
```bash
cd seu-diretorio-desejado
git clone https://github.com/Manojao00/Projeto-Imunidata.git
cd Projeto-Imunidata
```

### Ou se já tiver a pasta:
```bash
cd /caminho/para/Projeto-Imunidata
```

---

## 🔧 Passo 2: Configurar o Backend (Spring Boot)

### 2.1 Compilar o projeto Maven
```bash
# Na raiz do projeto
mvn clean install
```

**O que acontece:**
- Downloads de todas as dependências (pode levar alguns minutos)
- Compilação do código Java
- Criação do JAR da aplicação

**Se receber erro:**
- Verifique se Java 17+ está instalado: `java -version`
- Verifique se Maven está no PATH
- Tente limpar: `mvn clean` antes

### 2.2 Iniciar o Backend
```bash
# Ainda na raiz do projeto
mvn spring-boot:run
```

**Você verá algo como:**
```
[INFO] --- spring-boot-maven-plugin:3.1.5:run (default-cli) @ imunidata-api ---
[INFO] Attaching agents: []
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '_____|_|_|_|_|_|_|__,_|\___/  / / / /
 ==============================^_^==============================

2024-05-15 10:30:45.123  INFO 12345 --- [  restartedMain] c.i.ImunidataApplication             : Started ImunidataApplication in 3.456 seconds
```

✅ **Backend está rodando em:** `http://localhost:8080/api`

### Portas do Backend:
- **API REST:** http://localhost:8080/api
- **H2-Console:** http://localhost:8080/api/h2-console (usuário: sa, senha: vazia)

---

## 💻 Passo 3: Configurar o Frontend (React)

### 3.1 Abrir um novo terminal/prompt (NÃO FECHE O ANTERIOR)

Você precisa de dois terminais rodando simultaneamente!

### 3.2 Navegar até a pasta frontend
```bash
cd /caminho/para/Projeto-Imunidata/frontend
```

### 3.3 Instalar dependências npm
```bash
npm install
```

**O que acontece:**
- Instala React, Axios e outras dependências
- Cria pasta `node_modules`
- Pode levar alguns minutos (dependendo da internet)

**Se receber erro:**
- Verifique Node.js: `node -version`
- Tente limpar cache: `npm cache clean --force`
- Depois reinstale: `npm install`

### 3.4 Iniciar o Frontend
```bash
npm start
```

**Você verá algo como:**
```
Compiled successfully!

You can now view imunidata-frontend in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://192.168.1.100:3000
```

✅ **Frontend está rodando em:** `http://localhost:3000`

---

## 🌐 Passo 4: Acessar a Aplicação

### Abra seu navegador e acesse:

**Frontend:** http://localhost:3000

Você verá o Imunidata com 4 abas principais:
1. **📊 Dashboard** - Listagem de registros com filtros
2. **➕ Novo Registro** - Formulário para cadastrar vacinações
3. **📤 Importar CSV** - Carregar dados em massa
4. **🔗 SI-PNI / Datasus** - Dados de cobertura vacinal

---

## 🧪 Passo 5: Testar a Aplicação

### 5.1 Testar o Dashboard
1. Vá para a aba **"📊 Dashboard"**
2. Você verá uma tabela com dados pré-carregados do CSV
3. Experimente os filtros de vacina e estado

### 5.2 Testar Novo Registro
1. Vá para **"➕ Novo Registro"**
2. Preencha o formulário com dados:
   - Município: São Paulo
   - Estado: SP
   - Vacina: BCG
   - Dose: 1ª
   - Quantidade: 100
   - Data: escolha uma data
3. Clique em "Enviar Registro"
4. Volte ao Dashboard e verá o novo registro

### 5.3 Testar Importação CSV
1. Vá para **"📤 Importar CSV"**
2. Carregue o arquivo: `src/main/resources/dados_vacinacao.csv`
3. Verifique no Dashboard os novos registros

### 5.4 Testar SI-PNI
1. Vá para **"🔗 SI-PNI / Datasus"**
2. Selecione um estado (ex: SP)
3. Clique em "Buscar"
4. Veja os dados de cobertura vacinal

---

## 🔍 Teste dos Endpoints com cURL

Se preferir testar direto a API:

### Listar todos os registros
```bash
curl http://localhost:8080/api/registros
```

### Buscar por vacina
```bash
curl "http://localhost:8080/api/registros/buscar/vacina?nome=BCG"
```

### Buscar por estado
```bash
curl "http://localhost:8080/api/registros/buscar/estado?nome=SP"
```

### Criar novo registro
```bash
curl -X POST http://localhost:8080/api/registros \
  -H "Content-Type: application/json" \
  -d '{
    "municipio": "São Paulo",
    "estado": "SP",
    "vacina": "BCG",
    "dose": "1ª",
    "quantidadeAplicada": 100,
    "dataRegistro": "2024-05-15",
    "faixaEtaria": "0-4"
  }'
```

### Testar SI-PNI
```bash
curl "http://localhost:8080/api/sipni/cobertura/estado?estado=SP"
```

---

## 🛑 Parar a Aplicação

### Backend:
- Pressione **Ctrl + C** no terminal do Maven

### Frontend:
- Pressione **Ctrl + C** no terminal do Node

---

## 🐛 Troubleshooting

### Problema: "Porta 8080 já está em uso"
```bash
# Encontre o processo usando a porta 8080
netstat -ano | findstr :8080  (Windows)
lsof -i :8080                 (Mac/Linux)

# Mate o processo
taskkill /PID <numero> /F     (Windows)
kill -9 <numero>              (Mac/Linux)

# Ou altere a porta em application.properties:
# server.port=8081
```

### Problema: "Porta 3000 já está em uso"
```bash
# Mesmo comando acima, mas para porta 3000
netstat -ano | findstr :3000
lsof -i :3000
```

### Problema: "npm: command not found"
```bash
# Node.js não está instalado corretamente
# Baixe em: https://nodejs.org/
# Ou instale via gerenciador de pacotes:
# Windows: choco install nodejs
# Mac: brew install node
# Linux: sudo apt install nodejs npm
```

### Problema: "Maven compilation failed"
```bash
# Limpe o cache:
mvn clean
# Depois tente novamente:
mvn install
```

### Problema: "Frontend mostra erro de conexão"
```bash
# Verifique se o backend está rodando em http://localhost:8080
# Verifique os logs do backend
# Verifique a aba Network do DevTools (F12) do navegador
```

---

## 📱 Acessar de Outro Computador/Dispositivo

### Se estiver na mesma rede:

1. **Obtenha o IP da sua máquina:**
```bash
ipconfig       (Windows)
ifconfig       (Mac/Linux)
```

2. **Acesse do outro dispositivo:**
```
http://<seu-ip>:3000
```

Exemplo: `http://192.168.1.100:3000`

---

## 💾 Banco de Dados H2

### Acessar H2-Console:
http://localhost:8080/api/h2-console

**Configurações:**
- **JDBC URL:** `jdbc:h2:mem:imunidatadb`
- **User Name:** `sa`
- **Password:** (deixar vazio)

### Consultas úteis:
```sql
-- Listar todos os registros
SELECT * FROM REGISTRO_VACINACAO;

-- Contar registros por vacina
SELECT VACINA, COUNT(*) FROM REGISTRO_VACINACAO GROUP BY VACINA;

-- Registros por estado
SELECT ESTADO, COUNT(*) FROM REGISTRO_VACINACAO GROUP BY ESTADO;
```

---

## 📊 Estrutura de Pastas do Projeto

```
Projeto-Imunidata/
├── src/
│   └── main/
│       ├── java/com/imunidata/
│       │   ├── model/           # Entidades (RegistroVacinacao, etc)
│       │   ├── repository/       # Interfaces JPA
│       │   ├── service/          # Lógica de negócio
│       │   ├── controller/       # Endpoints REST
│       │   ├── config/           # Configurações
│       │   └── dto/              # Data Transfer Objects
│       └── resources/
│           ├── application.properties
│           └── dados_vacinacao.csv
├── frontend/
│   ├── src/
│   │   ├── components/          # Componentes React
│   │   ├── services/            # Serviços API
│   │   ├── App.js               # App principal
│   │   └── index.js             # Ponto de entrada
│   ├── public/
│   │   └── index.html
│   └── package.json
├── pom.xml                      # Dependências Maven
└── README.md                    # Documentação
```

---

## ✅ Checklist de Execução

- [ ] Instalei Java 17+
- [ ] Instalei Maven 3.8+
- [ ] Instalei Node.js 16+ e npm
- [ ] Clonei/extraí o projeto
- [ ] Executei `mvn clean install` no backend
- [ ] Backend está rodando em http://localhost:8080/api
- [ ] Executei `npm install` no frontend
- [ ] Frontend está rodando em http://localhost:3000
- [ ] Testei o Dashboard com dados pré-carregados
- [ ] Testei criar um novo registro
- [ ] Testei os filtros
- [ ] Testei a aba SI-PNI

---

## 🆘 Precisa de Ajuda?

Se encontrar algum problema:
1. Verifique os logs no terminal
2. Abra DevTools no navegador (F12)
3. Consulte a seção **Troubleshooting** acima
4. Verifique o arquivo README.md

---

**Última atualização:** 15 de maio de 2026
