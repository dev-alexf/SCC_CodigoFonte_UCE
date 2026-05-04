# SCC — Sistema de Controle de Serviços e Cobranças
**UCE – Engenharia de Software II | Uni-FACEF 2026**

---

## Equipe

| Aluno | RA |
|---|---|
| Jefferson Felizardo do Carmo | 25587 |
| Alex Costa da Silveira Filho | 25790 |
| Vítor Hugo de Paula Malta | 26145 |
| João Pedro Martins Garcia | 26171 |

**Professora:** Daniel

---

## Sobre o Sistema

Sistema web de **controle de serviços prestados e cobranças** para um escritório de contabilidade de pequeno porte, desenvolvido como projeto da UCE de Engenharia de Software II.

### Funcionalidades Implementadas

| Módulo | Funcionalidades |
|---|---|
| **Login / Segurança** | Autenticação com perfis (Admin e Funcionário), senhas criptografadas com BCrypt |
| **Dashboard** | Resumo financeiro (À Cobrar, Pendente, Pago, Recebido no Mês), alertas de inadimplência |
| **Clientes** | Cadastro PF/PJ, busca por nome/CPF/email, ficha completa por cliente |
| **Serviços** | Registro de serviços, controle de status (À Cobrar → Pendente → Pago) |
| **Pagamentos** | Registro de pagamentos parciais, cálculo automático de saldo devedor |
| **Inadimplência** | Aba separada (pendentes > 30 dias), conforme solicitado na elicitação |
| **Relatórios** | Filtros por período, cliente e status (somente Admin) |

### Status de Pagamento (conforme elicitação)
- **À Cobrar** → Serviço anotado, ainda não cobrado
- **Pendente** → Cobrança emitida, aguardando pagamento
- **Pago** → Valor integralmente quitado

---

## Como Executar

### Pré-requisitos

| Ferramenta | Versão mínima | Download |
|---|---|---|
| Java JDK | 17 ou superior | https://adoptium.net |
| Apache Maven | 3.8+ | https://maven.apache.org |

> **Verificar instalação:**
> ```bash
> java -version
> mvn -version
> ```

### Passos para Executar

**1. Abrir o terminal na pasta do projeto:**
```bash
cd scc
```

**2. Compilar e iniciar o servidor:**
```bash
mvn spring-boot:run
```

**3. Aguardar a mensagem:**
```
[SCC] Banco inicializado! Login: admin / admin123
Started SccApplication in X.XX seconds
```

**4. Acessar no navegador:**
```
http://localhost:8080
```

---

## Credenciais de Acesso

| Perfil | Login | Senha | Acesso |
|---|---|---|---|
| **Administrador** | `admin` | `admin123` | Tudo, incluindo Relatórios |
| **Funcionário** | `funcionario` | `func123` | Dashboard, Clientes, Inadimplência |

---

## Estrutura do Projeto

```
scc/
├── pom.xml                         ← Dependências Maven (Spring Boot 3.2.5)
├── README.md
└── src/
    └── main/
        ├── java/br/unifacef/scc/
        │   ├── SccApplication.java         ← Classe principal
        │   ├── model/
        │   │   ├── Usuario.java            ← Entidade usuário
        │   │   ├── Cliente.java            ← Entidade cliente (PF/PJ)
        │   │   ├── Servico.java            ← Entidade serviço prestado
        │   │   ├── Pagamento.java          ← Entidade pagamento (parcial/total)
        │   │   ├── StatusServico.java      ← Enum: A_COBRAR, PENDENTE, PAGO
        │   │   └── PerfilUsuario.java      ← Enum: ADMIN, FUNCIONARIO
        │   ├── repository/
        │   │   ├── UsuarioRepository.java
        │   │   ├── ClienteRepository.java  ← Busca por nome/CPF/email
        │   │   ├── ServicoRepository.java  ← Queries de inadimplência e relatório
        │   │   └── PagamentoRepository.java
        │   ├── service/
        │   │   ├── UsuarioService.java     ← Autenticação Spring Security
        │   │   ├── ClienteService.java
        │   │   └── ServicoService.java     ← Regras de negócio e pagamentos
        │   ├── controller/
        │   │   ├── DashboardController.java
        │   │   ├── ClienteController.java
        │   │   ├── InadimplenciaController.java
        │   │   └── RelatorioController.java
        │   └── config/
        │       ├── SecurityConfig.java     ← Spring Security (login, rotas, BCrypt)
        │       └── DataSeeder.java         ← Dados de exemplo para demonstração
        └── resources/
            ├── application.properties      ← Configurações (porta, banco H2)
            ├── templates/                  ← Páginas HTML (Thymeleaf)
            │   ├── login.html
            │   ├── dashboard.html
            │   ├── inadimplencia.html
            │   ├── clientes/
            │   │   ├── lista.html
            │   │   ├── form.html
            │   │   └── ficha.html
            │   └── relatorios/
            │       └── relatorio.html
            └── static/
                └── css/style.css           ← Estilo visual do sistema
```

---

## Tecnologias Utilizadas

| Tecnologia | Finalidade |
|---|---|
| **Java 21** | Linguagem principal |
| **Spring Boot 3.2.5** | Framework web MVC |
| **Spring Security** | Autenticação e autorização |
| **Spring Data JPA** | Acesso ao banco de dados |
| **Banco H2** | Banco embutido (arquivo local, sem instalação) |
| **Thymeleaf** | Templates HTML server-side |
| **HTML5 / CSS3** | Interface do usuário |

---

## Banco de Dados

O banco **H2** é embutido — não precisa instalar nada. Os dados ficam salvos em `./data/scc.mv.db`.

Para acessar o console do banco durante a execução:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:file:./data/scc
Usuario: sa
Senha: (vazio)
```

---

## Dados de Demonstração

Na primeira execução, o sistema popula automaticamente:
- **3 clientes** (2 PF + 1 PJ)
- **5 serviços** com diferentes status
- **3 pagamentos** (incluindo um pagamento parcial)
- **1 cliente inadimplente** (pendente há 40 dias — para demonstrar a aba de inadimplência)
