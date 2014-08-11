package file.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ChannelBufferCopy {

    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(4000);

    public static void main(String[] args) throws IOException {

        Path source = Paths.get("/home/phaun/Downloads/debuggingrules.jpg");
        Path destination = Paths.get("/tmp/debuggingrules.jpg");

        //Seit Java 7
        //Files.copy(source, destination);

        try (
                FileChannel ic = FileChannel.open(source, StandardOpenOption.READ);
                FileChannel oc = FileChannel.open(destination, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        ) {
            while (ic.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    oc.write(buffer);
                }
                buffer.clear();
            }

        }

    }
}
