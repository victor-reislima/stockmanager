# StockManager

Sistema de gerenciamento de estoque desenvolvido em Java puro, com foco em
orientação a objetos e organização em camadas.

Versão atual: **1.2**. Menu interativo no terminal com cadastro, listagem,
busca, atualização de preço, entrada e saída de estoque e remoção de produtos,
com validação de dados. Os dados ficam em memória.

---

## Novidades da versão 1.2

- **Interface `EstoqueView`** (`view/EstoqueView.java`): define o contrato que
  toda tela do StockManager precisa cumprir.
- O `MenuView` agora **implementa** essa interface (`implements EstoqueView`),
  e seus métodos são marcados com `@Override`.
- O `Main` passa a depender do **contrato** (`EstoqueView`), não da
  implementação. Para trocar o terminal por uma interface gráfica no futuro,
  basta criar outra classe que implemente `EstoqueView`.

---

## Novidades da versão 1.1

- **Menu interativo** no terminal (`view/MenuView.java`).
- **Remoção** de produtos.
- **Atualização de preço**.
- **Controle de entrada e saída** de estoque, sem permitir estoque negativo.
- **Validação de dados**: nome em branco, preço zero ou negativo, quantidade
  negativa e entradas não numéricas são rejeitados com mensagem clara.
- **ID gerado automaticamente** pelo `EstoqueService`, sem risco de IDs repetidos.
- O `EstoqueService` **não imprime mais nada**: ele devolve dados e a camada de
  visão decide como exibi-los.

---

## Estrutura do projeto

```text
stockmanager/
├── .vscode/
│   └── settings.json          Configuração do VS Code (raiz do código-fonte)
├── src/
│   ├── Main.java              Ponto de entrada da aplicação
│   ├── model/
│   │   └── Produto.java       Entidade que representa um produto
│   ├── service/
│   │   ├── EstoqueService.java                Regras de negócio do estoque
│   │   └── ProdutoNaoEncontradoException.java Erro de produto inexistente
│   └── view/
│       ├── EstoqueView.java   Interface (contrato) das telas
│       └── MenuView.java      Menu interativo no terminal
└── README.md                  Este documento
```

O projeto segue uma separação em camadas:

| Camada    | Pacote    | Responsabilidade                                   |
|-----------|-----------|----------------------------------------------------|
| Modelo    | `model`   | Representar os dados (o que é um produto)          |
| Serviço   | `service` | Aplicar as regras de negócio (o que fazer com eles)|
| Visão     | `view`    | Interagir com o usuário (ler e exibir)             |

Cada camada tem uma única responsabilidade. Assim, trocar a forma de exibição
(terminal, interface gráfica, web) não exige alterar as regras de negócio, e
trocar o armazenamento (lista em memória, arquivo, banco de dados) não exige
alterar o modelo.

---

## `model/Produto.java`

Representa um produto do estoque. É uma classe de **modelo**: guarda dados e
oferece acesso controlado a eles, sem conter regras de negócio.

### Atributos

| Atributo     | Tipo     | Descrição                          |
|--------------|----------|------------------------------------|
| `id`         | `int`    | Identificador único do produto     |
| `nome`       | `String` | Nome do produto                    |
| `preco`      | `double` | Preço unitário em reais            |
| `quantidade` | `int`    | Quantidade disponível em estoque   |

Todos os atributos são `private`, ou seja, nenhuma outra classe pode lê-los ou
alterá-los diretamente. Esse é o princípio do **encapsulamento**: o objeto
controla o próprio estado e expõe apenas operações públicas.

### Construtor

```java
public Produto(int id, String nome, double preco, int quantidade)
```

Cria um produto já com todos os dados preenchidos. A palavra-chave `this`
diferencia o atributo do objeto (`this.nome`) do parâmetro recebido (`nome`).

### Getters e setters

| Método                    | Função                         |
|---------------------------|--------------------------------|
| `getId()`                 | Retorna o ID                   |
| `getNome()` / `setNome()` | Lê / altera o nome             |
| `getPreco()` / `setPreco()` | Lê / altera o preço          |
| `getQuantidade()` / `setQuantidade()` | Lê / altera a quantidade |

**Por que não existe `setId()`?** O ID identifica o produto dentro do estoque.
Se ele pudesse ser alterado depois do cadastro, dois produtos poderiam acabar
com o mesmo ID e a busca deixaria de ser confiável. Por isso o ID só é
definido uma vez, no construtor.

### `toString()`

Sobrescreve (`@Override`) o método herdado da classe `Object` para que o
produto seja exibido em formato legível:

```
ID: 1 | Nome: Teclado Mecânico | Preço: R$ 249,90 | Quantidade: 15
```

Sem essa sobrescrita, o Java imprimiria algo como `model.Produto@1b6d3586`.

---

## `service/EstoqueService.java`

Contém a **lógica de gerenciamento do estoque**. É a classe que sabe o que
fazer com os produtos e quais operações são permitidas.

### Armazenamento

```java
private ArrayList<Produto> produtos;
private int proximoId;
```

Os produtos ficam em um `ArrayList`, uma lista que cresce automaticamente
conforme novos itens são adicionados. O `<Produto>` (generics) garante que a
lista só aceita objetos do tipo `Produto`.

`proximoId` começa em 1 e aumenta a cada cadastro. Como o próprio serviço gera
o ID, não há como cadastrar dois produtos com o mesmo ID. IDs de produtos
removidos **não são reaproveitados**, para que um ID antigo nunca passe a
apontar para outro produto.

### Métodos

| Método                                   | Descrição |
|------------------------------------------|-----------|
| `cadastrarProduto(nome, preco, quantidade)` | Valida os dados, gera o ID, adiciona o produto e o retorna |
| `listarProdutos()`                       | Retorna a lista de produtos (somente leitura) |
| `buscarProdutoPorId(id)`                 | Retorna o produto com o ID informado, ou `null` |
| `atualizarPreco(id, novoPreco)`          | Altera o preço do produto |
| `registrarEntrada(id, quantidade)`       | Soma a quantidade ao estoque do produto |
| `registrarSaida(id, quantidade)`         | Subtrai a quantidade, se houver estoque suficiente |
| `removerProduto(id)`                     | Remove o produto do estoque e o retorna |

**Lista somente leitura:** `listarProdutos()` devolve
`Collections.unmodifiableList(produtos)`. Quem recebe a lista pode percorrê-la,
mas qualquer tentativa de `add()` ou `remove()` lança erro. Assim, toda
alteração no estoque passa obrigatoriamente pelas regras do serviço.

### Validações

| Situação                               | Mensagem |
|----------------------------------------|----------|
| Nome vazio ou só com espaços           | O nome não pode ficar em branco. |
| Preço zero ou negativo                 | O preço deve ser maior que zero. |
| Quantidade inicial negativa            | A quantidade não pode ser negativa. |
| Entrada ou saída com quantidade ≤ 0    | A quantidade deve ser maior que zero. |
| Saída maior que o estoque disponível   | Estoque insuficiente. Disponível: N. |
| Operação com ID inexistente            | Produto com ID N não encontrado. |

Dados inválidos geram `IllegalArgumentException`, exceção padrão do Java para
argumentos que não fazem sentido. ID inexistente gera
`ProdutoNaoEncontradoException`.

---

## `service/ProdutoNaoEncontradoException.java`

Exceção própria do projeto, que estende `RuntimeException`. Ter um tipo
específico deixa claro **qual** problema aconteceu e permite tratá-lo
separadamente de outros erros, se necessário.

O método privado `obterProduto(id)` do serviço usa essa exceção: ele faz a
busca e, se não encontrar, lança o erro. Assim, cada operação (remover,
atualizar, movimentar) não precisa repetir o teste de `null`.

---

## `view/EstoqueView.java`

Uma **interface** Java: um contrato que diz *o que* uma tela do StockManager
precisa saber fazer, sem dizer *como*.

```java
public interface EstoqueView {
    void iniciar();
    void cadastrar();
    void listar();
    void buscar();
    void atualizarPreco();
    void registrarEntrada();
    void registrarSaida();
    void remover();
}
```

- Os métodos não têm corpo: são apenas a "promessa".
- Métodos de interface são públicos automaticamente.
- Ficam de fora os detalhes específicos do terminal (`lerInteiro()`,
  `exibirMenu()`, etc.), que continuam `private` no `MenuView`.

No `Main`, a variável é declarada com o tipo da interface:

```java
EstoqueView view = new MenuView(estoque, scanner);
```

Assim, uma futura tela gráfica (`JanelaView implements EstoqueView`) poderia
substituir o terminal mudando apenas essa linha.

---

## `view/MenuView.java`

Implementação de `EstoqueView` para o **terminal**. Camada de **interface com o usuário**. Exibe o menu, lê os dados digitados e
mostra os resultados. Ela não contém regras de negócio: apenas repassa os
dados ao `EstoqueService` e exibe o que ele retorna.

```
===== StockManager =====
1. Cadastrar produto
2. Listar produtos
3. Buscar produto por ID
4. Atualizar preço
5. Registrar entrada
6. Registrar saída
7. Remover produto
0. Sair
```

- O menu roda em um laço `do-while` até o usuário escolher `0`.
- O `switch` com `->` (Java 14+) escolhe a ação de cada opção.
- Um único `try/catch` em `executar()` captura os erros do serviço e mostra
  `Erro: <mensagem>`, sem encerrar o programa.
- `lerInteiro()` e `lerDecimal()` repetem a pergunta até receber um número
  válido. O preço aceita vírgula ou ponto (`249,90` ou `249.90`).
- Se a entrada terminar (Ctrl+Z no Windows, Ctrl+D no Linux/macOS), o programa
  encerra de forma limpa.

O `Scanner` é criado no `Main` e passado para a `MenuView` (injeção pelo
construtor). Isso facilita testar o menu com outra fonte de entrada no futuro.

---

## `Main.java`

Ponto de entrada do programa: é o método `public static void main` que a JVM
executa primeiro. Ele apenas monta as peças e inicia o menu:

1. Cria o `EstoqueService`.
2. Cria o `Scanner` para ler o teclado.
3. Cria a `MenuView` com os dois, guarda em uma variável do tipo
   `EstoqueView` e chama `iniciar()`.

### Exemplo de uso

```
Escolha uma opção: 1

Nome: Teclado Mecânico
Preço: 249,90
Quantidade: 15
Cadastrado: ID: 1 | Nome: Teclado Mecânico | Preço: R$ 249,90 | Quantidade: 15

Escolha uma opção: 6

ID: 1
Quantidade de saída: 20
Erro: Estoque insuficiente. Disponível: 15.
```

---

## `.vscode/settings.json`

Configuração do editor:

- `java.project.sourcePaths`: informa que o código-fonte começa em `src/`.
  Sem isso, o VS Code pode inferir a raiz errada e acusar erros como
  *"The declared package does not match the expected package"*.
- `java.project.outputPath`: os arquivos compilados (`.class`) vão para `bin/`.
- `cSpell.words`: palavras em português que o corretor ortográfico deve aceitar.

---

## Como executar

**Pelo VS Code:** abra `src/Main.java` e clique em **Run** acima do método `main`
(requer a extensão *Extension Pack for Java*).

**Pelo terminal** (requer JDK 17 ou superior instalado e no PATH), a partir da
pasta do projeto:

```
javac -encoding UTF-8 -d bin src/Main.java src/model/*.java src/service/*.java src/view/*.java
java -cp bin Main
```

---

## Conceitos aplicados

- **Classes e objetos**: `Produto` é o molde; cada `new Produto(...)` cria um objeto.
- **Encapsulamento**: atributos `private` com acesso por getters e setters, e
  lista de produtos exposta apenas como somente leitura.
- **Pacotes**: organização do código em `model`, `service` e `view`.
- **Coleções**: `ArrayList` com generics para armazenar vários produtos.
- **Sobrescrita de método**: `toString()` com `@Override`.
- **Exceções**: `IllegalArgumentException` e exceção personalizada, tratadas
  com `try/catch`.
- **Entrada de dados**: `Scanner` com tratamento de valores inválidos.
- **Separação de responsabilidades**: dados, regras e interface em camadas distintas.
- **Interfaces e abstração**: `EstoqueView` define o contrato e `MenuView` o
  implementa; o `Main` depende do contrato, não da implementação.

---

## Histórico de versões

- **1.2**: interface `EstoqueView`, implementada pelo `MenuView`.
- **1.1**: menu interativo, remoção, atualização de preço, entrada e saída de
  estoque, validação de dados e ID automático.
- **1.0**: cadastro, listagem e busca de produtos em memória.

---

## Próximas versões

- Persistência de dados em arquivo
- Edição do nome do produto
- Relatórios (valor total do estoque, produtos com estoque baixo)
- Testes automatizados
- Banco de dados
- Interface gráfica ou web
