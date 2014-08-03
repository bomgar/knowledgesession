package blocking;

import util.Magic;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleBlockingServer {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private static int openConnections = 0;

    public static void main(String[] args) throws IOException {

        final ServerSocket serverSocket = new ServerSocket(9999);

        while (true) {

            final Socket socket = serverSocket.accept();
            System.out.println("Open connections " + ++openConnections);
            executorService.submit(new SocketHandler(socket));
        }
    }

    private static class SocketHandler implements Runnable {
        private final Socket socket;

        public SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    Socket s = socket;
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream()
            ) {

                for (int i = 0; (i = is.read()) != -1; ) {
                    os.write(Magic.doMagic(i));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            openConnections--;

        }
    }
}
