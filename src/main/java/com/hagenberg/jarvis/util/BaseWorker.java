package com.hagenberg.jarvis.util;

public abstract class BaseWorker extends Thread {
    public BaseWorker() {
        setDaemon(true);
    }
}