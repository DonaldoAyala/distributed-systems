
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
    private double piValue;
    private static ReentrantLock piValueLock;
    
    private final static int NO_SERVERS = 4;
    private final static int WAIT_TIME = 3;
    
    public PiCalculatorClient() {
        servers = new Socket[NO_SERVERS];
        isCalculated = new boolean[NO_SERVERS];
        piValueLock = new ReentrantLock();
        piValue = 0;
    }
    
    private void addToSum(double sumFraction) {
        piValueLock.lock();
        piValue += sumFraction;
        piValueLock.unlock();
    }
    
    public double calculatePi(String[] ipAddresses, int[] ports) throws InterruptedException {
        boolean pendingServers = true;
        while (pendingServers) {
            for (int i = 0; i < NO_SERVERS; i++) {
                if (isCalculated[i]) continue;
                try {
                    servers[i] = new Socket(ipAddresses[i], ports[i]);
                    isCalculated[i] = true;
                    Thread newThread = new PiCalculatorClientThread(servers[i], (short)ports[i], i);
                    newThread.start();
                    newThread.join();
                } catch (Exception e) {
                    System.out.println("Could not connect to server " + (i + 1));
                }
            }
            
            pendingServers = false;
            for (boolean isServerCalculated: isCalculated) {
                pendingServers = pendingServers || !isServerCalculated;
            }
            
            if (pendingServers) {
                System.out.println("Reattempting connection in 3 seconds...");
                Thread.sleep(1000 * WAIT_TIME);
            }
        }
        
        return piValue;
    }
    
    class PiCalculatorClientThread extends Thread {
        int connectionNumber;
        Socket connection;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        
        public PiCalculatorClientThread(Socket connection, short port, int connectionNumber) throws IOException {
            this.connection = connection;
            inputStream = new DataInputStream(connection.getInputStream());
            outputStream = new DataOutputStream(connection.getOutputStream());
            this.connectionNumber = connectionNumber;
        }
        
        @Override
        public void run () {
            try {
                double serverResult = getServerCalculation();
                addToSum(serverResult);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        public double getServerCalculation () throws IOException {
            double serverCalculation = inputStream.readDouble();
            return serverCalculation;
        }
    }
}
