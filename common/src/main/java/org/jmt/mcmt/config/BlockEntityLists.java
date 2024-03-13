package org.jmt.mcmt.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BlockEntityLists {
    public static Set<Class<?>> teWhiteList = ConcurrentHashMap.newKeySet();
    public static Set<Class<?>> teBlackList = ConcurrentHashMap.newKeySet();
}
