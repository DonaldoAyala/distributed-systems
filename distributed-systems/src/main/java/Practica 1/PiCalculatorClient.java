
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class PiCalculatorClient {
    private Socket[] servers;
    private double piValue;
    private static ReentrantLock piValueLock;
    
    public final static int NO_SERVERS = 4;
    public final static int WAIT_TIME = 3;
    public final static int CLIENT_PORT = 5000;
    
    public PiCalculatorClient() {
        piValueLock = new ReentrantLock();
        piValue = 0;
    }
    
    private void addToSum(double sumFraction) {
        try {
            piValueLock.lock();
            System.out.println("Adding " + sumFraction);
            piValue += sumFraction;
        } finally {
            piValueLock.unlock();
        }
    }
    
    public double calculatePi(String[] ipAddresses) throws InterruptedException, IOException {
        Thread[] serverConnectionThreads = new Thread[NO_SERVERS];
        
        for (int i = 0; i < NO_SERVERS; i++) {
            serverConnectionThreads[i] = new PiCalculatorClientThread(ipAddresses[i], i + 1);
            serverConnectionThreads[i].start();
        }
        
        for (int i = 0; i < NO_SERVERS; i++) {
            serverConnectionThreads[i].join();
        }
        
        return piValue;
    }
    
    class PiCalculatorClientThread extends Thread {
        int connectionNumber;
        short port;
        String serverIp;
        Socket connection;
        DataInputStream inputStream;
        DataOutputStream outputStream;
        
        public PiCalculatorClientThread(String ip, int connectionNumber) throws IOException, InterruptedException {
            this.serverIp = ip;
            this.port = (short)(PiCalculatorClient.CLIENT_PORT + connectionNumber);
            this.connectionNumber = connectionNumber;
        }
        
        public void connect() throws InterruptedException, IOException {
            while (true) {
                    try {
                        this.connection = new Socket(serverIp, (short)(port));
                        break;
                    } catch (IOException ioe) {
                        System.out.println("Could not connect with the server " + connectionNumber);
                        System.out.println("Reattempting connection in " + WAIT_TIME + " seconds...");
                        Thread.sleep(WAIT_TIME*1000);
                    }
                }

            inputStream = new DataInputStream(connection.getInputStream());
            outputStream = new DataOutputStream(connection.getOutputStream());
        }
        
        @Override
        public void run () {
            try {
                connect();
                
                double serverResult = getServerCalculation();
                addToSum(serverResult);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
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
