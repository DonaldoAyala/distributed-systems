
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;


public class MatrixMultiplicatorServerNode {
    public static final String OBJECT_NAME = "matrixMultiplicator";
    
    public static void main(String[] args) throws RemoteException, MalformedURLException {
        String url = "rmi://localhost/" + OBJECT_NAME;
        RemoteMatrixMultiplicator matrixMultiplicator = new RemoteMatrixMultiplicator();
        
        Naming.rebind(url, matrixMultiplicator);
        
        System.out.println("Object binded");
    }
}
