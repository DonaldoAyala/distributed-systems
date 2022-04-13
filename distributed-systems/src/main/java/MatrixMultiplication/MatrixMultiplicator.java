

public class MatrixMultiplicator {
    
    public static void printMatrix(float[][] A) {
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
    
    public static void main(String[] args) {
        final int MATRIX_SIZE = 4000;
        float[][] C = new float[MATRIX_SIZE][MATRIX_SIZE];
        float[][] A = new float[MATRIX_SIZE][MATRIX_SIZE];
        float[][] B = new float[MATRIX_SIZE][MATRIX_SIZE];
        
        final int n = MATRIX_SIZE;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = i + 2 * j;
                B[i][j] = 3 * i - j;
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        float checksum = 0;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                checksum += C[i][j];
            }
        }
        
        System.out.printf("Checksum: %f\n", checksum);
    }
}
