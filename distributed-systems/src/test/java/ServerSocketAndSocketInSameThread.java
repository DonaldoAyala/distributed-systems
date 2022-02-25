
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;


public class ServerSocketAndSocketInSameThread {
    
    
    static class Producer extends Thread{
        public Semaphore semaphore;
        
        public Producer () throws InterruptedException {
            semaphore = new Semaphore(1);
            semaphore.acquire();
        }
        
        public void acquire() {
            try {
                semaphore.acquire();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        
        public void release() {
            semaphore.release();
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    
                    System.out.println("Semaphore acquired");
                    Thread.sleep(3000);
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            
        }
    }
    
    public static void main(String[] args) throws Exception  {
        Producer p = new Producer();
        p.release();
        p.start();
        while (true) {
            
            Thread.sleep(2000);
            p.release();
        }
    }
}
