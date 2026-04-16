import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Bogie {
    String name;
    String type;
    public Bogie(String name, String type) {
        this.name = name;
        this.type = type;
    }
    @Override
    public String toString() {
        return name;
    }
}

public class UC9 {
    public static void main(String[] args) {
        List<Bogie> bogies = Arrays.asList(
            new Bogie("Sleeper 1", "Passenger"),
            new Bogie("Cargo 1", "Goods"),
            new Bogie("AC 1", "Passenger")
        );
        
        Map<String, List<Bogie>> grouped = bogies.stream()
            .collect(Collectors.groupingBy(b -> b.type));
            
        System.out.println("Grouped Bogies: " + grouped);
    }
}
