import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PolynomialConstantFinder {

    public static BigInteger decodeValue(int base, String value) {
        return new BigInteger(value, base);
    }

    public static BigInteger lagrangeInterpolation(List<BigInteger[]> points, int k) {
        BigInteger constantTerm = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger[] pointI = points.get(i);
            BigInteger xi = pointI[0];
            BigInteger yi = pointI[1];
            BigInteger term = yi;

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    BigInteger[] pointJ = points.get(j);
                    BigInteger xj = pointJ[0];
                    term = term.multiply(xj).divide(xj.subtract(xi));
                }
            }
            constantTerm = constantTerm.add(term);
        }

        return constantTerm;
    }

    public static void findConstantTerm(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            JSONTokener tokener = new JSONTokener(fis);
            JSONObject data = new JSONObject(tokener);

            int n = data.getJSONObject("keys").getInt("n");
            int k = data.getJSONObject("keys").getInt("k");

            List<BigInteger[]> points = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                JSONObject pointData = data.getJSONObject(String.valueOf(i));
                int base = pointData.getInt("base");
                String value = pointData.getString("value");

                BigInteger x = BigInteger.valueOf(i);
                BigInteger y = decodeValue(base, value);
                points.add(new BigInteger[]{x, y});
            }

            BigInteger constantTerm = lagrangeInterpolation(points.subList(0, k), k);
            System.out.println("Constant term (C) for " + filename + ": " + constantTerm);

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        findConstantTerm("testcase1.json");
        findConstantTerm("testcase2.json");
    }
}
