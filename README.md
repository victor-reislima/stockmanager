# StockManager

Sistema de gerenciamento de estoque desenvolvido em Java puro, com foco em
orientação a objetos e organização em camadas.

Versão atual: **1.0**. Cadastro, listagem e busca de produtos em memória.

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
│   │   └── EstoqueService.java  Regras de negócio do estoque
│   └── view/                  Reservada para a interface com o usuário
└── README.md                  Este documento
```

O projeto segue uma separação em camadas:

| Camada    | Pacote    | Responsabilidade                                   |
|-----------|-----------|----------------------------------------------------|
| Modelo    | `model`   | Representar os dados (o que é um produto)          |
| Serviço   | `service` | Aplicar as regras de negócio (o que fazer com eles)|
| Visão     | `view`    | Interagir com o usuário (a ser implementada)       |

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
fazer com os produtos.

### Armazenamento

```java
private ArrayList<Produto> produtos;
```

Os produtos ficam em um `ArrayList`, uma lista que cresce automaticamente
conforme novos itens são adicionados. Diferente de um array comum
(`Produto[]`), não é preciso definir o tamanho antecipadamente. O `<Produto>`
(generics) garante que a lista só aceita objetos do tipo `Produto`, e erros
desse tipo são detectados já na compilação.

A lista é criada no construtor, então cada `EstoqueService` começa com um
estoque vazio.

### Métodos

| Método                             | Descrição                                                       |
|------------------------------------|-----------------------------------------------------------------|
| `cadastrarProduto(Produto produto)`| Adiciona o produto à lista com `produtos.add()`                 |
| `listarProdutos()`                 | Exibe todos os produtos, ou avisa se o estoque estiver vazio    |
| `buscarProdutoPorId(int id)`       | Percorre a lista e retorna o produto com o ID informado, ou `null` se não houver nenhum |

A busca usa um laço `for-each` (`for (Produto produto : produtos)`), que
percorre cada elemento da lista sem a necessidade de controlar índices.

---

## `Main.java`

Ponto de entrada do programa: é o método `public static void main` que a JVM
executa primeiro. Nesta versão, ele demonstra as funcionalidades:

1. Cria uma instância de `EstoqueService`.
2. Cadastra três produtos.
3. Lista todos os produtos.
4. Busca um produto existente (ID 2) e um inexistente (ID 99).

O método auxiliar `exibirBusca()` evita repetir código, tratando os dois
casos possíveis do resultado da busca (encontrado ou `null`).

### Saída esperada

```
=== Produtos cadastrados ===
ID: 1 | Nome: Teclado Mecânico | Preço: R$ 249,90 | Quantidade: 15
ID: 2 | Nome: Mouse Sem Fio | Preço: R$ 89,90 | Quantidade: 30
ID: 3 | Nome: Monitor 24" | Preço: R$ 899,00 | Quantidade: 8

=== Busca por ID ===
Encontrado: ID: 2 | Nome: Mouse Sem Fio | Preço: R$ 89,90 | Quantidade: 30
Produto com ID 99 não encontrado.
```

---

## `view/`

Pasta reservada para a camada de interface. Na próxima versão, ela receberá o
menu interativo no terminal, mantendo a interação com o usuário separada da
lógica do `EstoqueService`.

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
javac -encoding UTF-8 -d bin src/Main.java src/model/Produto.java src/service/EstoqueService.java
java -cp bin Main
```

---

## Conceitos aplicados

- **Classes e objetos**: `Produto` é o molde; cada `new Produto(...)` cria um objeto.
- **Encapsulamento**: atributos `private` com acesso por getters e setters.
- **Pacotes**: organização do código em `model`, `service` e `view`.
- **Coleções**: `ArrayList` com generics para armazenar vários produtos.
- **Sobrescrita de método**: `toString()` com `@Override`.
- **Separação de responsabilidades**: dados, regras e interface em camadas distintas.

---

## Próximas versões

- Remoção de produtos
- Atualização de preços e quantidades
- Controle de entrada e saída de estoque
- Validação de dados
- Menu interativo no terminal
- Persistência de dados em arquivo
- Banco de dados
- Interface gráfica ou web
- Testes automatizados
