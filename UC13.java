import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UC13 {
    public static void main(String[] args) {
        List<Integer> capacities = new ArrayList<>();
        for (int i = 0; i < 100000; i++) capacities.add((int)(Math.random() * 100));
        
        long startLoop = System.nanoTime();
        List<Integer> loopFiltered = new ArrayList<>();
        for (int c : capacities) {
            if (c > 50) loopFiltered.add(c);
        }
        long endLoop = System.nanoTime();
        
        long startStream = System.nanoTime();
        List<Integer> streamFiltered = capacities.stream().filter(c -> c > 50).collect(Collectors.toList());
        long endStream = System.nanoTime();
        
        System.out.println("Loop Time: " + (endLoop - startLoop) + " ns");
        System.out.println("Stream Time: " + (endStream - startStream) + " ns");
    }
}
