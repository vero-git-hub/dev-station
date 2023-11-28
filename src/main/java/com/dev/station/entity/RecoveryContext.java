package com.dev.station.entity;

public class RecoveryContext {
    public RecycleBin currentRecycleBin;
    public boolean isRestorationPerformedFlag;
    public String keyFieldClearFolder;
    public Object source;

    public RecoveryContext(RecycleBin currentRecycleBin, boolean isRestorationPerformedFlag, String keyFieldClearFolder, Object source) {
        this.currentRecycleBin = currentRecycleBin;
        this.isRestorationPerformedFlag = isRestorationPerformedFlag;
        this.keyFieldClearFolder = keyFieldClearFolder;
        this.source = source;
    }
}