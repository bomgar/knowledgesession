package net.vertx;

import io.netty.buffer.ByteBuf;
import net.util.Magic;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.net.NetSocket;

import java.io.IOException;

public class VertxServer {

    public static void main(String[] args) throws IOException {

        Vertx vertx = VertxFactory.newVertx();

        vertx.createNetServer().connectHandler(new Handler<NetSocket>() {
            public void handle(final NetSocket socket) {

                socket.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(final Buffer buffer) {

                        ByteBuf byteBuf = buffer.getByteBuf();
                        Buffer outBuffer = new Buffer();
                        while (byteBuf.isReadable()) {
                            outBuffer.appendByte((byte)Magic.doMagic(byteBuf.readByte()));
                        }
                        socket.write(outBuffer);
                    }
                });
            }
        }).listen(9999);

        System.in.read();
    }
}
