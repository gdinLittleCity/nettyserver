package com.littlecity.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Unit test for simple TimeServer.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        File file = new File("C:/upload/as");

        if (!file.exists()) {
            file.mkdirs();
        }

        File newFile = new File(file, "123.txt");

        FileOutputStream fos = new FileOutputStream(newFile);


        fos.write("hello".getBytes());
        fos.close();
    }
}
