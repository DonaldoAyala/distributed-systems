
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerExecutor {
    public static void main(String[] args) {
        
        Server server = new Server();
        int numberOfDoublesToSend = 10000;
        double[] receivedDoubles;
        
        try {
            server.start((short)13092);
            long initialTime = System.currentTimeMillis();
            receivedDoubles = server.testReceiveDoublesOneByOne();
            long finalTime = System.currentTimeMillis();
            server.stop();
            System.out.println("Transmission time using writeDouble: " + (finalTime - initialTime));
            
            for (int i = 0; i < receivedDoubles.length; i += 1000) {
                System.out.print(receivedDoubles[i] + " ");
            }
            System.out.println("");
            
            server.start((short)13092);
            initialTime = System.currentTimeMillis();
            receivedDoubles = server.testReceiveDoublesInBuffer();
            finalTime = System.currentTimeMillis();
            server.stop();
            
            System.out.println(receivedDoubles.length);
            
            for (int i = 0; i < receivedDoubles.length; i += 1000) {
                System.out.print(receivedDoubles[i] + " ");
            }
            System.out.println("");
            
            System.out.println("Transmission time using writeDouble: " + (finalTime - initialTime));
        } catch (IOException ex) {
            System.out.println("Error while starting the connection");
        }
    }
}
