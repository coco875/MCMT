package org.jmt.mcmt;

public class ExampleMod {
    public static final String MOD_ID = "mcmt";
    
    public static void init() {        
        System.out.println(ExampleExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}
