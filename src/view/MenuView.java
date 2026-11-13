package view;

import java.util.List;
import java.util.Scanner;
import model.Produto;
import service.EstoqueService;
import service.ProdutoNaoEncontradoException;

public class MenuView implements EstoqueView {

    private final EstoqueService estoque;
    private final Scanner scanner;

    public MenuView(EstoqueService estoque, Scanner scanner) {
        this.estoque = estoque;
        this.scanner = scanner;
    }

    @Override
    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            System.out.println();
            executar(opcao);
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== StockManager =====");
        System.out.println("1. Cadastrar produto");
        System.out.println("2. Listar produtos");
        System.out.println("3. Buscar produto por ID");
        System.out.println("4. Atualizar preço");
        System.out.println("5. Registrar entrada");
        System.out.println("6. Registrar saída");
        System.out.println("7. Remover produto");
        System.out.println("0. Sair");
    }

    private void executar(int opcao) {
        // As regras ficam no service; aqui apenas exibimos o erro ao usuário.
        try {
            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> buscar();
                case 4 -> atualizarPreco();
                case 5 -> registrarEntrada();
                case 6 -> registrarSaida();
                case 7 -> remover();
                case 0 -> System.out.println("Até logo!");
                default -> System.out.println("Opção inválida.");
            }
        } catch (IllegalArgumentException | ProdutoNaoEncontradoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    @Override
    public void cadastrar() {
        String nome = lerTexto("Nome: ");
        double preco = lerDecimal("Preço: ");
        int quantidade = lerInteiro("Quantidade: ");
        Produto produto = estoque.cadastrarProduto(nome, preco, quantidade);
        System.out.println("Cadastrado: " + produto);
    }

    @Override
    public void listar() {
        List<Produto> produtos = estoque.listarProdutos();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        for (Produto produto : produtos) {
            System.out.println(produto);
        }
    }

    @Override
    public void buscar() {
        int id = lerInteiro("ID: ");
        Produto produto = estoque.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.println("Encontrado: " + produto);
        } else {
            System.out.println("Produto com ID " + id + " não encontrado.");
        }
    }

    @Override
    public void atualizarPreco() {
        int id = lerInteiro("ID: ");
        double preco = lerDecimal("Novo preço: ");
        System.out.println("Atualizado: " + estoque.atualizarPreco(id, preco));
    }

    @Override
    public void registrarEntrada() {
        int id = lerInteiro("ID: ");
        int quantidade = lerInteiro("Quantidade de entrada: ");
        System.out.println("Atualizado: " + estoque.registrarEntrada(id, quantidade));
    }

    @Override
    public void registrarSaida() {
        int id = lerInteiro("ID: ");
        int quantidade = lerInteiro("Quantidade de saída: ");
        System.out.println("Atualizado: " + estoque.registrarSaida(id, quantidade));
    }

    @Override
    public void remover() {
        int id = lerInteiro("ID: ");
        System.out.println("Removido: " + estoque.removerProduto(id));
    }

    // ---- Leitura de dados: repete a pergunta até receber um valor válido ----

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return proximaLinha();
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            String entrada = lerTexto(mensagem).trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    // Aceita vírgula ou ponto como separador decimal (ex.: 249,90 ou 249.90).
    private double lerDecimal(String mensagem) {
        while (true) {
            String entrada = lerTexto(mensagem).trim().replace(',', '.');
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido (ex.: 249,90).");
            }
        }
    }

    // Se a entrada acabar (Ctrl+Z / Ctrl+D), encerra o programa de forma limpa.
    private String proximaLinha() {
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada encerrada. Até logo!");
            System.exit(0);
        }
        return scanner.nextLine();
    }
}
