import java.util.Arrays;

public class UC19 {
    public static void main(String[] args) {
        String[] ids = {"B101", "B102", "B104", "B105"}; // must be sorted
        String key = "B104";
        
        int low = 0;
        int high = ids.length - 1;
        int foundIndex = -1;
        
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = key.compareTo(ids[mid]);
            
            if (cmp == 0) {
                foundIndex = mid;
                break;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        
        System.out.println("Bogie " + key + (foundIndex != -1 ? " found at index " + foundIndex : " not found"));
    }
}
