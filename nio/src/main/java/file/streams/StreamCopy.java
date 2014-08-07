package file.streams;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamCopy {

    public static void main(String[] args) throws IOException {

        String source = "/home/phaun/Downloads/debuggingrules.jpg";
        String destination = "/tmp/debuggingrules.jpg";

        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(destination)) {

            for(int buf; (buf = is.read()) != -1;) {
                os.write(buf);
            }
        }

    }
}
