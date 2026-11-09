import model.Produto;
import service.EstoqueService;

public class Main {

    public static void main(String[] args) {
        EstoqueService estoque = new EstoqueService();

        estoque.cadastrarProduto(new Produto(1, "Teclado Mecânico", 249.90, 15));
        estoque.cadastrarProduto(new Produto(2, "Mouse Sem Fio", 89.90, 30));
        estoque.cadastrarProduto(new Produto(3, "Monitor 24\"", 899.00, 8));

        System.out.println("=== Produtos cadastrados ===");
        estoque.listarProdutos();

        System.out.println();
        System.out.println("=== Busca por ID ===");
        exibirBusca(estoque, 2);
        exibirBusca(estoque, 99);
    }

    private static void exibirBusca(EstoqueService estoque, int id) {
        Produto produto = estoque.buscarProdutoPorId(id);
        if (produto != null) {
            System.out.println("Encontrado: " + produto);
        } else {
            System.out.println("Produto com ID " + id + " não encontrado.");
        }
    }
}
