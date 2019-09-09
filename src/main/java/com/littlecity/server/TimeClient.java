package com.littlecity.server;

import com.littlecity.server.nio.TimeClientHandle;

public class TimeClient {

    public static void main(String[] args) {
        int port = 8010;
        TimeClientHandle timeClientHandle = new TimeClientHandle("127.0.0.1", port);
        new Thread(timeClientHandle).start();
    }
}
