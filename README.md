# SisAgi - Simulador de Sistema Bancário 🏦

Um sistema de back-end robusto para simulação bancária, permitindo o gerenciamento completo de clientes, gerentes, contas diversificadas (incluindo contas globais com conversão de moedas) e transações financeiras. Este projeto foi desenvolvido como uma aplicação monolítica bem estruturada utilizando as melhores práticas do ecossistema Spring.

---

## 🎯 Sobre o Projeto

O SisAgi foi criado como um projeto de estudo para aplicar e aprofundar conhecimentos em desenvolvimento back-end com Java e Spring Boot. O objetivo é simular as funcionalidades essenciais de um sistema bancário moderno, incluindo cadastro de usuários, autenticação, gerenciamento de múltiplos tipos de contas, operações financeiras e integração com APIs externas de câmbio e CEP, tudo exposto através de uma API RESTful segura.

---

## ✨ Funcionalidades

### Autenticação e Autorização
-   ✅ **Sistema de Autenticação:** Registro e login com diferenciação de papéis (`CLIENTE`, `GERENTE`) usando Spring Security
-   ✅ **Controle de Acesso:** Autorização baseada em roles para endpoints específicos

### Gestão de Usuários
-   ✅ **CRUD de Clientes:** Operações completas de Criação, Leitura, Atualização e Deleção para clientes
-   ✅ **CRUD de Gerentes:** Operações completas de Criação, Leitura, Atualização e Deleção para gerentes
-   ✅ **Validação de CPF:** Validação customizada de CPF com constraint annotation
-   ✅ **Gestão de Telefones:** Suporte a diferentes tipos de telefone (fixo, celular, comercial)

### Gestão de Endereços
-   ✅ **CRUD de Endereços:** Operações completas para gerenciamento de endereços de clientes
-   ✅ **Integração com ViaCEP:** Busca automática de endereço por CEP
-   ✅ **Múltiplos Endereços:** Suporte a diferentes tipos de endereço por cliente (residencial, comercial, etc.)

### Gestão de Contas Bancárias
-   ✅ **Conta Corrente:** Conta tradicional com limite de cheque especial configurável
-   ✅ **Conta Poupança:** Conta com rendimento e data de aniversário
-   ✅ **Conta Jovem:** Conta especial vinculada a um responsável, sem limite
-   ✅ **Conta Global:** Conta internacional com saldo em dólares e conversão automática para reais
-   ✅ **Cotação de Moedas:** Integração com ExchangeRate-API para conversão USD/BRL em tempo real
-   ✅ **Múltiplos Titulares:** Suporte a contas com mais de um titular

### Transações Financeiras
-   ✅ **Sistema de Transações:** Estrutura completa para registro de operações financeiras
-   ✅ **Tipos de Transação:** Depósito, Saque, Transferência (enviada/recebida)
-   ✅ **NSU Único:** Geração automática de número sequencial único alfanumérico
-   ✅ **Rastreabilidade:** Registro de gerente executor, data/hora e motivo da movimentação
-   ✅ **DTOs Padronizados:** Request e Response para todas as operações de transação

---

## 🏗️ Arquitetura

### Estrutura de Pacotes
```
com.agibank.sisagi/
├── config/              # Configurações (RestTemplate, Security, etc.)
├── controller/          # Controladores REST (API endpoints)
├── dto/                 # Data Transfer Objects (Request/Response)
├── model/               # Entidades JPA e Enums
│   └── enums/          # Enumerações (TipoTelefone, TipoTransacao, UserRole)
├── repository/          # Repositórios Spring Data JPA
├── service/             # Lógica de negócio
├── util/                # Utilitários
└── validator/           # Validadores customizados
```

### Modelo de Dados

#### Hierarquia de Contas (Herança Single Table)
```
Conta (Abstrata)
├── ContaCorrente (limite de cheque especial)
├── ContaPoupanca (rendimento e data aniversário)
├── ContaJovem (vinculada a responsável)
└── ContaGlobal (saldo em dólares + conversão)
```

#### Hierarquia de Usuários
```
Usuarios (Abstrata)
├── Cliente (CPF, telefone, endereços, gerente associado)
└── Gerente (matrícula única)
```

#### Relacionamentos Principais
- **Cliente ↔ Gerente:** ManyToOne (cada cliente tem um gerente)
- **Cliente ↔ Endereco:** OneToMany (cliente pode ter múltiplos endereços)
- **Conta ↔ Cliente:** ManyToMany (conta pode ter múltiplos titulares)
- **Transacao ↔ Conta:** ManyToOne (transação vinculada a uma conta)
- **Transacao ↔ Gerente:** ManyToOne (transação executada por um gerente)

---

## 🛠️ Tecnologias Utilizadas

### Core
-   **Linguagem:** Java 21
-   **Framework:** Spring Boot 3.5.5
-   **Build Tool:** Gradle 8.x

### Spring Modules
-   **Spring Data JPA:** Persistência e ORM com Hibernate
-   **Spring Web:** API RESTful
-   **Spring Security:** Autenticação e autorização
-   **Spring Validation:** Validação de dados
-   **Spring Mail:** Suporte para envio de e-mails
-   **Spring DevTools:** Ferramentas de desenvolvimento

### Banco de Dados
-   **MySQL 8.x:** Banco de dados relacional
-   **Hospedagem:** Aiven Cloud

### Utilitários
-   **Lombok:** Redução de boilerplate code
-   **RestTemplate:** Cliente HTTP para APIs externas

### APIs Externas Integradas
-   **ViaCEP API:** Consulta de endereços por CEP
-   **ExchangeRate-API:** Cotação de moedas em tempo real (USD/BRL)

### Ambiente de Desenvolvimento
-   **IDE:** IntelliJ IDEA
-   **Database Client:** DBeaver
-   **Docker:** Opcional para banco de dados local

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos

-   **JDK 21** ou superior
-   **IntelliJ IDEA** ou outra IDE de sua preferência
-   **Conta no Aiven** com um serviço MySQL ativo (ou MySQL local)
-   **Gradle** (incluído via Gradle Wrapper)

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/SisAgi/SISAGI.git
    cd SISAGI
    ```

2.  **Configure a Conexão com o Banco Aiven:**
    - Baixe o certificado `ca.pem` do seu serviço MySQL no Aiven
    - Coloque o certificado na pasta `src/main/resources/certs/`

3.  **Configure o `application.yml`:**
    - Abra o arquivo `src/main/resources/application.yml`
    - Preencha as configurações de banco de dados:
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://<host>:<port>/<database>?sslMode=REQUIRED
        username: <seu-usuario>
        password: <sua-senha>
    ```
    - Configure a chave da API de câmbio (opcional, já tem valor padrão):
    ```yaml
    exchangerate:
      api:
        key: 6b2fcaba86114260aa3dd360e48d6a49
        url: https://v6.exchangerate-api.com/v6
    ```

4.  **Execute a Aplicação:**
    - **Usando IntelliJ:** Execute a classe principal `SisagiApplication.java`
    - **Usando Gradle:**
      ```bash
      ./gradlew bootRun
      ```
    - **Ou compile e execute o JAR:**
      ```bash
      ./gradlew build
      java -jar build/libs/sisagi-0.0.1-SNAPSHOT.jar
      ```
    - O servidor iniciará na porta `8080`

5.  **Teste a API:**
    ```bash
    curl http://localhost:8080/api/v1/clientes
    ```

---

## 📖 Endpoints da API

A API está organizada por recursos e versões. Todos os endpoints, exceto `/auth/**`, requerem autenticação.

### Base URL
```
http://localhost:8080
```

### Autenticação e Registro
| Verbo HTTP | Endpoint URL        | Descrição                  | Acesso  |
|------------|---------------------|----------------------------|---------|
| `POST`     | `/auth/registrar`   | Cria um novo `Cliente`     | Público |
| `POST`     | `/login`            | Autentica um usuário       | Público |

### Clientes
| Verbo HTTP | Endpoint URL               | Descrição                        | Role      |
|------------|----------------------------|----------------------------------|-----------|
| `POST`     | `/api/v1/clientes`         | Cria um novo cliente             | GERENTE   |
| `GET`      | `/api/v1/clientes`         | Lista todos os clientes          | GERENTE   |
| `GET`      | `/api/v1/clientes/{id}`    | Busca um cliente específico      | AMBOS     |
| `PUT`      | `/api/v1/clientes/{id}`    | Atualiza os dados de um cliente  | AMBOS     |
| `DELETE`   | `/api/v1/clientes/{id}`    | Remove um cliente                | GERENTE   |

### Gerentes
| Verbo HTTP | Endpoint URL               | Descrição                        | Role      |
|------------|----------------------------|----------------------------------|-----------|
| `POST`     | `/api/v1/gerentes`         | Cria um novo gerente             | ADMIN     |
| `GET`      | `/api/v1/gerentes`         | Lista todos os gerentes          | GERENTE   |
| `GET`      | `/api/v1/gerentes/{id}`    | Busca um gerente específico      | GERENTE   |
| `PUT`      | `/api/v1/gerentes/{id}`    | Atualiza os dados de um gerente  | ADMIN     |
| `DELETE`   | `/api/v1/gerentes/{id}`    | Remove um gerente                | ADMIN     |

### Endereços
| Verbo HTTP | Endpoint URL                                      | Descrição                                    | Role    |
|------------|---------------------------------------------------|----------------------------------------------|---------|
| `POST`     | `/api/v1/enderecos`                               | Cria um novo endereço                        | AMBOS   |
| `GET`      | `/api/v1/enderecos`                               | Lista todos os endereços                     | GERENTE |
| `GET`      | `/api/v1/enderecos/{id}`                          | Busca um endereço específico                 | AMBOS   |
| `GET`      | `/api/v1/enderecos/cep/{cep}`                     | Busca endereço por CEP (ViaCEP)              | AMBOS   |
| `GET`      | `/api/v1/enderecos/cliente/{clienteId}`           | Lista endereços de um cliente                | AMBOS   |
| `GET`      | `/api/v1/enderecos/cliente/{clienteId}/tipo/{tipo}` | Busca endereço por cliente e tipo          | AMBOS   |
| `PUT`      | `/api/v1/enderecos/{id}`                          | Atualiza um endereço                         | AMBOS   |
| `DELETE`   | `/api/v1/enderecos/{id}`                          | Remove um endereço                           | AMBOS   |

### Contas Bancárias
*Endpoints em desenvolvimento*

| Tipo de Conta     | Características                                           |
|-------------------|-----------------------------------------------------------|
| Conta Corrente    | Possui limite de cheque especial configurável             |
| Conta Poupança    | Possui rendimento e data de aniversário                   |
| Conta Jovem       | Vinculada a um responsável, sem limite                    |
| Conta Global      | Saldo em dólares com conversão automática para reais     |

### Transações
*Endpoints em desenvolvimento*

Operações disponíveis:
- ✅ **DEPOSITO:** Entrada de valores na conta
- ✅ **SAQUE:** Retirada de valores da conta
- ✅ **TRANSFERENCIA_ENVIADA:** Transferência para outra conta
- ✅ **TRANSFERENCIA_RECEBIDA:** Recebimento de transferência

Cada transação possui:
- NSU único alfanumérico (32 caracteres)
- Data/hora de execução
- Gerente executor
- Motivo da movimentação
- Conta(s) envolvida(s)

---

## 🔧 Recursos Especiais

### Validação de CPF
O sistema possui validador customizado de CPF que pode ser usado via annotation:
```java
@Cpf
private String cpf;
```

### Integração ViaCEP
Busca automática de endereço através do CEP:
```bash
GET /api/v1/enderecos/cep/01310100
```

### Conversão de Moedas (Conta Global)
A Conta Global possui integração com a ExchangeRate-API para:
- Consultar cotação USD/BRL em tempo real
- Converter saldo em dólares para reais automaticamente
- Cache de cotações para melhor performance

**API Key:** `6b2fcaba86114260aa3dd360e48d6a49`

### Sistema de Cache
Cotações de câmbio são cacheadas para otimizar performance e reduzir chamadas à API externa.

---

## 📁 Principais DTOs

### Requests
- `ClienteRequest`, `ClienteUpdateRequest`
- `GerenteRequest`
- `EnderecoRequest`, `EnderecoUpdateRequest`
- `ContaCorrenteRequest`, `ContaPoupRequest`, `ContaJovemRequest`, `ContaGlobalRequest`
- `TransacaoRequest`

### Responses
- `ClienteResponse`
- `GerenteResponse`
- `EnderecoResponse`
- `ContaCorrenteResponse`, `ContaPoupResponse`, `ContaGlobalResponse`
- `TransacaoResponse`
- `ViaCepResponse` (integração externa)
- `ExchangeRateResponse` (integração externa)

---

## 🔐 Segurança

- **Spring Security:** Autenticação e autorização
- **Roles:** `CLIENTE`, `GERENTE`, `ADMIN`
- **SSL/TLS:** Conexão segura com banco de dados (certificado CA)
- **Validações:** Bean Validation em todos os endpoints
- **Password Encoding:** Senhas criptografadas (BCrypt)

---

## 📊 Modelo de Banco de Dados

### Tabelas Principais
- `usuarios` (herança com clientes e gerentes)
- `clientes`
- `gerentes`
- `enderecos`
- `contas` (single table inheritance)
- `conta_titulares` (tabela de junção)
- `transacoes`

### Estratégia de Herança
- **Usuarios:** JOINED (tabelas separadas)
- **Contas:** SINGLE_TABLE (uma tabela com discriminator)

---

## 🧪 Testes

O projeto possui estrutura preparada para testes:
```bash
./gradlew test
```

---

## 📝 Próximos Passos

- [ ] Implementar controllers e services para Contas
- [ ] Implementar controllers e services para Transações
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar documentação Swagger/OpenAPI
- [ ] Adicionar logs estruturados
- [ ] Implementar auditoria de transações
- [ ] Adicionar endpoints para relatórios
- [ ] Implementar limitações de taxa (rate limiting)
- [ ] Adicionar métricas e monitoramento (Actuator)

---

## 🤝 Contribuindo

Este é um projeto de estudo, mas sugestões e melhorias são bem-vindas!

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## 👨‍💻 Autor

Desenvolvido como projeto de estudos em desenvolvimento back-end com Spring Boot.

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Security](https://spring.io/projects/spring-security)
- [ViaCEP API](https://viacep.com.br/)
- [ExchangeRate-API](https://www.exchangerate-api.com/)
- [MySQL Documentation](https://dev.mysql.com/doc/)

---

**Versão:** 0.0.1-SNAPSHOT  
**Última atualização:** Outubro 2025
