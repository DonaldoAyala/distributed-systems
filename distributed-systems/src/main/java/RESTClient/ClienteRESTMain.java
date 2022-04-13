
import java.io.IOException;

public class ClienteRESTMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Ejecuta java ClienteRestMain <ip_servidor> <puerto> <ruta>");
            System.exit(0);
        }
        MenuClienteREST menu = new MenuClienteREST();
        menu.inicializarClienteREST(args[0], args[1], args[2]);
        
        menu.ejecutarMenu();
    }
}
