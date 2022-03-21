
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
    private volatile short value;
    private volatile boolean isRunning;
    private ReentrantLock valueLock;
    
    public static final int INITIAL_PORT = 5000;
    public static final int RING_SIZE = 2;
    public static final int STOP_VALUE = 500;
    
    public NetworkNode(int nodeNumber) {
        this.nodeNumber = nodeNumber;
        canSendSemaphore = new Semaphore(1);
        canWriteValue = true;
        valueLock = new ReentrantLock();
        isRunning = true;
    }
    
    public void start () throws InterruptedException {
        ClientThread clientThread = new ClientThread("localhost");
        ServerThread serverThread = new ServerThread();
        serverThread.start();
        clientThread.start();
        clientThread.join();
        serverThread.join();
    }
    
    public void setValue (short newValue) {
        try {
            valueLock.lock();
            this.value = newValue;
            canWriteValue = false;
            canSendSemaphore.release();
        } finally {
            valueLock.unlock();
        }
    }

    public short getValue() {
        short currentValue;
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
                while (isRunning) {
                    //System.out.println("Waiting for token");
                    canSendSemaphore.acquire();
                    short valueToSend = getValue();
                    sendValue((short)(valueToSend >= STOP_VALUE ? valueToSend : valueToSend + 1));
                    canWriteValue = true;
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (IOException ioe) {
                System.exit(0);
            }
        }

        public void connect () throws InterruptedException, IOException{
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            while (true) {
                try {
                    this.connection = socketFactory.createSocket(this.serverIp, this.port);
                    break;
                } catch (IOException ioe) {
                    System.out.println("Client " + nodeNumber + " could not connect with the server ");
                    System.out.println("Reattempting connection in " + WAIT_SECONDS + " seconds...");
                    Thread.sleep(WAIT_SECONDS * 1000);
                }
            }
            outputStream = new DataOutputStream(connection.getOutputStream());
        }

        private void sendValue(short valueToSend) throws IOException {
            outputStream.writeShort(valueToSend);
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
                short readValue;
                while (isRunning) {
                    readValue = waitForValue();
                    tryWriteValue(readValue);
                }
                
            } catch (IOException ioe) {
                try {
                    tryWriteValue((short)-1);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                isRunning = false;
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        
        private void connect() throws IOException {
            SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = socketFactory.createServerSocket(this.port);
            System.out.println("Waiting for connections on port " + this.port);
            
            connection = serverSocket.accept();
            
            inputStream = new DataInputStream(connection.getInputStream());
        }
        
        private short waitForValue () throws IOException {
            short receivedValue = inputStream.readShort();
            System.out.println("Received value " + receivedValue);
            if (receivedValue >= STOP_VALUE) {
                System.out.println("Limit reached, stopping execution");
                isRunning = false;
                try {
                    tryWriteValue(receivedValue);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            return receivedValue;
        }
        
        private void tryWriteValue(short readValue) throws InterruptedException {
            while (true) {
                //System.out.println("Waiting to write value");
                if (canWriteValue) {
                    setValue(readValue);
                    break;
                }
                Thread.sleep(1000);
            }
        }
    }
}
