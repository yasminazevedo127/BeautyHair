## 1\. Visão Geral e Objetivo 

O projeto **BeautyHair** é uma aplicação desktop desenvolvida em **JavaFX** para gerenciamento de um salão de beleza. O sistema oferece uma interface centralizada e intuitiva para:

  * **Agendamento** e busca de reservas.
  * Administração de catálogos de **Serviços** e **Profissionais**.

A arquitetura do projeto segue o padrão **MVC (Model-View-Controller)**, utilizando o padrão **Singleton** para gerenciar o acesso a dados e a biblioteca **Gson** para persistência em disco via **JSON**.

-----

## 2\. Tecnologias e Dependências 

O projeto foi construído utilizando as seguintes tecnologias e ferramentas:

  * **Java SDK:** 21
  * **JavaFX:** 21 (Biblioteca de UI)
  * **Maven:** Gerenciamento de dependências.
  * **Gson:** Biblioteca para persistência de dados (I/O) em formato JSON.

-----

## 3\. Estrutura do Projeto 

O código-fonte é organizado em pacotes que refletem a arquitetura MVC, garantindo a separação de responsabilidades:

| Pacote | Conteúdo | Responsabilidade |
| :--- | :--- | :--- |
| `application` | Classe `Main` | Ponto de entrada e inicialização da aplicação (View). |
| `model` | `Reserva`, `ReservaFX` | **Modelo de Dados:** Representação e lógica das regras de negócio (ex: checagem de conflito). |
| `repository` | Singletons de Repositório | **Persistência:** Acesso e manipulação dos arquivos JSON (`*.json`). |
| `control` | Controladores JavaFX | **Controle:** Lógica da UI, validação e orquestração do fluxo. |
| **`services`** | **`ReservaValidator`** | **Regras de Negócio/Validação:** Checagem de integridade e conflito de agendamento. |
| `view` (Resources) | Arquivos FXML | **Visão:** Definição estrutural da interface do usuário. |

-----

## 4\. Ponto de Entrada (`application.Main`) 

A classe `application.Main` é o ponto de partida do aplicativo.

```java
// application/Main.java
public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
    Scene scene = new Scene(root, 900, 600);
    stage.setTitle("BeautyHair");
    stage.setScene(scene);
    stage.show();
}
```

  * **Método `start(Stage stage)`:** Carrega a interface principal (`MainView.fxml`), define as dimensões iniciais da janela (900x600), define o título e exibe a janela.

-----

## 5\. Camadas de Lógica e Persistência

### 5.1. Camada de Modelo (`model`)

Gerencia a representação e as regras de agendamento:

  * **`Reserva` (POJO):** Objeto de domínio usado para persistência e lógica, incluindo o método **`conflitaCom()`** para checagem de sobreposição de agendamentos.
  * **`ReservaFX` (FX-friendly):** Objeto que encapsula `Reserva` e utiliza **JavaFX Properties** para facilitar o Data Binding com a `TableView` e formulários.

### 5.2. Camada de Repositório (`repository`)

Responsável por todas as operações de I/O, utilizando arquivos JSON na raiz do projeto (`reservas.json`, `profissionais.json`, `servicos.json`).

  * **`ReservaRepository`:** Gerencia as operações **CRUD** de agendamentos, busca e controle do ID sequencial.
  * **`ProfissionaisRepository` e `ServicosRepository`:** Gerenciam o carregamento e a persistência dos catálogos de nomes.

-----

## 6\. Camada de Serviços (`services`) e Validação 

O pacote **`services`** contém a classe **`ReservaValidator`**, que centraliza as regras de negócio críticas, atuando como um filtro antes da persistência dos dados.

### `ReservaValidator`

| Método | Tipo de Regra | Descrição |
| :--- | :--- | :--- |
| **`validar(Reserva r)`** | **Validação de Campos e Tempo** | 1. **Obrigatoriedade:** Verifica campos essenciais. 2. **Datas Passadas:** Impede agendamentos para datas ou horários que já se iniciaram. |
| **`existeConflito(...)`** | **Conflito de Agendamento** | Garante que **não haja sobreposição** de horários para o mesmo profissional na mesma data. Ignora o ID da própria reserva sendo editada. |

-----

## 7\. Camada de Controle (`control`) e Utilitários 

O pacote `control` contém a lógica de interface, orquestração e classes de suporte que padronizam a experiência do usuário.

### 7.1. Controladores Específicos

| Controlador | Responsabilidade Primária | Funcionalidades Chave |
| :--- | :--- | :--- |
| `MainViewController` | Gerenciamento da Visão Geral e Navegação. | Exibe agendamentos do **Dia Atual**. Permite **Busca** (`search`) e **Ações** de Editar/Excluir. |
| `CadastrarReservaController` | Criação de Novos Agendamentos. | Utiliza o `ReservaValidator` para checar conflito antes de salvar. |
| `EditarReservaController` | Modificação de Agendamentos. | **Bloqueio Seletivo de Campos:** Para reservas passadas, apenas o campo **Status** fica editável. |
| `AdicionarItemController` | Diálogo de Inclusão Genérico. | Utiliza **Interface Funcional** para adicionar itens ao catálogo. |

### 7.2. Classes de Suporte (Base e Utils)

| Classe | Tipo | Responsabilidade |
| :--- | :--- | :--- |
| **`ControleBase`** | Abstrata | Centraliza o **feedback do usuário** e o gerenciamento básico da janela. Contém métodos padronizados como `sucesso()`, `erro()`, `confirmacao()` e `onCancelar()`. |
| **`JavaFXUtils`** | Utilitária | Implementa métodos **estáticos** para formatação de data/hora (`dd/MM/yyyy`, `HH:mm`), preenchimento de `ComboBox` de horários e configuração do `DatePicker`. |

-----

## 8\. Instruções de Execução 

Para rodar o projeto localmente, siga os passos abaixo:

### Pré-requisitos

  * **Java Development Kit (JDK):** Versão 21 ou superior.
  * **Maven:** Ferramenta de gerenciamento de dependências configurada.

### Comandos

1.  **Compilar:** Navegue até o diretório raiz do projeto e execute o comando para limpar e compilar:
    ```bash
    mvn clean compile
    ```
2.  **Executar:** Inicie o aplicativo JavaFX:
    ```bash
    mvn javafx:run
    ```
