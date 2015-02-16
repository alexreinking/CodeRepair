import java.io.*;
import java.io.BufferedReader;
import java.io.SequenceInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class corpus {
    public static void main(String[] args) {
        String body = "hello, world!";
        String signature = "Alex";

        SequenceInputStream sis = new SequenceInputStream(body, signature);
//        Matcher m = Pattern.compile(body);
    }
}
