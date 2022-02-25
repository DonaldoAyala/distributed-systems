
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiThreadServer {
    private ServerSocket serverSocket;
    private boolean isRunning;
    
    public void start(short port) throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        Socket newClientConnection;
        Thread newClientThread;
        
        System.out.println("Server waiting for connections on port " + port);
        while(isRunning) {
            newClientConnection = serverSocket.accept();
            newClientThread = new ServerThread(newClientConnection);
            newClientThread.start();
            System.out.println("New connection established");
        }
        
        serverSocket.close();
    }
    
    public void stop() throws IOException{
        isRunning = false;
    }
    
    class ServerThread extends Thread {
        private Socket clientConnection;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        
        public ServerThread(Socket clientConnection) throws IOException {
            this.clientConnection = clientConnection;
            inputStream = new DataInputStream(clientConnection.getInputStream());
            outputStream = new DataOutputStream(clientConnection.getOutputStream());
        }
        
        @Override
        public void run() {
            try {
                testReceiveDoublesInBuffer();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        public double[] testReceiveDoublesInBuffer() throws IOException {
            int numberOfDoubles = inputStream.readInt();
            int readBytes;
            double[] doubles = new double[numberOfDoubles];
            byte[] rawDataBuffer = new byte[numberOfDoubles * Double.SIZE];

            int position = 0;
            int remainingBytes = numberOfDoubles * Double.SIZE;
            while (remainingBytes > 0) {
                readBytes = inputStream.read(rawDataBuffer, position, remainingBytes);
                position += readBytes;
                remainingBytes -= readBytes;
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(rawDataBuffer);
            for (int i = 0; i < numberOfDoubles; i++) {
                doubles[i] = byteBuffer.getDouble();
            }

            System.out.println("All floats received");
            return doubles;
        }
    }
    
    
}
