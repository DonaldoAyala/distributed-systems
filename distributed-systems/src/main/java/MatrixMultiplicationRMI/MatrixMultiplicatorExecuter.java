
public class MatrixMultiplicatorExecuter {
    public static void main(String[] args) throws Exception {
        final int matrixSize = 4;
        final int numberOfNodes = 1;
        
        DistributedMatrixMultiplicator matrixMultiplicator = new DistributedMatrixMultiplicator(numberOfNodes);
        float[][] A, B;
        
        A = new float[matrixSize][matrixSize];
        B = new float[matrixSize][matrixSize];
        
        
        String localhostURL = "rmi://localhost/" + MatrixMultiplicatorServerNode.OBJECT_NAME;
        
        String[] urls = {localhostURL, localhostURL, localhostURL, localhostURL};
        
        matrixMultiplicator.initializeMatrixes(A, B);
        
        matrixMultiplicator.printMatrix(A);
        matrixMultiplicator.printMatrix(B);
        
        float[][] result = matrixMultiplicator.multiplicateMatrixes(A, B, urls);
        
        
        matrixMultiplicator.printMatrix(result);
        
        System.out.println("Checksum: " + matrixMultiplicator.calculateChecksum(result));
    }
}
