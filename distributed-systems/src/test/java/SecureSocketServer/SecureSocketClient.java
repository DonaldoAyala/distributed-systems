
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class SecureSocketClient {
    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore","keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
        try {
            SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket conexion = cliente.createSocket("localhost",50000);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            salida.writeDouble(123456789.123456789);
            System.out.println(123456789.123456789);
            conexion.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
    }
}
