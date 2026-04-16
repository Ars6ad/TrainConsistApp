import java.util.HashSet;
import java.util.Set;

public class UC3 {
    public static void main(String[] args) {
        Set<String> bogies = new HashSet<>();
        bogies.add("B101");
        bogies.add("B102");
        bogies.add("B101"); // duplicate ignored
        System.out.println("Unique Bogie IDs: " + bogies);
    }
}
