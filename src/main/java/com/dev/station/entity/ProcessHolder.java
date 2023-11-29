package com.dev.station.entity;

public class ProcessHolder {
    public Process process;
    public boolean isRunning;

    public ProcessHolder() {

    }

    public ProcessHolder(Process process, boolean isRunning) {
        this.process = process;
        this.isRunning = isRunning;
    }
}