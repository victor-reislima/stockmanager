package service;

// Lançada quando uma operação exige um produto que não está no estoque.
public class ProdutoNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProdutoNaoEncontradoException(int id) {
        super("Produto com ID " + id + " não encontrado.");
    }
}
