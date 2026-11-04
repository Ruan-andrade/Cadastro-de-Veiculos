# 🚗 Sistema de Gestão e Cadastro de Veículos — API REST

API RESTful desenvolvida em **Java com Spring Boot** para o gerenciamento completo do ciclo de vida de veículos, validação estrita de fabricantes e geração de relatórios analíticos em tempo real (distribuição por décadas, marcas, estoque disponível e registros recentes).

---

## 🛠️ Tecnologias e Ferramentas Utilizadas

* **Linguagem:** Java 17+
* **Framework Principal:** Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Spring Validation)
* **Banco de Dados:**
    * **H2 Database** (Em memória, configurado por padrão para execução rápida e testes)
    * **PostgreSQL** (Driver incluído para perfil de produção)
* **Produtividade:** Project Lombok
* **Testes Automatizados:** JUnit 5 (Jupiter), Mockito e AssertJ
* **Gerenciador de Dependências:** Maven (com Maven Wrapper embutido)

---

## 🏗️ Arquitetura do Projeto

O projeto aplica o padrão de separação em camadas (**Layered Architecture**) com uso de DTOs para proteger o modelo de domínio e tratamento centralizado de exceções:

```text
src/main/java/com/testeapi/cadastrar_Veiculos
├── controller/
│   └── VeiculoController.java          # Endpoints REST e contratos HTTP
├── domain/
│   ├── Marca.java                      # Enum de validação e padronização de fabricantes
│   └── Veiculo.java                    # Entidade JPA mapeada para o banco de dados
├── dto/
│   ├── ErroPadraoDTO.java              # Estrutura padronizada para retornos de erro
│   ├── RelatorioEstatisticasDTO.java   # Agregador de métricas e relatórios gerenciais
│   ├── VeiculoPatchDTO.java            # Payload para atualização parcial (PATCH)
│   ├── VeiculoRequestDTO.java          # Payload de entrada com Bean Validation (POST/PUT)
│   └── VeiculoResponseDTO.java         # Objeto de transferência de saída
├── exception/
│   ├── GlobalExceptionHandler.java     # Interceptador global (@RestControllerAdvice)
│   └── ResourceNotFoundException.java  # Exceção de domínio para recursos inexistentes
├── repository/
│   └── VeiculoRepository.java          # Interface Spring Data JPA com Derived Queries
├── service/
│   ├── VeiculoService.java             # Contrato da camada de negócios
│   └── impl/
│       └── VeiculoServiceImpl.java     # Implementação das regras de negócio e cálculos
└── CadastrarVeiculosApplication.java   # Ponto de entrada da aplicação Spring Boot