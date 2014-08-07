package net.client;

import java.io.IOException;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < 3000; i++) {
            Socket s = new Socket("localhost", 9999);
            System.out.println("Connected " + i);
        }
    }

}
