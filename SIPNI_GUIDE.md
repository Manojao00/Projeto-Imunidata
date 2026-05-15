# 🔗 Guia de Integração SI-PNI / Datasus

## O que é SI-PNI?

O **Sistema de Informações do Programa Nacional de Imunizações (SI-PNI)** é um sistema de informação do Ministério da Saúde que coleta e disponibiliza dados sobre cobertura vacinal em todo o Brasil.

## Integração no Imunidata

O Imunidata agora integra dados de cobertura vacinal através da API SI-PNI do Datasus. Isso permite acessar informações reais sobre a cobertura de vacinação por estado, município e tipo de vacina.

## 📋 Endpoints da API SI-PNI

### 1. Cobertura por Estado
```
GET /api/sipni/cobertura/estado?estado=SP
```
**Parâmetros:**
- `estado` (obrigatório): Código do estado (ex: SP, RJ, MG, DF, BA)

**Exemplo de resposta:**
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
  }
]
```

### 2. Cobertura por Município
```
GET /api/sipni/cobertura/municipio?municipio=São Paulo
```
**Parâmetros:**
- `municipio` (obrigatório): Nome do município

### 3. Cobertura por Vacina
```
GET /api/sipni/cobertura/vacina?vacina=BCG
```
**Parâmetros:**
- `vacina` (obrigatório): Nome da vacina (ex: BCG, Gripe, Poliomielite, Sarampo, Difteria, HPV)

### 4. Cobertura em Período
```
GET /api/sipni/cobertura/periodo?ano=2024&mes=05&estado=SP
```
**Parâmetros:**
- `ano` (obrigatório): Ano (ex: 2024)
- `mes` (obrigatório): Mês (01-12)
- `estado` (obrigatório): Código do estado

### 5. Resumo por Estado
```
GET /api/sipni/resumo/estados
```
Retorna dados consolidados de cobertura para todos os estados.

### 6. Estatísticas Gerais
```
GET /api/sipni/estatisticas
```
Retorna resumo das coberturas médias, mínimas e máximas.

### 7. Health Check
```
GET /api/sipni/health
```
Verifica se o serviço SI-PNI está funcionando.

## 🖥️ Interface Frontend

### Acessando a Aba SI-PNI

1. Inicie o frontend: `npm start`
2. Clique na aba **"🔗 SI-PNI / Datasus"**
3. Você verá:
   - Estatísticas gerais de cobertura
   - Filtros para buscar dados
   - Tabela com resultados
   - Barras de progresso coloridas

### Filtros Disponíveis

#### Por Estado
Selecione um estado da lista e clique em "Buscar":
- SP, RJ, MG, DF, BA

#### Por Município
Digite o nome do município e busque:
- São Paulo
- Rio de Janeiro
- Belo Horizonte
- Brasília

#### Por Vacina
Escolha uma vacina:
- BCG
- Gripe
- Poliomielite
- Sarampo
- Difteria
- HPV

#### Por Período
Selecione ano, mês e estado para dados históricos.

## 🧪 Testando com cURL

### Teste 1: Cobertura em São Paulo
```bash
curl "http://localhost:8080/api/sipni/cobertura/estado?estado=SP"
```

### Teste 2: Dados da vacina BCG
```bash
curl "http://localhost:8080/api/sipni/cobertura/vacina?vacina=BCG"
```

### Teste 3: Dados de um município
```bash
curl "http://localhost:8080/api/sipni/cobertura/municipio?municipio=São Paulo"
```

### Teste 4: Cobertura em um período
```bash
curl "http://localhost:8080/api/sipni/cobertura/periodo?ano=2024&mes=05&estado=SP"
```

### Teste 5: Estatísticas
```bash
curl "http://localhost:8080/api/sipni/estatisticas"
```

## 📊 Interpretando os Dados

### Cobertura (%)
- **🟢 Verde (≥95%)**: Excelente cobertura
- **🟡 Amarelo (85-95%)**: Boa cobertura, pode melhorar
- **🔴 Vermelho (<85%)**: Cobertura baixa, ação necessária

### Campos do DTO

| Campo | Descrição |
|-------|-----------|
| `codigoMunicipio` | Código IBGE do município |
| `nomeMunicipio` | Nome do município |
| `estado` | Sigla do estado |
| `vacina` | Tipo de vacina |
| `cobertura` | Percentual de cobertura (0-100) |
| `quantidadeAplicada` | Quantidade de doses aplicadas |
| `populacaoAlvo` | População alvo para vacinação |
| `ano` | Ano do registro |
| `mes` | Mês do registro |

## 🔧 Configuração e Cache

### Cache Automático
O serviço SI-PNI implementa cache para evitar requisições redundantes:

```java
@Cacheable("coberturaPorEstado")
public List<CoberturavacinadalDTO> buscarCoberturaPorEstado(String estado)
```

Isto melhora a performance significativamente.

### Configuração em application.properties
```properties
# Cache habilitado automaticamente via CacheConfig
spring.cache.type=simple
```

## 🚀 Integração com Dados Reais

### Migração para API Real do Datasus

Para integrar com a API real do Datasus, você precisará:

1. **Ajustar a URL base:**
```java
private static final String SIPNI_API_URL = "http://sipni.datasus.gov.br/api/";
```

2. **Implementar parser JSON real** em vez dos dados simulados

3. **Adicionar tratamento de timeout** para requisições externas

4. **Implementar retry logic** para chamadas que falhem

### Exemplo de Implementação Real
```java
@GetMapping("/cobertura/estado")
public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorEstado(
        @RequestParam String estado) {
    try {
        String url = SIPNI_API_URL + "/cobertura?estado=" + estado;
        ResponseEntity<List<CoberturavacinadalDTO>> response = 
            restTemplate.getForEntity(url, new ParameterizedTypeReference<>(){});
        return response;
    } catch (RestClientException e) {
        log.error("Erro ao buscar dados da API SI-PNI", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
```

## 📝 Exemplo de Fluxo Completo

### 1. Frontend faz requisição
```javascript
const response = await axios.get('/api/sipni/cobertura/estado?estado=SP');
```

### 2. Backend (Controller) recebe
```java
@GetMapping("/cobertura/estado")
public ResponseEntity<List<CoberturavacinadalDTO>> buscarCoberturaPorEstado(@RequestParam String estado)
```

### 3. Service processa
```java
List<CoberturavacinadalDTO> cobertura = sipniService.buscarCoberturaPorEstado("SP");
```

### 4. Service retorna dados
```json
[
  { "nomeMunicipio": "São Paulo", "vacina": "BCG", "cobertura": 98.5, ... }
]
```

### 5. Frontend exibe
- Tabela com dados
- Barras de progresso
- Estatísticas

## 🐛 Troubleshooting

### Erro: "No data found"
- Verifique se o estado/município existe
- Tente um estado diferente (SP, RJ, MG)

### Erro: "API Timeout"
- A API pode estar lenta
- Verifique sua conexão de internet
- Tente novamente em alguns segundos

### Dados não atualizam
- O cache pode estar retornando dados antigos
- Reinicie o backend para limpar o cache
- Ou aguarde o tempo de expiração do cache

## 📚 Referências

- [SI-PNI Datasus](http://sipni.datasus.gov.br/)
- [Documentação PNI](https://www.gov.br/saude/pt-br/acesso-a-informacao/agir/gerenciamento-de-riscos/riscos-organizacionais/pni)

---

**Última atualização:** 15 de maio de 2026
