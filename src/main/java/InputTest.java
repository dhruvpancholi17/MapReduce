import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 * Created by dhruv.pancholi on 05/01/16.
 */
public class InputTest {
    public static void main(String[] args) {
        FileInputStream in = null;
        try{
            Scanner scanner = new Scanner(new File("/Users/dhruv.pancholi/flipkart/input.txt"));
            while(scanner.hasNext()){
                System.out.println(scanner.nextLine());
            }
            scanner.next();
        } catch(Exception e){

        }

    }
}
