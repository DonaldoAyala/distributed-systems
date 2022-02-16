
public class PiCalculator {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Execute 'java PiCalculator x' x is the type of instance");
            System.out.println("x = 0: Client");
            System.out.println("x = 1 | 3: Server positive sum calculator");
            System.out.println("x = 2 | 4: Server negative sum calculator");
            System.exit(1);
        }
        
        int programType = Integer.parseInt(args[0]);
        switch (programType) {
            case 0:
                PiCalculatorClient piCalculator = new PiCalculatorClient();
                var piValue = piCalculator.calculatePi(
                        new String[]{"localhost", "localhost", "localhost", "localhost"}, 
                        new int[]{5001, 5002, 5003, 5004});
                System.out.println("PI value: " + piValue);
            break;
            
            default:
                for (int i = 1; i <= 4; i++) {
                    try {
                        PiCalculatorServerNegative.runServer(i);
                    } catch (Exception e) {
                        
                    }
                }
            break;
        }
    }
}
