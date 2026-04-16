import java.util.Arrays;

public class UC17 {
    public static void main(String[] args) {
        String[] types = {"Sleeper", "First Class", "AC Chair", "General"};
        Arrays.sort(types);
        System.out.println("Sorted bogie names: " + Arrays.toString(types));
    }
}
