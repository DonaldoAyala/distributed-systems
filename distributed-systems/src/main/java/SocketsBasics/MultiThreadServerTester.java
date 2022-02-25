
import java.io.IOException;

public class MultiThreadServerTester {
    public static void main(String[] args) {
        MultiThreadServer server = new MultiThreadServer();
        try {
            server.start((short)13092);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
