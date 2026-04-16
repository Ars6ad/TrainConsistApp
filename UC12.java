import java.util.Arrays;
import java.util.List;

class GoodsBogie {
    String shape;
    String cargo;
    public GoodsBogie(String shape, String cargo) {
        this.shape = shape;
        this.cargo = cargo;
    }
}

public class UC12 {
    public static void main(String[] args) {
        List<GoodsBogie> bogies = Arrays.asList(
            new GoodsBogie("Cylindrical", "Petroleum"),
            new GoodsBogie("Rectangular", "Coal")
        );
        
        boolean isSafe = bogies.stream()
            .allMatch(b -> b.shape.equals("Cylindrical") ? b.cargo.equals("Petroleum") : true);
            
        System.out.println("Train safety compliant: " + isSafe);
    }
}
