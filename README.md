README: Sistema de Gestão de Parceiros (Terra Verde)
Este documento fornece todas as instruções e informações necessárias para configurar, construir e executar o projeto Sistema de Gestão de Parceiros Comerciais da empresa fictícia Terra Verde, com foco na utilização da IDE Eclipse.

 1. Visão Geral do Projeto
Este é um sistema Full-Stack desenvolvido para gerenciar o cadastro de Clientes e Fornecedores (Parceiros). Ele segue a arquitetura MVC e utiliza Spring Boot para o Backend (API REST) e HTML/CSS/JavaScript puro para o Frontend.

Detalhes Técnicos:
Backend
Java 17 ou superior, Spring Boot 3.x, Spring Data JPA, Spring Security, iText (para PDF)

Frontend
HTML5, CSS (Custom/Bootstrap-like), JavaScript puro (Manipulação de DOM e fetch para API)

Banco de Dados
PostgreSQL

Padrão
API REST (O controle de sessão é simulado via localStorage no cliente)

2. Requisitos de Execução e Configuração do Eclipse
Para rodar o projeto no Eclipse, você precisa ter instalado:

Eclipse IDE: Recomendado o pacote Eclipse IDE for Enterprise Java and Web Developers.

Java Development Kit (JDK): Versão 17 ou superior configurada no Eclipse.

Apache Maven: O Eclipse deve estar configurado para usar o Maven (geralmente já vem integrado).

PostgreSQL Server: Versão 10 ou superior.

2.1. Importação do Projeto no Eclipse
Baixe o projeto do repositório em Code > Download ZIP
Descompacte o arquivo baixado.
No Eclipse, vá em File > Import....
Selecione Maven > Existing Maven Projects.
Clique em Browse... e selecione o diretório raiz do projeto (onde está o arquivo pom.xml).
Clique em Finish. O Eclipse irá importar o projeto e baixar as dependências do Maven.

2.2. Atualização de dependências do Maven
Clique com o botão direito em cima da pasta do projeto.
Selecione Maven > Update Project > marque Force update of snapshots/releases > Ok.
Agora vá em Window, na barra de opções superior do Eclipse, clique em Preferences > Maven 
Tenha marcado as caixinhas Download Artifact Sources, Download Artifact Javadoc, Download repository index updates on startup, Update Maven projects on startup e Automatically update Maven projects configuration.

3. Instalação do Plugin Lombok no Eclipse
O Lombok usa um truque para modificar o código-fonte compilado (bytecode) durante o build. O Eclipse precisa de um plugin instalado em sua JVM para permitir essa alteração.

3.1. Localizando o Arquivo JAR
O arquivo .jar do Lombok é baixado pelo Maven para o seu repositório local. Siga os passos:
No Eclipse, localize o JAR do Lombok no seu projeto (geralmente em Maven Dependencies).
Clique com o botão direito no arquivo JAR e vá em Properties.
Copie o caminho completo (Path) para o arquivo, que geralmente é similar a: C:\Users\seu_usuario\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.32.jar

3.2. Execução do Instalador
Abra o terminal ou prompt de comando.
Execute o arquivo JAR do Lombok (use o caminho completo que você copiou acima):

java -jar C:\Users\seu_usuario\.m2\repository\org\projectlombok\lombok\1.18.30\lombok-1.18.32.jar

Uma janela de instalação do Lombok aparecerá:
O instalador tentará detectar automaticamente as instalações do Eclipse.
Se o Eclipse não for detectado, clique em Specify location... e aponte para o arquivo executável do Eclipse (o eclipse.exe).
Clique em Install / Update.

3.3. Reinicialização do Eclipse
Após a instalação:
Feche completamente o Eclipse.
Reinicie o Eclipse.

4. Configuração do Banco de Dados (PostgreSQL)
O projeto exige uma instância do PostgreSQL rodando e um banco de dados configurado. Então baixe e instale o PostgreSQL na sua máquina.

4.1. Criação do Banco de Dados
Crie um banco de dados para a aplicação. Você pode usar a interface pgAdmin ou o psql (terminal) para criar o banco de dados:

SQL
CREATE DATABASE db_terra_verde;

4.2. Configuração do Arquivo application.properties
Abra o arquivo src/main/resources/application.properties e ajuste as credenciais de conexão do seu banco de dados:
Properties
# ------------------------------------------------
# PostgreSQL Configuration
# ------------------------------------------------
spring.datasource.url=jdbc:postgresql://localhost:5432/db_terra_verde
spring.datasource.username=seu_usuario_postgres
spring.datasource.password=sua_senha_postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuração JPA (Hibernate)
spring.jpa.hibernate.ddl-auto=update  # Cria ou atualiza as tabelas automaticamente
spring.jpa.show-sql=true

Atenção: Mantenha spring.jpa.hibernate.ddl-auto=update para que o Spring crie as tabelas tb_parceiros e tb_usuarios automaticamente na primeira execução.

5. Execução do Projeto no Eclipse
Após configurar o banco de dados, resta executar  a aplicação:
Clique na pasta do projeto
Localize um ícone de  “play” verde com uma seta do lado, na barra superior de opções do Eclipse.
Clique na seta e selecione “CadastroClienteFornecedorApplication” para rodar a aplicação.
O console do Eclipse exibirá as informações de inicialização. Procure pela mensagem indicando que o servidor foi iniciado na porta 8080:
>>> Usuário ADMIN inicial criado: admin/admin123
... Started CadastroClienteFornecedorApplication in X.XXX seconds (process running for Y.YYY)

5.1. Primeiro Acesso e Credenciais
Abra seu navegador e acesse a URL: http://localhost:8080/
O sistema irá redirecionar para a página de Login.

Credenciais Iniciais para testar (Injetadas pelo SecurityConfig.java):

Username: admin

Password: admin123


