package ch.chaos.library.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GarbageCollectorType {
    DEFAULT("< Default >"),
    ZGC("ZGC", "-XX:+UseZGC"),
    G1("G1 GC", "-XX:+UseG1GC"),
    PARALLEL("Parallel GC", "-XX:+UseParallelGC"),
    SERIAL("Serial GC", "-XX:+UseSerialGC");
    
    private final String name;
    private final List<String> jvmArgs = new ArrayList<>();

    
    private GarbageCollectorType(String name, String... jvmArgs) {
        this.name = name;
        this.jvmArgs.addAll(Arrays.asList(jvmArgs));
    }
    
    public List<String> getJvmArgs() {
        return jvmArgs;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
