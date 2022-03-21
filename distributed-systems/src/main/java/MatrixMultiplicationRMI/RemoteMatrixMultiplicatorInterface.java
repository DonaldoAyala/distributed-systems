import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteMatrixMultiplicatorInterface extends Remote {
    public float[][] multiplyTransposedMatrixes(float[][] A, float[][] B) throws RemoteException;
}
