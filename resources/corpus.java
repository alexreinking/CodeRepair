import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.*;
import java.util.zip.DeflaterInputStream;

public class corpus {
    private static final String fileName = "my_file.txt";

    public static void main(String[] args) {
        int buffSize = 1024, compLevel = Deflater.BEST_SPEED;

        BufferedInputStream bis = new BufferedInputStream(buffSize, new DeflaterInputStream(new FileInputStream(), compLevel, true));
    }
}
