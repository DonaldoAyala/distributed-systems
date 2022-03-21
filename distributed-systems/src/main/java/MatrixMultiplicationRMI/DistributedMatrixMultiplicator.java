
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class DistributedMatrixMultiplicator {
    volatile private float [][][][] matrixFragments;
    private int divisions;
    
    public DistributedMatrixMultiplicator(int divisions) {
        matrixFragments = new float[divisions][divisions][][];
        this.divisions = divisions;
    }
    
    public float[][] multiplicateMatrixes(float[][] A, float[][] B) throws InterruptedException {
        // A: nxm, B: mxr
        final int n = A.length;
        final int m = A[0].length;
        final int r = B[0].length;
        
        float[][] transposedB = getTransposedMatrix(B);
        
        float[][][] ASubmatrixes = new float[divisions][][];
        float[][][] BSubmatrixes = new float[divisions][][];
        
        final int AJump = n / divisions;
        final int BJump = r / divisions;
        
        for (int submatrix = 0; submatrix < divisions; submatrix++) {
            ASubmatrixes[submatrix] = getSubmatrix(A, submatrix * AJump, 0, (submatrix + 1) * AJump, m);
            BSubmatrixes[submatrix] = getSubmatrix(transposedB, submatrix * BJump, 0, (submatrix + 1) * BJump, m);
            printMatrix(BSubmatrixes[submatrix]);
        }
        
        
        Thread[][] threads = new Thread[divisions][divisions];
        for (int i = 0; i < divisions; i++) {
            for (int j = 0; j < divisions; j++) {
                threads[i][j] = new MatrixCalculatorClientThread("url", ASubmatrixes[i], BSubmatrixes[j], i, j);
                threads[i][j].start();
            }
        }
        
        for (int i = 0; i < divisions; i++) {
            for (int j = 0; j < divisions; j++) {
                threads[i][j].join();
            }
        }
                
        float[][] result = new float[n][r];
        
        System.out.println("submatrix diensions " + matrixFragments[0][0].length + "," + matrixFragments[0][0][0].length);
        
        for (int row = 0; row < divisions; row++) {
            for (int col = 0; col < divisions; col++) {
                for (int i = 0; i < matrixFragments[row][col].length; i++) {
                    for (int j = 0; j < matrixFragments[row][col][0].length; j++) {
                        //System.out.println(result[row * AJump + i][col * BJump + j]);
                        System.out.println("row " + (row * AJump + i) + " col " + (i + j));
                        //System.out.println(matrixFragments[row][col][i][j]);
                        result[row * AJump + i][col * BJump + j] = matrixFragments[row][col][i][j];
                    }
                }
            }
        }
        
        printMatrix(result);
        
        System.out.println("Done");
        
        return result;
    }
    
    public void initializeMatrixes(float[][] A, float[][] B) {
        final int n = A.length;
        final int m = A[0].length;
        final int p = B.length;
        final int q = B[0].length;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                A[i][j] = i + 2 * j;
            }
        }
        
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < q; j++) {
                B[i][j] = 3 * i - j;
            }
        }
    }
    
    private float[][] getTransposedMatrix (float[][] A) {
        // Assuming A: nxm
        final int n = A.length;
        final int m = A[0].length;
        float[][] transposedMatrix = new float[m][n];
        int auxiliar;
        
        for (int i = 0; i < n; i++) {
            for (int j = i; j < m; j++) {
                transposedMatrix[j][i] = A[i][j];
            }
        }
        
        return transposedMatrix;
    }
    
    private float[][] getSubmatrix(float[][] A, int beginRow, int beginColumn, int endRow, int endColumn) {
        final int rows = endRow - beginRow;
        final int columns = endColumn - beginColumn;
        float[][] submatrix = new float[rows][columns];
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                submatrix[row][col] = A[beginRow + row][beginColumn + col];
            }
        }
        
        return submatrix;
    }
    
    private void printMatrix(float[][] A) {
        final int n = A.length;
        final int m = A[0].length;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m ; j++) {
                System.out.printf("%.2f\t", A[i][j]);
            }
            System.out.println("");
        }
        System.out.println("");
    }
    
    class MatrixCalculatorClientThread extends Thread {
        private String remoteObjectUrl;
        private float[][] matrixA;
        private float[][] matrixB;
        private int row;
        private int col;
        
        public MatrixCalculatorClientThread(String remoteObjectUrl, float[][] matrixA, float[][] matrixB, int row, int col) {
            this.remoteObjectUrl = remoteObjectUrl;
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.row = row;
            this.col = col;
        }
        
        @Override
        public void run() {
            try {
                RemoteMatrixMultiplicator rmm = new RemoteMatrixMultiplicator();
                matrixFragments[row][col] = rmm.multiplyTransposedMatrixes(matrixA, matrixB);
                System.out.println("Thread " + this.getName() + " Done");
                return;
                
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            
        }
    }
}
