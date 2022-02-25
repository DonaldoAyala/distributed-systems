
import RingSocketTopology.NetworkNode;


public class NetworkNodeRunner {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Execute 'java NetworkNodeRunner x' x is the node number");
            System.exit(1);
        }
        
        int nodeNumber = Integer.parseInt(args[0]);
        NetworkNode node = new NetworkNode(nodeNumber);
        node.start();
    }
}
