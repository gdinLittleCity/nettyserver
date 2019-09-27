package com.littlecity.server.utils;

import java.util.Random;

/**
 * @author huangxiaocheng
 * @Date 2019/9/27
 **/
public class RamdomUtils {
    private static char[] chars = {'1','2','3','4','5','6','7','8','9','0'};


    public static String getRamdom(int length){
        Random ra =new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char ramChar = chars[ra.nextInt(10)];
            sb.append(ramChar);
        }

        return sb.toString();
    }
}
