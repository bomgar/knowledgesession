package net.nonblocking;

import net.util.Magic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SingleThreadedNonBlockingServer {

    private static Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<>();

    private static ByteBuffer readBuffer = ByteBuffer.allocateDirect(80);

    private static Selector selector;

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int selectedChannels = selector.select();
            if (selector.isOpen() && selectedChannels > 0) {
                handleSelectionKeys(selector);
            }

        }
    }

    private static void handleSelectionKeys(final Selector selector) {

        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
        while (selectionKeyIterator.hasNext()) {
            SelectionKey selectionKey = selectionKeyIterator.next();
            handleSelectionKey(selectionKey);
            selectionKeyIterator.remove();
        }
    }

    private static void handleSelectionKey(final SelectionKey selectionKey) {

        if (selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {
                handleAccept(selectionKey);
            } else if (selectionKey.isReadable()) {
                handleReadable(selectionKey);
            } else if (selectionKey.isWritable()) {
                handleWriteable(selectionKey);
            }
        }

    }

    private static void handleWriteable(final SelectionKey selectionKey) {

        try {
            SocketChannel socketChannel = ((SocketChannel) selectionKey.channel());
            Queue<ByteBuffer> buffers = pendingData.get(socketChannel);
            while (!buffers.isEmpty()) {
                ByteBuffer buffer = buffers.peek();

                int written = socketChannel.write(buffer);
                if (written == 0) {
                    break;
                }
                if (!buffer.hasRemaining()) {
                    buffers.remove();
                }
            }
            selectionKey.interestOps(SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleReadable(final SelectionKey selectionKey) {

        try {
            SocketChannel socketChannel = ((SocketChannel) selectionKey.channel());
            readBuffer.clear();
            int numRead = socketChannel.read(readBuffer);

            if (numRead == -1) {
                socketChannel.close();
                pendingData.remove(socketChannel);
            } else {
                readBuffer.flip();
                ByteBuffer responseBuffer = ByteBuffer.allocate(readBuffer.limit());
                while (readBuffer.hasRemaining()) {
                    byte r = readBuffer.get();
                    responseBuffer.put((byte) Magic.doMagic(r));
                }
                responseBuffer.flip();
                pendingData.get(socketChannel).offer(responseBuffer);
                selectionKey.interestOps(SelectionKey.OP_WRITE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(final SelectionKey selectionKey) {

        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            pendingData.put(socketChannel, new LinkedList<ByteBuffer>());
            System.out.println("New connection: " + socketChannel);
            System.out.println("Open channels: " + pendingData.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
