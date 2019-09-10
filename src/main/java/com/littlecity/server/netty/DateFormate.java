package com.littlecity.server.netty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormate {

    public static void main(String[] args) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-m-d h:m:s");

        Date date = new Date();
        date.setTime(1568358795000L);
        String format = sf.format(date);
        System.out.println(format);
    }
}
