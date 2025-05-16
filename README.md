# Projeto Blog Pessoal - Backend com Spring Boot

<div align="center">
<img src="https://i.imgur.com/w8tTOuT.png" title="source: imgur.com" /> 
</div>

<div align="center">
<img src="https://img.shields.io/github/languages/top/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/github/repo-size/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/github/languages/count/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/github/last-commit/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/github/issues/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/github/issues-pr/seuusuario/aulas_java_generation?style=flat-square" />
<img src="https://img.shields.io/badge/status-construção-yellow" alt="Status: Em Construção">

## 1. Descrição

Este projeto **Blog Pessoal** foi criado com base em um projeto do meu professor, cujo objetivo é desenvolver uma aplicação que permite que usuários publiquem, editem e visualizem postagens relacionadas a temas variados, de forma organizada e segura. Este projeto foi desenvolvido para fins educacionais, simulando uma aplicação real de blog para praticar conceitos de API REST com Java e Spring Boot.

Os principais recursos incluem:

1. Criação, edição e exclusão de postagens  
2. Associação de postagens a temas específicos  
3. Cadastro e autenticação de usuários  
4. Visualização de postagens por tema ou usuário  
5. Controle de acesso a operações sensíveis  

(Para mais detalhes sobre o projeto, consulte as instruções do meu professor nesta documentação.)

## 2. Sobre esta API

A API do Blog Pessoal foi desenvolvida utilizando **Java** e o **framework Spring**, seguindo a arquitetura MVC e princípios REST. Ela oferece endpoints para gerenciamento dos recursos **Usuário**, **Postagem** e **Tema**, permitindo interação entre usuários e conteúdos.

### 2.1. Funcionalidades principais:

- Consulta, cadastro, login e atualização de usuários  
- Gerenciamento de temas para classificar postagens  
- Criação, edição, listagem e remoção de postagens  
- Associação de postagens a temas e autores  
- Autenticação via token JWT para segurança  

## 3. Diagrama de Classes

```mermaid  
classDiagram  
class Postagem {  
  - id : Long  
  - titulo : String  
  - texto : String  
  - data : LocalDateTime  
  - tema : Tema  
  - usuario : Usuario  
}  
class Tema {  
  - id : Long  
  - descricao : String  
  - postagens : List<Postagem>  
}  
class Usuario {  
  - id : Long  
  - nome : String  
  - usuario : String  
  - senha : String  
  - foto : String  
  - postagens : List<Postagem>  
}  
Tema "1" --> "0..*" Postagem : classifica  
Usuario "1" --> "0..*" Postagem : cria  

erDiagram  
    tb_usuarios ||--o{ tb_postagens : escreve  
    tb_temas ||--o{ tb_postagens : classifica  
    tb_usuarios {  
        bigint id PK  
        varchar(255) nome  
        varchar(255) usuario  
        varchar(255) senha  
        varchar(5000) foto  
    }  
    tb_temas {  
        bigint id PK  
        varchar(255) descricao  
    }  
    tb_postagens {  
        bigint id PK  
        varchar(255) titulo  
        text texto  
        datetime data  
        bigint tema_id FK  
        bigint usuario_id FK  
    }  

| Item                          | Descrição       |
| ----------------------------- | --------------- |
| **Servidor**                  | Tomcat          |
| **Linguagem de programação**  | Java            |
| **Framework**                 | Spring Boot     |
| **ORM**                       | JPA + Hibernate |
| **Banco de dados**            | MySQL           |
| **Segurança**                 | Spring Security |
| **Autenticação**              | JWT             |
| **Testes automatizados**      | JUnit           |
| **Documentação**              | SpringDoc       |


6. Requisitos
Para rodar esse projeto localmente, você vai precisar de:

Java JDK 17 ou superior (link)
Banco de dados MySQL (link)
Spring Tools Suite (STS) (link)
Insomnia ou Postman para testar as APIs (Insomnia, Postman)
7. Como executar no STS
7.1. Clonando o projeto
bash
git clone https://github.com/rafaelq80/blogpessoal_spring_t82.git  
7.2. Executando
Abra o projeto no Spring Tools Suite
Na aba Boot Dashboard, localize o projeto
Clique em Start or Restart para iniciar
Acesse http://localhost:8080 no navegador para abrir o Swagger da API
(Lembre-se de configurar o banco de dados antes e de ajustar as configurações do projeto.)

8. Contribuições
Este projeto é uma atividade de estudo e aprendizado, criado a partir de materiais do meu professor. Contribuições e melhorias são bem-vindas! Sinta-se à vontade para:

Criar issues no GitHub
Enviar pull requests com melhorias
9. Contato
Este projeto foi desenvolvido por Carlos Moroni (GitHub). Para dúvidas, sugestões ou colaborações, entre em contato ou abra uma issue aqui no GitHub.

bash

Se desejar, substitua `"seuusuario"` nos Shields por seu usuário do GitHub ou mantenha os links gerais com seu próprio repositório.  
Quer que ajuste algum detalhe ou deixe  