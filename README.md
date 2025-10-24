# Desafio Backend Nimble 🚀

### 📋 Índice

* [Sobre](#-sobre)
* [Funcionalidades](#-funcionalidades)
* [Demonstração](#-demonstração)
* [Como Executar](#-como-executar)
* [Tecnologias](#-tecnologias)
* [Uso de Inteligencia Artificial](#-uso-de-inteligencia-artificial)
* [Observações](#-observações)

---

### 📖 Sobre

Projeto para desafio da backend Nimble. É um simples projeto para transações financeiras a partir de cobranças.

---

### ✨ Funcionalidades

- [x] Sign in: login de usuario.
- [x] Sign up: cadastro de usuario.
- [x] Depósito monetário: depósito no saldo do usuario.
- [x] Cadastro de cobrança: cadastro de cobrança para um destinatário a partir do CPF.
- [x] Consulta de cobranças: lista as cobranças com filtro de status e relação do usuario com a cobranca.
- [x] Pagamento de cobranca: realiza pagamento das cobranças com saldo da conta ou cartão de crédito.
- [x] Cancelamento de cobranças: cancela cobranças.

---

### 🚀 Como Executar

Clonar o repositório ou fazer o download do projeto.

Será necessário rodar o banco de dados MySQL a partir de um docker compose. Depois que for executado, basta rodar o projeto na sua IDE e começar a consumir os recursos.

Para rodar o banco de dado dockeirizado, basta rodar o seguinte comando:

```bash
docker-compose up
```
ou caso queira usar docker compose no modo detached:
```bash
docker-compose up -d
```

---

### 🛠️ Tecnologias

As seguintes ferramentas e tecnologias foram utilizadas na construção do projeto:

* Java
* Spring Web
* Spring Boot
* Spring Data
* Spring Security
* OAuth2
* MySQL
* Jacoco
* JUnit5
* Mockito
* Maven

---

### Uso de Inteligencia Artificial

* Criação da validação do CPF e numero de Cartão (só a lógica, a implementação com annotation foi uma ideia minha)
* Criação de trechos dos códigos automatizados.
* Auxílio na solução de bugs e demais inconsistências que apareceram
* Criação da documentação (guiado por mim)
* Dúvidas sobre possíveis melhorias de código
* Criação deste readme aqui

### Observações

Devido a problemas pessoais, infelizmente não tive tempo de implementar todas as funcionalidades que eu queria, como por exemplo:

* adicionar validação de horário de depósito de pagamento, assim como valor limite
* mapear cartao e conta bancaria como entidades e não como simples atributos de classe
* dockeirizar toda a aplicação
* implementar hateoas

Tentei dar o meu melhor com o tempo que eu tinha, e até o que eu não tinha (deve ter alguns commits feitos por volta das 3 da manhã rsrs).

