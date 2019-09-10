package com.littlecity.server.nio;


/**
 * Hello world!
 *
 */
public class TimeServer
{
    public static void main( String[] args )
    {
//        System.out.println( "Hello World!" );
        int port = 8010;
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer).start();
    }
}
