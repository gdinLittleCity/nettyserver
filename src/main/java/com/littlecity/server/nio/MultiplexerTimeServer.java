package com.littlecity.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;
    private volatile static int RUN_TIME = 1;


    public MultiplexerTimeServer(int port){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            // backlog ?
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time server is start in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop){
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()){
                    key = iterator.next();

                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception ex){
                        //关闭连接
                        if (key != null){
                            key.cancel();
                            if (key.channel() != null){
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (selector != null){
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        System.out.println("handle input run start. time:" + (RUN_TIME++));
        if (key.isValid()){
            if (key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);

            }
            if (key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                sc.configureBlocking(false);
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int read = sc.read(readBuffer);
                if (read > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("the time server receive order:"+ body);
                    String currentTime = "Query time order".equalsIgnoreCase(body)? "北京时间:"+ new Date(System.currentTimeMillis()).toString(): "Bad Order.";
//                    String currentTime = "12658";
                    byte[] responseBytes = currentTime.getBytes();
                    ByteBuffer writerBuffer = ByteBuffer.allocate(responseBytes.length);
                    writerBuffer.put(responseBytes);
                    writerBuffer.flip();
                    sc.write(writerBuffer);
                } else if (read < 0){
                    key.cancel();
                    sc.close();
                }
            }
        }

    }
}
