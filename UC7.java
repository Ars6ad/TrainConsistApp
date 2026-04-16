import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

public class UC7 {
    public static void main(String[] args) {
        List<Bogie> bogies = new ArrayList<>();
        bogies.add(new Bogie("Sleeper", 72));
        bogies.add(new Bogie("AC Chair", 60));
        bogies.add(new Bogie("First Class", 24));
        
        bogies.sort(Comparator.comparingInt(b -> b.capacity));
        System.out.println("Sorted Bogies: " + bogies);
    }
}
