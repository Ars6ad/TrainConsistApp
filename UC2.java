import java.util.ArrayList;
import java.util.List;

public class UC2 {
    public static void main(String[] args) {
        List<String> bogies = new ArrayList<>();
        bogies.add("Sleeper");
        bogies.add("AC Chair");
        bogies.add("First Class");
        System.out.println("Bogies after insertion: " + bogies);
        bogies.remove("AC Chair");
        boolean hasSleeper = bogies.contains("Sleeper");
        System.out.println("Contains Sleeper? " + hasSleeper);
        System.out.println("Final state: " + bogies);
    }
}
