# SisAgi - Simulador de Sistema Bancário 🏦

Um sistema de back-end para um simulador bancário, permitindo o gerenciamento de clientes, gerentes, contas e transações. Este projeto foi desenvolvido como uma aplicação monolítica bem estruturada utilizando as melhores práticas do ecossistema Spring.

---

## 🎯 Sobre o Projeto

O SisAgi foi criado como um projeto de estudo para aplicar e aprofundar conhecimentos em desenvolvimento back-end com Java e Spring Boot. O objetivo é simular as funcionalidades essenciais de um sistema bancário, como cadastro de usuários, autenticação, gerenciamento de contas e operações financeiras, tudo exposto através de uma API RESTful segura.

---

## ✨ Funcionalidades

-   ✅ **Autenticação e Autorização:** Sistema de registro e login com diferenciação de papéis (`CLIENTE`, `GERENTE`) usando Spring Security.
-   ✅ **CRUD de Clientes:** Operações completas de Criação, Leitura, Atualização e Deleção para clientes.
-   ✅ **CRUD de Gerentes:** Operações completas de Criação, Leitura, Atualização e Deleção para gerentes.
-   ⏳ **Gestão de Contas:** (Em desenvolvimento) Criação e consulta de contas bancárias associadas a clientes.
-   ⏳ **Transações Financeiras:** (Em desenvolvimento) Lógica para depósitos, saques e transferências.

---

## 🛠️ Tecnologias Utilizadas

Este projeto foi construído com as seguintes tecnologias:

-   **Linguagem:** Java 21
-   **Framework:** Spring Boot 3.x
-   **Segurança:** Spring Security
-   **Persistência de Dados:** Spring Data JPA / Hibernate
-   **Banco de Dados:** MySQL (hospedado no Aiven)
-   **Build Tool:** Gradle
-   **Utilitários:** Lombok
-   **Ambiente de Desenvolvimento:** IntelliJ IDEA, Docker (para banco de dados local opcional), DBeaver

---

## 🚀 Como Rodar o Projeto

Siga os passos abaixo para configurar e executar o ambiente de desenvolvimento local.

### Pré-requisitos

-   JDK 21 ou superior
-   IntelliJ IDEA ou outra IDE de sua preferência
-   Conta no [Aiven](https://aiven.io/) com um serviço MySQL ativo

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SisAgi/SISAGI.git](https://github.com/SisAgi/SISAGI.git)
    ```

2.  **Configure a Conexão com o Banco Aiven:**
    - Baixe o certificado `ca.pem` do seu serviço MySQL no Aiven.
    - Configure o sua conexão usando o arquivo `ca.pem`.

3.  **Configure o `application.yml`:**
    - Abra o arquivo `src/main/resources/application.yml`.
    - Preencha os placeholders (`<...>`) com as suas credenciais do Aiven.

4.  **Execute a Aplicação:**
    - Abra o projeto no IntelliJ e execute a classe principal `Sisagi1Application.java`.
    - O servidor iniciará na porta `8080`.

---

## 📖 Endpoints da API

A API está documentada abaixo. Todos os endpoints, exceto `/auth/**`, requerem autenticação.

### Autenticação e Registro
| Verbo HTTP | Endpoint URL        | Descrição                  |
|------------|---------------------|----------------------------|
| `POST`     | `/auth/registrar`   | Cria um novo `Cliente`.    |
| `POST`     | `/login`            | Autentica um usuário.      |

### Clientes
| Verbo HTTP | Endpoint URL               | Descrição                        |
|------------|----------------------------|----------------------------------|
| `GET`      | `/api/v1/clientes`         | Lista todos os clientes.         |
| `GET`      | `/api/v1/clientes/{id}`    | Busca um cliente específico.     |
| `PUT`      | `/api/v1/clientes/{id}`    | Atualiza os dados de um cliente. |
| `DELETE`   | `/api/v1/clientes/{id}`    | Remove um cliente.               |

*(Adicione as tabelas para Gerentes, Contas e Transações aqui conforme você as desenvolve)*

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---
