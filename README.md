# GameTesting — Sistema de Gerenciamento de Sessões de Teste

> Aplicação web para gerenciamento de projetos de teste, estratégias, sessões e usuários, desenvolvida com **Spring Boot**, **Thymeleaf** e **MySQL**.

---

## Membros

Yasmin Victoria Oliveira 812308
Melissa Shimada 821620
Fernanda Aramaki 791969

## Tecnologias Utilizadas

- Java 17  
- Spring Boot  
- Spring MVC + Spring Security  
- Thymeleaf  
- MySQL  
- Maven

---

## Como Rodar o Projeto

### 1. Clone o repositório:

```bash
git clone https://github.com/yasminvo/GameTesting
cd GameTesting

```

### 2. Configure o banco de dados
> Certifique-se de ter o MySQL rodando localmente. Crie um banco com o nome desejado, por exemplo:

```bash
spring.datasource.url = jdbc:mysql://localhost:3306/game_testing
spring.datasource.username = user-game-testing
spring.datasource.password = senha-game-testing
spring.jpa.hibernate.ddl-auto=update
Obs: Você pode alterar o ddl-auto para create, update, validate ou none conforme o ambiente.

```

### 3. Compile o projeto com Maven
```bash
mvn clean install
```

### 4. Execute a aplicação
```bash
mvn spring-boot:run
```

### 5. Acesse no navegador
```bash
http://localhost:8080
```
## Perfis de Usuário
ADMIN: tem acesso total à criação, edição e exclusão de usuários, estratégias, sessões e projetos.

TESTER: pode visualizar os projetos dos quais participa e executar sessões de teste, além de ver todas as estratégias cadastradas no sistema.

## Interface 

![Tela de Login](/images/login.png)

![Admin dashboard](/images/dashboard-admin.png)

