
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class RemoteMatrixMultiplicator extends UnicastRemoteObject implements RemoteMatrixMultiplicatorInterface {

    public RemoteMatrixMultiplicator() throws RemoteException {
        super();
    }
    
    public float[][] multiplyTransposedMatrixes(float[][] A, float[][] B) throws RemoteException {
        // Assuming A: nxm and B: mxr (Before transposed)
        final int n = A.length;
        final int m = A[0].length;
        final int r = B.length;
        float[][] result = new float[n][r];
        
        System.out.println("n " + n + " m " + m + " r " + r);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < r; j++) {
                for (int k = 0; k < m; k++) {
                    result[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        
        return result;
    }
}
