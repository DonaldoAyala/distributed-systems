
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class NetworkNode {
    private int nodeNumber;
    private volatile boolean canWriteValue;
    private volatile Semaphore canSendSemaphore;
    private volatile int value;
    private ReentrantLock valueLock;
    
    public static final int INITIAL_PORT = 5000;
    public static final int RING_SIZE = 2;
    
    public NetworkNode(int nodeNumber) {
        this.nodeNumber = nodeNumber;
        canSendSemaphore = new Semaphore(1);
        canWriteValue = true;
        valueLock = new ReentrantLock();
    }
    
    public void start () throws InterruptedException {
        ClientThread clientThread = new ClientThread("localhost");
        ServerThread serverThread = new ServerThread();
        clientThread.start();
        serverThread.start();
        clientThread.join();
        serverThread.join();
    }
    
    public void setValue (int newValue) {
        try {
            valueLock.lock();
            this.value = newValue;
            canWriteValue = false;
            canSendSemaphore.release();
        } finally {
            valueLock.unlock();
        }
    }

    public int getValue() {
        int currentValue;
        try {
            valueLock.lock();
            currentValue = this.value;
        } finally {
            valueLock.unlock();
        }
        return currentValue;
    }
    
    public class ClientThread extends Thread {
        private String serverIp;
        private int port;
        private Socket connection;
        private DataOutputStream outputStream;

        public static final int WAIT_SECONDS = 3;

        public ClientThread (String ip) {
            this.serverIp = ip;
            this.port = NetworkNode.INITIAL_PORT + ((nodeNumber + 1) % RING_SIZE);
        }
        
        @Override
        public void run() {
            try {
                connect();
                if (nodeNumber != 0) {
                    canSendSemaphore.acquire();
                }
                while (true) {
                    System.out.println("Waiting for semaphore");
                    canSendSemaphore.acquire();
                    Thread.sleep(500);
                    sendValue(getValue() + 1);
                    canWriteValue = true;
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public void connect () throws InterruptedException, IOException{
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            while (true) {
                try {
                    //this.connection = socketFactory.createSocket(this.serverIp, this.port);
                    this.connection = new Socket(this.serverIp, this.port);
                    break;
                } catch (IOException ioe) {
                    System.out.println("Client " + nodeNumber + " could not connect with the server ");
                    System.out.println("Reattempting connection in " + WAIT_SECONDS + " seconds...");
                    Thread.sleep(WAIT_SECONDS * 1000);
                }
            }

            outputStream = new DataOutputStream(connection.getOutputStream());            
        }

        private void sendValue(int valueToSend) throws IOException {
            outputStream.writeInt(valueToSend);
        }
    }
    
    class ServerThread extends Thread {
        private Socket connection;
        private int port;
        private DataInputStream inputStream;
        
        public ServerThread() {
            this.port = INITIAL_PORT + (nodeNumber);
        }
        
        @Override
        public void run() {
            try {
                connect();
                int readValue;
                while (true) {
                    readValue = waitForValue();
                
                    tryWriteValue(readValue);
                }
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        
        private void connect() throws IOException {
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            //ServerSocket serverSocket = socketFactory.createServerSocket(this.port);
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Waiting for connections on port " + this.port);
            connection = serverSocket.accept();
            
            inputStream = new DataInputStream(connection.getInputStream());
        }
        
        private int waitForValue () throws IOException {
            int receivedValue = inputStream.readInt();
            System.out.println("Value received " + receivedValue);
            if (receivedValue >= 500) {
                receivedValue = 0;
            }
            return receivedValue;
        }
        
        private void tryWriteValue(int readValue) throws InterruptedException {
            while (true) {
                System.out.println("Waiting to write value");
                if (canWriteValue) {
                    setValue(readValue);
                    break;
                }
                Thread.sleep(1000);
            }
        }
    }
}
