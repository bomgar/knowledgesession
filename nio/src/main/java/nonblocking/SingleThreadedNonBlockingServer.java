package nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SingleThreadedNonBlockingServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(9999));

        Selector selector = Selector.open();
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
            handleSelectionKey(selectionKey, selector);
            selectionKeyIterator.remove();
        }
    }

    private static void handleSelectionKey(final SelectionKey selectionKey, final Selector selector) {

        if (selectionKey.isValid()) {
            if (selectionKey.isAcceptable()) {
                handleAccept(selectionKey, selector);
            } else if (selectionKey.isReadable()) {
                handleReadable(selectionKey);
            } else if (selectionKey.isWritable()) {
                handleWriteable(selectionKey);
            }
        }

    }

    private static void handleWriteable(final SelectionKey selectionKey) {

    }

    private static void handleReadable(final SelectionKey selectionKey) {

        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            ByteBuffer buffer = ByteBuffer.allocate(80);

            int numRead = socketChannel.read(buffer);

            if (numRead == -1) {
                socketChannel.close();
            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAccept(final SelectionKey selectionKey, final Selector selector) {

        try {
            SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("New connection: " + socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
