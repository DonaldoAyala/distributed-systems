
/*
javac *.java && java -Djavax.net.ssl.keyStore=keystore_servidor.jks -Djavax.net.ssl.keyStorePassword=123456 NetworkNodeRunner 0
*/

public class NetworkNodeRunner {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("javax.net.ssl.trustStore","keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","123456");
        
        if (args.length != 1) {
            System.out.println("Execute 'java NetworkNodeRunner x' x is the node number");
            System.exit(1);
        }
        
        int nodeNumber = Integer.parseInt(args[0]);
        NetworkNode node = new NetworkNode(nodeNumber);
        node.start();
    }
}
