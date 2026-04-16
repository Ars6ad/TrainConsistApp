import java.util.Arrays;
import java.util.List;

class Bogie {
    String name;
    int capacity;
    public Bogie(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
}

public class UC10 {
    public static void main(String[] args) {
        List<Bogie> bogies = Arrays.asList(
            new Bogie("Sleeper", 72),
            new Bogie("AC", 60)
        );
        
        int totalSeats = bogies.stream()
            .map(b -> b.capacity)
            .reduce(0, Integer::sum);
            
        System.out.println("Total seating capacity: " + totalSeats);
    }
}
