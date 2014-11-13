import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;

public class corpus {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("./data/rt.javap"));
            for (String s = br.readLine(); s != null; s = br.readLine()) {
                System.out.println(s);
            }
            doSomething();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void doSomething() {
        System.out.printf("in doSomething()");
    }
}
