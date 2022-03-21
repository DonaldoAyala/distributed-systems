
public class MatrixMultiplicatorExecuter {
    public static void main(String[] args) throws Exception {
        DistributedMatrixMultiplicator matrixMultiplicator = new DistributedMatrixMultiplicator(3);
        float[][] A, B;
        A = new float[3][3];
        B = new float[3][3];
        matrixMultiplicator.initializeMatrixes(A, B);
        
        matrixMultiplicator.multiplicateMatrixes(A, B);
        
        System.out.println("Done");
        
    }
}
