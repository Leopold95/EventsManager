package me.melvuze.eventmanager.core;

import java.time.LocalTime;

public class Time {
    public static int localTimeToSeconds(int localTime){
        return LocalTime.now().getSecond();
    }

    public static int localTimeToSeconds(LocalTime localTime){
        return localTime.getSecond();
    }

    public static boolean intoOneSecRange(LocalTime now, LocalTime time){
        return time.equals(now) || (time.isAfter(now) && time.isBefore(now.plusSeconds(1)));
    }
}