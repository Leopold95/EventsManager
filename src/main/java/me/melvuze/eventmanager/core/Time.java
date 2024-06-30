package me.melvuze.eventmanager.core;

import java.time.LocalTime;

public class Time {
    public static int localTimeToSeconds(int localTime){
        return LocalTime.now().getSecond();
    }

    public static int localTimeToSeconds(LocalTime localTime){
        return localTime.getSecond();
    }
}