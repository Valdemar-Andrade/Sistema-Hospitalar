# 🏥 Sistema Hospitalar

Sistema de Gestão Hospitalar desenvolvido com **Spring Boot** para gerenciamento completo de pacientes, médicos, consultas e triagens.

- Java 17
- Spring Boot 3.4.3
- Spring Data JPA
- PostgreSQL
- Thymeleaf
---

## Descrição

Este sistema foi desenvolvido para automatizar e otimizar os processos de um ambiente hospitalar, oferecendo funcionalidades para diferentes perfis de usuários: **Administradores**, **Médicos**, **Enfermeiros** e **Recepcionistas**.

O sistema permite o cadastro e gerenciamento de pacientes, agendamento de consultas, realização de triagens com classificação de risco, e acompanhamento do fluxo de atendimento hospitalar.

---

## Funcionalidades Principais

### Administrador
- Gerenciamento de usuários (médicos, enfermeiros, recepcionistas)
- Cadastro e edição de pacientes
- Gerenciamento de especialidades médicas
- Configuração de tipos de consulta
- Relatórios e configurações do sistema

### Médico
- Visualização da fila de pacientes aguardando atendimento
- Acesso ao histórico de consultas dos pacientes
- Registro de diagnósticos e prescrições
- Acompanhamento de consultas agendadas

### Enfermeiro
- Realização de triagens com coleta de sinais vitais
- Classificação de risco (Protocolo de Manchester)
- Encaminhamento de pacientes para atendimento médico
- Visualização da fila de triagem

### Recepcionista
- Agendamento de consultas
- Cadastro de novos pacientes
- Check-in de pacientes
- Encaminhamento para triagem

---

## Tecnologias Utilizadas

| Tecnologia | Descrição |
|------------|-----------|
| **Java 17** | Linguagem de programação |
| **Spring Boot 3.4.3** | Framework principal |
| **Spring Data JPA** | Persistência de dados |
| **Spring Validation** | Validação de dados |
| **PostgreSQL** | Banco de dados relacional |
| **Thymeleaf** | Template engine para views |
| **Maven** | Gerenciamento de dependências |

---

## Estrutura do Projeto

```
src/main/java/com/hospital/sistema/
├── config/           # Configurações (sessão, beans)
├── controller/       # Controladores REST e MVC
├── dto/              # Objetos de transferência de dados
├── entity/           # Entidades JPA
├── enums/            # Enumerações do sistema
├── exception/        # Exceções personalizadas
├── repository/       # Repositórios JPA
├── service/          # Regras de negócio
└── util/             # Classes utilitárias

src/main/resources/
├── templates/        # Templates Thymeleaf
├── static/           # Arquivos estáticos (CSS, JS)
└── application.properties
```

---

## Como Executar

### Pré-requisitos

- **Java 17** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+**

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:
```sql
CREATE DATABASE hospital_db;
```

2. Configure as credenciais no arquivo `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### Executando a Aplicação

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/sistema-hospitalar.git

# Entre no diretório
cd sistema-hospitalar

# Execute com Maven
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Credenciais Padrão

Na primeira execução, uma conta de administrador é criada automaticamente:
- **Login:** admin
- **Senha:** admin123

> ⚠️ **Importante:** Altere a senha padrão após o primeiro acesso!

---

## Entidades Principais

| Entidade | Descrição |
|----------|-----------|
| **Paciente** | Dados pessoais, documentos, histórico médico |
| **Medico** | Dados do profissional, especialidade, horários |
| **Enfermeiro** | Dados do profissional, COREN |
| **Consulta** | Agendamentos, diagnósticos, prescrições |
| **Triagem** | Sinais vitais, classificação de risco |
| **Especialidade** | Áreas de atuação médica |

---

## Melhorias Futuras

- [ ] Implementação de autenticação JWT para APIs
- [ ] Dashboard com gráficos estatísticos
- [ ] Sistema de notificações por email/SMS
- [ ] Sistema de agendamento online
- [ ] Relatórios exportáveis (PDF, Excel)

---

## Autor

Desenvolvido por Valdemar-Andrade.
Email: andradevaldemar298@gmail.com