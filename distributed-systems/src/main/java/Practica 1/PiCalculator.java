
public class PiCalculator {
    public static void main(String[] args) {
        if (args.length != 0) {
            System.out.println("Execute 'java PiCalculator x' x is the type of instance");
            System.out.println("x = 0: Client");
            System.out.println("x = 1 | 3: Server positive sum calculator");
            System.out.println("x = 2 | 4: Server negative sum calculator");
            System.exit(1);
        }
        
        int programType = Integer.parseInt(args[0]);
        switch (programType) {
            case 0:
            break;
            case 1:
            break;
            case 2:
            break;
            case 3:
            break;
            case 4:
            break;
        }
    }
}
