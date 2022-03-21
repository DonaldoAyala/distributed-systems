import java.util.*;

public class MulticastServer {
    
    static class ReaderThread extends Thread {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            String entrada;
            try {
                while (true) {
                    System.out.println("Enter something");
                    entrada = scanner.nextLine();
                    System.out.println("Entered line " + entrada);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    static class PrinterThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("Hello...");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        ReaderThread rt = new ReaderThread();
        PrinterThread pt = new PrinterThread();
        
        rt.start();
        pt.start();
        
        rt.join();
        pt.join();
    }
}
