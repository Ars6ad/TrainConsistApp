import java.util.ArrayList;
import java.util.List;

public class UC20 {
    public static void main(String[] args) {
        List<String> bogies = new ArrayList<>(); // Empty
        
        try {
            searchBogie(bogies, "B101");
        } catch (IllegalStateException e) {
            System.out.println("Search failed: " + e.getMessage());
        }
    }
    
    public static void searchBogie(List<String> bogies, String key) {
        if (bogies.isEmpty()) {
            throw new IllegalStateException("Cannot search on an empty train!");
        }
        System.out.println("Searching for " + key + "...");
    }
}
