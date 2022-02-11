
import java.io.BufferedInputStream;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Client {
    private Socket clientSocket;
    
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    public void connect(String server, short port) throws IOException {
        clientSocket = new Socket(server, port);
        
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
    }
    
    public void connectWithRetries(String server, short port) {
        try {
            while (true) {
                try {
                    clientSocket = new Socket(server, port);
                    inputStream = new DataInputStream(clientSocket.getInputStream());
                    outputStream = new DataOutputStream(clientSocket.getOutputStream());
                    break;
                } catch (IOException e) {
                    System.out.println("Could not establish connection with the server.\nRetrying in 3 seconds.");
                    Thread.sleep(3000);
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    public void closeConnection() throws IOException {
        clientSocket.close();
    }
    
    public void testSendDoublesOneByOne(int numberOfDoubles) throws IOException {
        outputStream.writeInt(numberOfDoubles);
        
        for (int i = 0; i < numberOfDoubles; i++) {
            outputStream.writeDouble((double)i);
        }
    }
    
    public void testSendDoublesInBuffer(int numberOfDoubles) throws IOException {
        System.out.println("Sending data");
        outputStream.writeInt(numberOfDoubles);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(numberOfDoubles * Double.SIZE);
        for(int i = 0; i < numberOfDoubles; i++) {
            byteBuffer.putDouble((double)i);
        }
        
        byte[] rawData = byteBuffer.array();
        outputStream.write(rawData);
    }
}
