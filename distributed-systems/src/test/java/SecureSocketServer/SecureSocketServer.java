
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class SecureSocketServer {
    public static void main(String[] args) {
        try {
            
            SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket socket_servidor = socket_factory.createServerSocket(50000);
            Socket conexion = socket_servidor.accept();
            
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            double x = entrada.readDouble();
            System.out.println(x);
            conexion.close();
            
            
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
    }
}
