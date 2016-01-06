import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by dhruv.pancholi on 04/01/16.
 */
public class Main {
    public static void main(String args[]) {
        FileInputStream in = null;
        try {
            Scanner scanner = new Scanner(new File("/Users/dhruv.pancholi/flipkart/input.txt"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String lastToken = null;

                StringTokenizer s = new StringTokenizer(line, " ");
                String year = s.nextToken();

                while (s.hasMoreTokens()) {
                    lastToken = s.nextToken();
                }
                int maxAvg = Integer.parseInt(lastToken);
                System.out.println(maxAvg);
            }
        } catch (Exception e) {

        }
    }
}
