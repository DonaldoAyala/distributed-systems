
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class PiCalculatorServerNegative {
    public static void runServer(int serverNumber) throws Exception {
        ServerSocket server = new ServerSocket((short)(5000 + serverNumber));
        Socket connection = server.accept();
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        
        outputStream.writeDouble(3.5);
        
        connection.close();
    }
}
