# 🚗 Sistema de Gestão e Cadastro de Veículos — API REST

API RESTful corporativa desenvolvida em **Java com Spring Boot** para atendimento integral ao Desafio Técnico (Atividade nº 5: *Cadastro de veículos*). 

A aplicação gerencia todo o ciclo de vida de veículos, com validação estrita de marcas, busca dinâmica multicritério, atualização total e parcial, tratamento padronizado de exceções e relatórios analíticos gerenciais em tempo real.

---

## 📋 Sumário
1. [Tecnologias Utilizadas](#-tecnologias-utilizadas)
2. [Arquitetura e Padrões de Projeto](#-arquitetura-e-padrões-de-projeto)
3. [Como Executar o Projeto](#-como-executar-o-projeto)
4. [Execução dos Testes Automatizados](#-execução-dos-testes-automatizados)
5. [Acesso ao Banco de Dados (Console H2)](#-acesso-ao-banco-de-dados-console-h2)
6. [Guia Completo da API (Endpoints e Exemplos)](#-guia-completo-da-api-endpoints-e-exemplos)
7. [Regras de Negócio e Validações](#-regras-de-negócio-e-validações)

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework:** Spring Boot (Spring Web MVC, Spring Data JPA, Spring Validation)
* **Banco de Dados Principal (Desenvolvimento/Testes):** H2 Database (em memória, com console web)
* **Banco de Dados Adicional (Produção):** PostgreSQL (driver configurado)
* **Validação de Dados:** Hibernate Validator / Jakarta Bean Validation
* **Produtividade:** Project Lombok
* **Testes Automatizados:** JUnit 5 (Jupiter), Mockito, MockMvc, AssertJ
* **Padronização de Código:** Spotless Plugin (Google Java Format AOSP)
* **Gerenciador de Dependências:** Apache Maven (com Maven Wrapper embutido)

---

## 🏗️ Arquitetura e Padrões de Projeto

O projeto segue os princípios da **Arquitetura em Camadas (Layered Architecture)**, separando rigorosamente as responsabilidades:

```text
src/main/java/com/testeapi/cadastrar_Veiculos
├── controller/
│   └── VeiculoController.java          # Camada Web: Contratos HTTP, códigos de status e endpoints REST
├── domain/
│   ├── Marca.java                      # Enum com validação de marcas e padronização visual
│   └── Veiculo.java                    # Entidade JPA mapeada para a tabela tb_veiculos
├── dto/
│   ├── ErroPadraoDTO.java              # Payload padronizado para respostas de erro (RFC 7807 inspired)
│   ├── RelatorioEstatisticasDTO.java   # DTO consolidado com métricas analíticas
│   ├── VeiculoPatchDTO.java            # DTO para atualização parcial (PATCH)
│   ├── VeiculoRequestDTO.java          # DTO de entrada para cadastro e atualização completa (POST/PUT)
│   └── VeiculoResponseDTO.java         # DTO de saída protegendo o modelo de domínio
├── exception/
│   ├── GlobalExceptionHandler.java     # Interceptador global de exceções (@RestControllerAdvice)
│   └── ResourceNotFoundException.java  # Exceção de domínio para recursos não encontrados (HTTP 404)
├── repository/
│   └── VeiculoRepository.java          # Acesso a dados Spring Data JPA com JpaSpecificationExecutor
├── service/
│   ├── VeiculoService.java             # Interface da camada de negócios
│   └── impl/
│       └── VeiculoServiceImpl.java     # Implementação das regras de negócio, filtros dinâmicos e relatórios
└── CadastrarVeiculosApplication.java   # Classe principal de inicialização do Spring Boot
```

### Design Patterns Aplicados:
* **Data Transfer Object (DTO):** Isolamento entre a camada de persistência e a camada web, prevenindo *mass assignment* e vazamento de dados internos.
* **Specification Pattern (JPA Criteria):** Permite construir consultas dinâmicas e flexíveis combinando múltiplos filtros (`marca`, `ano`, `cor`) de forma extensível e limpa.
* **Builder Pattern:** Criação de objetos legível e imutável através do Lombok `@Builder`.
* **Strategy / Enum Method Pattern:** Centralização da validação e conversão de fabricantes no próprio enum `Marca`.
* **Global Exception Handler:** Centralização do tratamento de erros através de `@RestControllerAdvice`, garantindo contratos de erro previsíveis.

---

## 🚀 Como Executar o Projeto

### Pré-requisitos:
* Java JDK 17 ou superior instalado e configurado no `PATH`.
* Git (para clonar o repositório).

### Passos:
1. Abra o terminal na pasta raiz do backend:
   ```bash
   cd Cadastro-de-Veiculos/backend/cadastrar_Veiculos
   ```
2. Inicie a aplicação com o Maven Wrapper:
   * **No Windows (PowerShell / CMD):**
     ```bash
     .\mvnw.cmd spring-boot:run
     ```
   * **No Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
3. A API estará disponível em: `http://localhost:8080`

---

## 🧪 Execução dos Testes Automatizados

O projeto conta com uma suíte abrangente de testes automatizados unitários e de integração de camada Web:
* **Testes de Serviço (`VeiculoServiceTest`):** Valida cenários de criação, busca, filtros, atualização completa, atualização parcial, exclusão, relatórios analíticos e lançamentos de exceções.
* **Testes de Controller (`VeiculoControllerTest`):** Valida os contratos HTTP via `MockMvc` para todos os verbos (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`), verificando status codes (`200`, `201`, `204`, `400`, `404`) e validações de payload.

Para rodar todos os testes:
```bash
.\mvnw.cmd test
```

---

## 🗄️ Acesso ao Banco de Dados (Console H2)

O banco de dados H2 roda em memória, facilitando testes rápidos sem necessidade de instalar dependências externas:
* **URL:** `http://localhost:8080/h2-console`
* **JDBC URL:** `jdbc:h2:mem:veiculosdb`
* **User Name:** `root`
* **Password:** `root`

---

## 📡 Guia Completo da API (Endpoints e Exemplos)

### 1. Cadastrar Veículo
* **Método:** `POST`
* **Rota:** `/veiculos`
* **Corpo da Requisição (JSON):**
  ```json
  {
    "veiculo": "Civic Touring 1.5 Turbo",
    "marca": "Honda",
    "ano": 2021,
    "cor": "Preto",
    "descricao": "Sedan premium único dono",
    "vendido": false
  }
  ```
* **Resposta de Sucesso:** `HTTP 201 Created`
  ```json
  {
    "id": 1,
    "veiculo": "Civic Touring 1.5 Turbo",
    "marca": "Honda",
    "ano": 2021,
    "cor": "Preto",
    "descricao": "Sedan premium único dono",
    "vendido": false,
    "created": "2026-11-04T12:00:00",
    "updated": "2026-11-04T12:00:00"
  }
  ```

---

### 2. Listar e Filtrar Veículos
* **Método:** `GET`
* **Rotas Suportadas:**
  * `GET /veiculos` (Retorna todos os veículos)
  * `GET /veiculos?marca=Honda`
  * `GET /veiculos?ano=2021`
  * `GET /veiculos?cor=Preto`
  * `GET /veiculos?marca=Honda&ano=2021&cor=Preto` (Combinação de múltiplos filtros)
* **Resposta de Sucesso:** `HTTP 200 OK` (Array de Veículos)

---

### 3. Buscar Veículo por ID
* **Método:** `GET`
* **Rota:** `/veiculos/{id}`
* **Resposta de Sucesso:** `HTTP 200 OK` com os detalhes do veículo.
* **Resposta se não existir:** `HTTP 404 Not Found`
  ```json
  {
    "timestamp": "2026-11-04T12:00:00",
    "status": 404,
    "erro": "Não Encontrado",
    "mensagem": "Veículo não encontrado com o ID: 99",
    "path": "/veiculos/99"
  }
  ```

---

### 4. Atualização Completa de Veículo
* **Método:** `PUT`
* **Rota:** `/veiculos/{id}`
* **Corpo da Requisição (JSON):** Substitui todos os dados do veículo.
  ```json
  {
    "veiculo": "Civic Touring Atualizado",
    "marca": "Honda",
    "ano": 2022,
    "cor": "Cinza Chumbo",
    "descricao": "Veículo com garantia estendida",
    "vendido": true
  }
  ```
* **Resposta de Sucesso:** `HTTP 200 OK`
* **Resposta se ID inexistente:** `HTTP 404 Not Found`

---

### 5. Atualização Parcial de Veículo
* **Método:** `PATCH`
* **Rota:** `/veiculos/{id}`
* **Corpo da Requisição (JSON):** Envia apenas os campos que deseja alterar (ex: marcar como vendido e mudar a cor).
  ```json
  {
    "vendido": true,
    "cor": "Branco Perolizado"
  }
  ```
* **Resposta de Sucesso:** `HTTP 200 OK`
* **Resposta se ID inexistente:** `HTTP 404 Not Found`

---

### 6. Excluir Veículo
* **Método:** `DELETE`
* **Rota:** `/veiculos/{id}`
* **Resposta de Sucesso:** `HTTP 204 No Content`
* **Resposta se ID inexistente:** `HTTP 404 Not Found`

---

### 7. Relatório Gerencial e Estatísticas
* **Método:** `GET`
* **Rota:** `/veiculos/relatorios`
* **Descrição:** Retorna os dados analíticos exigidos no desafio técnico:
  1. Total de veículos não vendidos na base.
  2. Distribuição de veículos por década de fabricação.
  3. Distribuição de veículos por fabricante.
  4. Total de veículos registrados durante a última semana.
* **Resposta de Sucesso:** `HTTP 200 OK`
  ```json
  {
    "totalNaoVendidos": 12,
    "distribuicaoPorDecada": {
      "Década 1990": 3,
      "Década 2000": 8,
      "Década 2020": 15
    },
    "distribuicaoPorFabricante": {
      "Volkswagen": 10,
      "Ford": 6,
      "Honda": 8,
      "Toyota": 5
    },
    "totalUltimaSemana": 4
  }
  ```

---

### 8. Veículos Registrados na Última Semana
* **Método:** `GET`
* **Rota:** `/veiculos/semana`
* **Descrição:** Exibe a lista completa com os detalhes dos veículos cadastrados nos últimos 7 dias.
* **Resposta de Sucesso:** `HTTP 200 OK` (Array de Veículos)

---

## 🛡️ Regras de Negócio e Validações

1. **Consistência de Fabricantes:**
   * Apenas fabricantes válidos são aceitos: `Chevrolet`, `Volkswagen`, `Fiat`, `Ford`, `Toyota`, `Honda`, `Hyundai`, `Renault`, `Nissan`, `Jeep`.
   * Tentativas de cadastrar grafias erradas (ex: `"Volskvagen"`, `"Forde"`, `"Xevrolé"`) são rejeitadas com `HTTP 400 Bad Request`.
2. **Datas de Auditoria Automáticas:**
   * `created` é preenchido automaticamente na criação e protegido contra alterações manuais (`updatable = false`).
   * `updated` é recalculado automaticamente a cada alteração via `@PreUpdate`.
3. **Padrão de Venda Padrão:**
   * Se o campo `vendido` for omitido na criação, assume o valor padrão `false`.
