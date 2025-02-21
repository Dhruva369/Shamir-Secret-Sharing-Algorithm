import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class SecretPolynomial {
    
    public static void main(String[] args) {
        String file1 = "testcase1.json"; // JSON file paths
        String file2 = "testcase2.json";

        System.out.println("Secret for Test Case 1: " + findSecret(file1));
        System.out.println("Secret for Test Case 2: " + findSecret(file2));
    }

    // Function to compute the constant term 'c'
    public static BigInteger findSecret(String filename) {
        try {
            // Parse JSON file
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(filename));

            // Read keys n and k
            JSONObject keys = (JSONObject) json.get("keys");
            int n = ((Long) keys.get("n")).intValue();
            int k = ((Long) keys.get("k")).intValue();

            // Read x, y pairs
            List<BigInteger[]> points = new ArrayList<>();
            for (Object key : json.keySet()) {
                if (key.equals("keys")) continue; // Skip metadata
                
                int x = Integer.parseInt((String) key);
                JSONObject entry = (JSONObject) json.get(key);
                int base = Integer.parseInt((String) entry.get("base"));
                
                // Fix: Use BigInteger to parse large numbers
                BigInteger y = new BigInteger((String) entry.get("value"), base);
                
                points.add(new BigInteger[]{BigInteger.valueOf(x), y});
            }

            // Sort points based on x and select first k
            points.sort(Comparator.comparing(a -> a[0]));
            List<BigInteger[]> selectedPoints = points.subList(0, k);

            // Apply Lagrange Interpolation to compute 'c'
            return lagrangeInterpolation(selectedPoints);
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return BigInteger.valueOf(-1); // Error case
    }

    // Function to compute f(0) using Lagrange Interpolation
    public static BigInteger lagrangeInterpolation(List<BigInteger[]> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger x_i = points.get(i)[0];
            BigInteger y_i = points.get(i)[1];

            BigInteger term = y_i;
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger x_j = points.get(j)[0];
                    term = term.multiply(BigInteger.ZERO.subtract(x_j)).divide(x_i.subtract(x_j));
                }
            }
            result = result.add(term);
        }
        return result;
    }
}
