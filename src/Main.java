import java.util.Scanner;
import service.EstoqueService;
import view.MenuView;
import view.EstoqueView;

public class Main {

    public static void main(String[] args) {
        EstoqueService estoque = new EstoqueService();
        Scanner scanner = new Scanner(System.in);

        EstoqueView view = new MenuView(estoque, scanner);
        view.iniciar();

        scanner.close();
    }
}
