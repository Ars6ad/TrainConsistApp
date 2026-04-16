import java.util.TreeSet;
import java.util.Set;

public class UC4 {
    public static void main(String[] args) {
        Set<String> bogies = new TreeSet<>();
        bogies.add("B105");
        bogies.add("B101");
        bogies.add("B103");
        bogies.add("B101"); // Duplicate ignored
        System.out.println("Ordered Unique Bogie IDs: " + bogies);
    }
}
