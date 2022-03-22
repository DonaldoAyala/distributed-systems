
public class MatrixMultiplicatorExecuter {
    public static void main(String[] args) throws Exception {
        final int matrixSize = 4000;
        final int numberOfNodes = 4;
        
        DistributedMatrixMultiplicator matrixMultiplicator = new DistributedMatrixMultiplicator(numberOfNodes);
        float[][] A, B;
        
        A = new float[matrixSize][matrixSize];
        B = new float[matrixSize][matrixSize];
        
        
        String localhostURL = "rmi://localhost/" + MatrixMultiplicatorServerNode.OBJECT_NAME;
        
        String[] urls = {
            "rmi://10.0.0.5/matrixMultiplicator", 
            "rmi://10.0.0.6/matrixMultiplicator",
            "rmi://10.0.0.7/matrixMultiplicator",
            "rmi://10.0.0.8/matrixMultiplicator"};
        
        matrixMultiplicator.initializeMatrixes(A, B);
        
        float[][] result = matrixMultiplicator.multiplicateMatrixes(A, B, urls);
        
        
        if (matrixSize <= 8) {
            System.out.println("Matrix A");
            matrixMultiplicator.printMatrix(A);
            System.out.println("Matrix B");
            matrixMultiplicator.printMatrix(B);
            System.out.println("AxB result");
            matrixMultiplicator.printMatrix(result);
        }
        
        System.out.println("Checksum: " + matrixMultiplicator.calculateChecksum(result));
    }
}
