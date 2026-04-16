import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UC11 {
    public static void main(String[] args) {
        String trainId = "TRN-1234";
        String cargoCode = "PET-AB";
        
        Pattern trainPattern = Pattern.compile("TRN-\\d{4}");
        Matcher trainMatcher = trainPattern.matcher(trainId);
        
        Pattern cargoPattern = Pattern.compile("PET-[A-Z]{2}");
        Matcher cargoMatcher = cargoPattern.matcher(cargoCode);
        
        System.out.println("Train ID valid: " + trainMatcher.matches());
        System.out.println("Cargo Code valid: " + cargoMatcher.matches());
    }
}
