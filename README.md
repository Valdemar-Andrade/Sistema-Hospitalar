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

## Estrutura de Testes
- **Cobertura JaCoCo**: Relatório disponível em `target/jacoco-report/`

```
src/test/java/com/hospital/sistema/
├── controller/
│   ├── PacienteControllerTest.java    # Testes @WebMvcTest
│   ├── ConsultaControllerTest.java
│   └── LoginControllerTest.java
├── service/
│   ├── PacienteServiceTest.java       # Testes com Mockito
│   ├── ConsultaServiceTest.java
│   ├── MedicoServiceTest.java
│   ├── AutenticacaoServiceTest.java
│   └── TriagemServiceTest.java
├── repository/
│   └── PacienteRepositoryTest.java    # Testes @DataJpaTest
├── util/
│   ├── ValidadorDocumentoTest.java
│   ├── SenhaUtilsTest.java
│   └── FilaTriagemTest.java
├── integration/
│   ├── PacienteIntegrationTest.java   # Testes @SpringBootTest
│   └── ConsultaIntegrationTest.java
└── SistemaHospitalarApplicationTests.java
```
## Como Executar os Testes

### Todos os Testes
```bash
mvn test -Dspring.profiles.active=test
```

### Com Relatório de Cobertura
```bash
mvn test jacoco:report
# Relatório em: target/jacoco-report/index.html
```

### Testes Específicos
```bash
# Apenas testes unitários de service
mvn test -Dtest="*ServiceTest"

# Apenas testes de controller
mvn test -Dtest="*ControllerTest"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"

# Teste específico
mvn test -Dtest="PacienteServiceTest#deveSalvarPacienteComDadosValidos"
```

## 📚 Dependências de Teste

```xml
<!-- JUnit 5, Mockito, AssertJ (inclusos no starter-test) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 Database para testes -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```


## 🔧 Configuração de Teste

### application-test.properties
```properties
# Banco H2 em memória
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

## 📈 JaCoCo - Cobertura de Código

O JaCoCo está configurado para:
- Gerar relatórios após execução dos testes
- Verificar cobertura mínima de 50% de linhas
- Relatório HTML em `target/jacoco-report/`

```bash
# Gerar relatório
mvn test jacoco:report

# Verificar cobertura (falha se < 50%)
mvn verify
```

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
