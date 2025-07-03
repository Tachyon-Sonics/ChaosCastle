package ch.chaos.library.settings;


public enum VsyncType {
    SLEEP("Sleep"),
    BALANCED_LOW("Balanced low"),
    BALANCED_HIGH("Balanced high"),
    ACTIVE("Active");
    
    private final String name;
    
    private VsyncType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
