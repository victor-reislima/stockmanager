package view;

// Contrato: toda tela do StockManager precisa saber fazer estas operações.
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
