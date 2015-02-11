import java.io.*;

public class corpus {
    public static void main(String[] args) {
        String body = "hello, world!";
        String sig = "Alex";

        SequenceInputStream sis = new SequenceInputStream(body, sig);
    }
}
