package file.channels;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChannelCopy {

    private static final long CHUNK_SIZE = 4000L;

    public static void main(String[] args) throws IOException {

        Path source = Paths.get("/home/phaun/Downloads/debuggingrules.jpg");
        Path destination = Paths.get("/tmp/debuggingrules.jpg");

        //Seit Java 7
        //Files.copy(source, destination);

        try (
                FileChannel ic = FileChannel.open(source, StandardOpenOption.READ);
                FileChannel oc = FileChannel.open(destination, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                ) {
            long remaining = Files.size(source);
            long pos = 0;
            while(remaining > 0) {
                long transferred = ic.transferTo(pos, Math.min(remaining, CHUNK_SIZE), oc);
                pos += transferred;
                remaining -= transferred;
            }

        }

    }
}
