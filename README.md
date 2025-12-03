## 1\. Visão Geral e Objetivo

O projeto **BeautyHair** é uma aplicação desktop desenvolvida em **JavaFX** para gerenciamento de um salão de beleza. O sistema oferece uma interface centralizada para agendamento, busca de reservas e administração de catálogos (Serviços e Profissionais).

A arquitetura do projeto segue o padrão **MVC (Model-View-Controller)**, utilizando **Singletons** para acesso a dados e **JSON** para persistência em disco.

-----
## 2\. Tecnologias e Dependências 
O projeto utiliza as seguintes tecnologias:

* Java SDK: 21

* JavaFX: 21

* Maven: Gerenciamento de dependências.

* Gson: Biblioteca para persistência de dados em JSON.

-----
## 3\. Estrutura do Projeto

O código-fonte é organizado nos seguintes pacotes:

| Pacote | Conteúdo | Responsabilidade |
| :--- | :--- | :--- |
| `application` | Classe `Main` | Ponto de entrada da aplicação. |
| `model` | `Reserva`, `ReservaFX` | Representação e lógica dos dados. |
| `repository` | Singletons de Repositório | Persistência e acesso aos arquivos JSON. |
| `control` | Controladores JavaFX | Lógica da UI, validação e coordenação. |
| `view` (Resources) | Arquivos FXML | Definição da interface do usuário. |

-----

## 4\. Ponto de Entrada (`application.Main`)

A classe `application.Main` é o ponto de partida do aplicativo.

  * **Método `start(Stage stage)`:**
      * Carrega a interface principal (`MainView.fxml`).
      * Define as dimensões iniciais da janela (`900x600`).
      * Define o título da janela (`BeautyHair`).
      * Exibe a janela.

<!-- end list -->

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

-----

## 5\. Camada de Modelo (`model`)

Gerencia a representação e as regras de agendamento.

  * **`Reserva` (POJO):** Usada para persistência e lógica, contém o método `conflitaCom()` para checagem de sobreposição de agendamentos.
  * **`ReservaFX` (FX-friendly):** Usa `JavaFX Properties` para Data Binding com a `TableView` e formulários.

-----

## 6\. Camada de Repositório (`repository`)

Responsável por todas as operações de I/O, utilizando arquivos JSON na raiz do projeto (`reservas.json`, `profissionais.json`, `servicos.json`).

  * **`ReservaRepository`:** Gerencia o CRUD de agendamentos, busca e controle do ID sequencial.
  * **`ProfissionaisRepository` e `ServicosRepository`:** Gerenciam os catálogos de nomes.

-----

## 7\. Camada de Controle (`control`)

O pacote `control` contém a lógica de interface, validação e orquestração do fluxo de trabalho, atuando como o intermediário entre a View (FXML) e o Modelo/Repositório.

| Controlador | Responsabilidade Primária | Funcionalidades Chave |
| :--- | :--- | :--- |
| **`MainViewController`** | Gerenciamento da Visão Geral e Navegação. | Exibe a lista de agendamentos do **Dia Atual** por padrão. Permite **Busca** (`search`) e **Ações** de Editar/Excluir na tabela. |
| **`CadastrarReservaController`** | Criação de Novos Agendamentos. | **Validação de Campos** (obrigatórios, data/hora no futuro) e **Checagem de Conflito** (Garante exclusividade de horário/data por profissional). |
| **`EditarReservaController`** | Modificação de Agendamentos. | **Bloqueio Seletivo de Campos:** Para reservas passadas, apenas o campo **Status** fica editável, impedindo alterações acidentais de data/hora. |
| **`ServicoController` / `ProfissionaisController`** | Gerenciamento de Catálogos. | Exibe e gerencia as listas de itens, utilizando `ListView` com botões de remoção. |
| **`AdicionarItemController`** | Diálogo de Inclusão Genérico. | Controlador reutilizável que utiliza uma **Interface Funcional (`Consumer<String>`)** para adicionar itens (Serviço ou Profissional) de volta ao controlador chamador. |

---

### Detalhamento do Fluxo de Agendamento

A validação de conflito de horário é uma regra crítica de negócio implementada através da interação entre o `CadastrarReservaController` e o objeto `Reserva` (método `conflitaCom`), garantindo a integridade dos agendamentos:

1.  O **Controller** coleta os dados (profissional, data, hora).
2.  Cria um objeto `Reserva` temporário.
3.  Consulta o **`ReservaRepository`** para verificar se *alguma* reserva existente **conflita** com o objeto temporário.
4.  Somente se não houver conflito, o **Controller** chama `repo.add()` para persistir a nova reserva.

-----

## 8\. Instruções de Execução

1.  **Pré-requisitos:** Java Development Kit (JDK) e Maven configurados.
2.  **Compilar:** Navegue até o diretório raiz e compile:
    ```bash
    mvn clean compile
    ```
3.  **Executar:** Inicie o aplicativo JavaFX:
    ```bash
    mvn javafx:run
    ```
