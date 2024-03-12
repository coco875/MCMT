package org.jmt.mcmt.fabric.jmx;

public interface MCMTDebugMBean {

    public String[] getLoadedMods();

    public String getMainChunkLoadStatus();

    public String[] getBrokenChunkList();

}
