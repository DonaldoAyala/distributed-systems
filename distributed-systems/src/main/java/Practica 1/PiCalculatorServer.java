
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class PiCalculatorServer {
    private ServerSocket server;
    private Socket connection;
    private DataOutputStream dataOutputStream;
    private int serverNumber;
    
    public final static int SUM_TERMS = 1000000;
    
    public PiCalculatorServer(int serverNumber) throws IOException {
        this.serverNumber = serverNumber;
        server = new ServerSocket((short)(PiCalculatorClient.CLIENT_PORT + serverNumber));
    }
    
    private double calculatePiSumFraction () {
        double piSumFraction = 0;
        
        for(int i = 0; i < SUM_TERMS; i++){
            piSumFraction += 4.0 / ( 8.0 * i + 2.0 * ((double)this.serverNumber - 2.0) + 3.0);
        }
        
        if (this.serverNumber % 2 == 0) {
            piSumFraction *= -1.0;
        }
        
        return piSumFraction;
    }
    
    public void startServer() throws IOException {
        while (true) {
            System.out.println("Waiting for connections on port " + server.getLocalPort());
            connection = server.accept();
            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            double piSumFraction = calculatePiSumFraction();
            System.out.println("Calculated pi sum fraction " + piSumFraction);
            dataOutputStream.writeDouble(piSumFraction);

            connection.close();
        }
        
    }
}
