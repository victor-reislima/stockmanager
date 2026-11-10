package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Produto;

public class EstoqueService {

    private ArrayList<Produto> produtos;
    private int proximoId;

    public EstoqueService() {
        produtos = new ArrayList<>();
        proximoId = 1;
    }

    // O ID é gerado aqui, garantindo que nunca existam dois produtos com o mesmo ID.
    public Produto cadastrarProduto(String nome, double preco, int quantidade) {
        validarNome(nome);
        validarPreco(preco);
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }

        Produto produto = new Produto(proximoId, nome.trim(), preco, quantidade);
        produtos.add(produto);
        proximoId++;
        return produto;
    }

    // Retorna uma visão somente leitura: quem chama não consegue alterar a lista.
    public List<Produto> listarProdutos() {
        return Collections.unmodifiableList(produtos);
    }

    // Retorna o produto com o ID informado, ou null se não for encontrado.
    public Produto buscarProdutoPorId(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    public Produto removerProduto(int id) {
        Produto produto = obterProduto(id);
        produtos.remove(produto);
        return produto;
    }

    public Produto atualizarPreco(int id, double novoPreco) {
        validarPreco(novoPreco);
        Produto produto = obterProduto(id);
        produto.setPreco(novoPreco);
        return produto;
    }

    public Produto registrarEntrada(int id, int quantidade) {
        validarMovimentacao(quantidade);
        Produto produto = obterProduto(id);
        produto.setQuantidade(produto.getQuantidade() + quantidade);
        return produto;
    }

    public Produto registrarSaida(int id, int quantidade) {
        validarMovimentacao(quantidade);
        Produto produto = obterProduto(id);
        if (quantidade > produto.getQuantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: "
                    + produto.getQuantidade() + ".");
        }
        produto.setQuantidade(produto.getQuantidade() - quantidade);
        return produto;
    }

    // Como buscarProdutoPorId, mas lança exceção em vez de retornar null.
    private Produto obterProduto(int id) {
        Produto produto = buscarProdutoPorId(id);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException(id);
        }
        return produto;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ficar em branco.");
        }
    }

    private void validarPreco(double preco) {
        if (!(preco > 0) || Double.isInfinite(preco)) { // rejeita também NaN e infinito
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
    }

    private void validarMovimentacao(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }
}
