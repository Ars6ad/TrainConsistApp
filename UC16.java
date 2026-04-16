import java.util.Arrays;

public class UC16 {
    public static void main(String[] args) {
        int[] capacities = {72, 24, 60, 48};
        
        for (int i = 0; i < capacities.length - 1; i++) {
            for (int j = 0; j < capacities.length - i - 1; j++) {
                if (capacities[j] > capacities[j+1]) {
                    int temp = capacities[j];
                    capacities[j] = capacities[j+1];
                    capacities[j+1] = temp;
                }
            }
        }
        
        System.out.println("Sorted capacities: " + Arrays.toString(capacities));
    }
}
