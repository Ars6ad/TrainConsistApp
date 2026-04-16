public class UC18 {
    public static void main(String[] args) {
        String[] ids = {"B101", "B105", "B102", "B104"};
        String key = "B102";
        
        boolean found = false;
        for (String id : ids) {
            if (id.equals(key)) {
                found = true;
                break;
            }
        }
        
        System.out.println("Bogie " + key + " exists: " + found);
    }
}
