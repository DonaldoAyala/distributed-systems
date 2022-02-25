
package RingSocketTopology;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLSocketFactory;

public class NetworkNode {
    private int nodeNumber;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    private volatile Semaphore canSendSemaphore;
    
    public static final int INITIAL_PORT = 5000;
    
    public NetworkNode(int nodeNumber) {
        this.nodeNumber = nodeNumber;
        canSendSemaphore = new Semaphore(1);
    }
    
    public void start () {
        
    }
    
    
    public class ClientThread extends Thread {
        private String serverIp;
        private int port;
        private int clientNumber;
        private Socket connection;
        private DataOutputStream outputStream;
        private volatile boolean canWriteValue;

        private volatile int value;
        private ReentrantLock valueLock;

        public static final int WAIT_SECONDS = 3;

        public ClientThread (String ip, int clientNumber) {
            this.clientNumber = clientNumber;
            this.serverIp = ip;
            this.port = NetworkNode.INITIAL_PORT + (clientNumber * 2);
            this.canWriteValue = true;
            
        }
        
        @Override
        public void run() {
            try {
                connect();
                canSendSemaphore.acquire();
                while (true) {
                    canSendSemaphore.acquire();
                    sendValue(getValue());
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
                    this.connection = socketFactory.createSocket(serverIp, port);
                    break;
                } catch (IOException ioe) {
                    System.out.println("Client " + clientNumber + " could not connect with the server ");
                    System.out.println("Reattempting connection in " + WAIT_SECONDS + " seconds...");
                    Thread.sleep(WAIT_SECONDS * 1000);
                }
            }

            outputStream = new DataOutputStream(connection.getOutputStream());            
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
        
        public boolean canWriteValue () {
            return canWriteValue;
        }
        
        private void sendValue(int valueToSend) throws IOException {
            outputStream.writeInt(valueToSend);
        }
    }
}
