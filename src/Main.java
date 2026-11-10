import java.util.Scanner;
import service.EstoqueService;
import view.MenuView;

public class Main {

    public static void main(String[] args) {
        EstoqueService estoque = new EstoqueService();
        Scanner scanner = new Scanner(System.in);

        MenuView menu = new MenuView(estoque, scanner);
        menu.iniciar();

        scanner.close();
    }
}
