
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PiCalculatorClient {
    private Socket[] servers;
    private boolean[] isCalculated;
    private double[] serverResults;
    private double piValue;
    private static ReentrantLock piValueLock;
    
    private static int NO_SERVERS = 4;
    
    public PiCalculatorClient() {
        servers = new Socket[NO_SERVERS];
        isCalculated = new boolean[NO_SERVERS];
        serverResults = new double[NO_SERVERS];
        piValue = 0;
    }
    
    private void addToSum(double sumFraction) {
        piValueLock.lock();
        piValue += sumFraction;
        piValueLock.unlock();
    }
    
    public double calculatePi(String[] ipAddresses, int[] ports) {
        try {
            for (int i = 0; i < NO_SERVERS; i++) {
                if (isCalculated[i]) continue;
                try {
                    servers[i] = new Socket(ipAddresses[i], ports[i]);
                    isCalculated[i] = true;
                    (new PiCalculatorClientThread(servers[i], i)).start();
                    break;
                } catch (Exception e) {
                    System.out.println("Could not connect to server " + i + 1);
                    throw e;
                }
            }
        } catch (Exception e) {
            System.out.println("Reattempting connection in 3 seconds...");
        }
        
        double totalSum = 0;
        for (int i = 0; i < serverResults.length; i++) {
            totalSum += serverResults[i];
        }
        
        return totalSum;
    }
    
    class PiCalculatorClientThread extends Thread {
        int connectionNumber;
        Socket connection;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        
        public PiCalculatorClientThread(Socket connection, int connectionNumber) throws IOException {
            this.connection = connection;
            inputStream = new DataInputStream(connection.getInputStream());
            outputStream = new DataOutputStream(connection.getOutputStream());
            this.connectionNumber = connectionNumber;
        }
        
        @Override
        public void run () {
            try {
                double serverResult = getServerCalculation();
                
            } catch (IOException ioe) {
                
            }
        }
        
        public double getServerCalculation () throws IOException {
            double serverCalculation = inputStream.readDouble();
            return serverCalculation;
        }
    }
}
