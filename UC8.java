import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Bogie {
    String name;
    int capacity;
    public Bogie(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }
    @Override
    public String toString() {
        return name + " (" + capacity + ")";
    }
}

public class UC8 {
    public static void main(String[] args) {
        List<Bogie> bogies = new ArrayList<>();
        bogies.add(new Bogie("Sleeper", 72));
        bogies.add(new Bogie("AC Chair", 60));
        bogies.add(new Bogie("First Class", 24));
        
        List<Bogie> filtered = bogies.stream()
            .filter(b -> b.capacity > 60)
            .collect(Collectors.toList());
            
        System.out.println("Bogies with capacity > 60: " + filtered);
    }
}
