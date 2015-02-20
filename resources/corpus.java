import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.*;
import java.util.zip.DeflaterInputStream;

public class corpus {
    public static void main(String[] args) {
        int buffSize = 1024, compLevel = Deflater.BEST_SPEED;
        String fileName = "compressed.txt";

        String body = "Hello world!";
        String signature = "Alex";

        InputStream input = new BufferedInputStream(buffSize, new DeflaterInputStream(
                new FileInputStream(fileName), compLevel, true));
    }
}
