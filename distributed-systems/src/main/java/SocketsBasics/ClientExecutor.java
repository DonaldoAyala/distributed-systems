
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ClientExecutor {
    public static void runComparison() throws InterruptedException{
        Client client = new Client();
        
        try {
            client.connect("localhost", (short)13092);
            long initialTime = System.currentTimeMillis();
            client.testSendDoublesOneByOne(10000);
            long finalTime = System.currentTimeMillis();
            client.closeConnection();
            System.out.println("Transmission time using writeDouble: " + (finalTime - initialTime));
            
            Thread.sleep(1000);
            
            client.connect("localhost", (short)13092);
            initialTime = System.currentTimeMillis();
            client.testSendDoublesInBuffer(10000);
            finalTime = System.currentTimeMillis();
            client.closeConnection();
            System.out.println("Transmission time using ByteBuffer: " + (finalTime - initialTime));
            
        } catch (IOException ex) {
            System.out.println("Connection attempt failed.");
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.connectWithRetries("localhost", (short)13092);
        Thread.sleep(1000);
        client.testSendDoublesInBuffer(100000);
    }
}
