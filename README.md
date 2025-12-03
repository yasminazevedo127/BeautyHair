## README do Projeto BeautyHair

-----

## 1\. Visão Geral e Objetivo

O projeto **BeautyHair** é uma aplicação desktop desenvolvida em **JavaFX** para gerenciamento de um salão de beleza. O sistema oferece uma interface centralizada para agendamento, busca de reservas e administração de catálogos (Serviços e Profissionais).

A arquitetura do projeto segue rigorosamente o padrão **MVC (Model-View-Controller)**, utilizando **Singletons** para acesso a dados e **JSON** para persistência em disco.

[Image of MVC architectural pattern diagram]

-----

## 2\. Estrutura do Projeto

O código-fonte é organizado nos seguintes pacotes:

| Pacote | Conteúdo | Responsabilidade |
| :--- | :--- | :--- |
| `application` | Classe `Main` | Ponto de entrada da aplicação. |
| `model` | `Reserva`, `ReservaFX` | Representação e lógica dos dados. |
| `repository` | Singletons de Repositório | Persistência e acesso aos arquivos JSON. |
| `control` | Controladores JavaFX | Lógica da UI, validação e coordenação. |
| `view` (Resources) | Arquivos FXML | Definição da interface do usuário. |

-----

## 3\. Ponto de Entrada (`application.Main`)

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

## 4\. Camada de Modelo (`model`)

Gerencia a representação e as regras de agendamento.

  * **`Reserva` (POJO):** Usada para persistência e lógica, contém o método `conflitaCom()` para checagem de sobreposição de agendamentos.
  * **`ReservaFX` (FX-friendly):** Usa `JavaFX Properties` para Data Binding com a `TableView` e formulários.

-----

## 5\. Camada de Repositório (`repository`)

Responsável por todas as operações de I/O, utilizando arquivos JSON na raiz do projeto (`reservas.json`, `profissionais.json`, `servicos.json`).

  * **`ReservaRepository`:** Gerencia o CRUD de agendamentos, busca e controle do ID sequencial.
  * **`ProfissionaisRepository` e `ServicosRepository`:** Gerenciam os catálogos de nomes.

-----

## 6\. Camada de Controle (`control`)

Coordena a aplicação, sendo o ponto de contato entre a UI e os dados.

| Controlador | Funcionalidade Principal | Destaque |
| :--- | :--- | :--- |
| `MainViewController` | Gerencia a tabela principal de reservas. | Força o crescimento vertical dos botões e implementa ações de **Editar** / **Excluir** na tabela. |
| `CadastrarReservaController` | Criação de novas reservas. | **Validação rigorosa** de campos, data/hora e **checa conflito** de profissional antes de salvar. |
| `EditarReservaController` | Edição de reservas. | **Bloqueia campos** de agendamento (data, horário) se a reserva já tiver passado, permitindo apenas a alteração do **Status**. |
| `ServicoController` / `ProfissionaisController` | Gerenciamento de listas. | Usa **ListView** com botão de remoção na célula e integra o diálogo genérico `AdicionarItemController`. |
| `AdicionarItemController` | Diálogo genérico Salvar/Cancelar. | Usa **Programação Funcional** (`Consumer<String>`) para retornar o valor salvo ao controlador que o chamou. |

-----

## 7\. Instruções de Execução

1.  **Pré-requisitos:** Java Development Kit (JDK) e Maven configurados.
2.  **Compilar:** Navegue até o diretório raiz e compile:
    ```bash
    mvn clean compile
    ```
3.  **Executar:** Inicie o aplicativo JavaFX:
    ```bash
    mvn javafx:run
    ```
