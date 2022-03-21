

public class MatrixMultiplicator {
    public static void main(String[] args) {
        final int MATRIX_SIZE = 1000;
        double[][] C = new double[MATRIX_SIZE][MATRIX_SIZE];
        double[][] A = new double[MATRIX_SIZE][MATRIX_SIZE];
        double[][] B = new double[MATRIX_SIZE][MATRIX_SIZE];
        
        final int n = MATRIX_SIZE;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = i + 5 * j;
                B[i][j] = 5 * i - j;
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        double checksum = 0;
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                checksum += C[i][j];
            }
        }
        
        System.out.printf("Checksum: %f\n", checksum);
    }
}
