package com.akgames.biriba;

public class Debug {
    public static int eventNum = 0;


    public static void print(String msg) {
        System.out.println(eventNum +
                ". " +
                msg);
        eventNum++;
    }
}
