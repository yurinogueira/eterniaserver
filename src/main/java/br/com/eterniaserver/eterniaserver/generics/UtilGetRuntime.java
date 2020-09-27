package br.com.eterniaserver.eterniaserver.generics;

import java.lang.management.ManagementFactory;

public class UtilGetRuntime {

    private long freemem;
    private long totalmem;
    private int seconds;
    private int minutes;
    private int hours;

    public void recalculateRuntime() {
        Runtime runtime = Runtime.getRuntime();
        long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        totalmem = runtime.totalMemory() / 1048576;
        freemem = totalmem - (runtime.freeMemory() / 1048576);
        seconds = (int) (milliseconds / 1000) % 60;
        minutes = (int) ((milliseconds / (1000*60)) % 60);
        hours   = (int) ((milliseconds / (1000*60*60)) % 24);
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public long getFreemem() {
        return freemem;
    }

    public long getTotalmem() {
        return totalmem;
    }

}
