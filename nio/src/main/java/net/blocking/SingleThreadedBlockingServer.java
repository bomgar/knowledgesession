package net.blocking;

import net.util.Magic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadedBlockingServer {


    public static void main(String[] args) throws IOException {

        final ServerSocket serverSocket = new ServerSocket(9999);

        while (true) {
            try (
                    final Socket socket = serverSocket.accept();
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream()
            ) {

                for (int i = 0; (i = is.read()) != -1; ) {
                    os.write(Magic.doMagic(i));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
