
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientConnection;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    
    public void start(short port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server waiting for connections on port " + port);
        clientConnection = serverSocket.accept();
        System.out.println("New connection established");
        inputStream = new DataInputStream(clientConnection.getInputStream());
        outputStream = new DataOutputStream(clientConnection.getOutputStream());
    }
    
    public void stop() throws IOException{
        serverSocket.close();
    }
    
    public double[] testReceiveDoublesOneByOne() throws IOException {
        int numberOfDoubles = inputStream.readInt();
        double[] doubles = new double[numberOfDoubles];
        
        for (int i = 0; i < numberOfDoubles; i++) {
            doubles[i] = inputStream.readDouble();
        }
        System.out.println("All floats received");
        return doubles;
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
