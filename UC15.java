class CargoSafetyException extends RuntimeException {
    public CargoSafetyException(String message) {
        super(message);
    }
}

public class UC15 {
    public static void assignCargo(String shape, String cargo) {
        if (shape.equals("Rectangular") && cargo.equals("Petroleum")) {
            throw new CargoSafetyException("Unsafe assignment: Petroleum in Rectangular bogie!");
        }
        System.out.println("Safely assigned " + cargo + " to " + shape + " bogie.");
    }
    
    public static void main(String[] args) {
        try {
            assignCargo("Rectangular", "Petroleum");
        } catch (CargoSafetyException e) {
            System.out.println("Exception caught: " + e.getMessage());
        } finally {
            System.out.println("Cleanup / Logging executed.");
        }
        System.out.println("Program continues safely.");
    }
}
