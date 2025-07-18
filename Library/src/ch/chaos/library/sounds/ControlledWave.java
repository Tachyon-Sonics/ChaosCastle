package ch.chaos.library.sounds;


public record ControlledWave(long time, SoundWave wave, SoundControls controls) {
    
    public ControlledWave(SoundWave wave, SoundControls controls) {
        this(System.nanoTime(), wave, controls);
    }

    public boolean isSimilarTo(ControlledWave other) {
        if (!this.wave.equals(other.wave))
            return false;
        if (!isSimilar(this.controls.getRate(), other.controls.getRate(), 10.0))
            return false;
        if (!isSimilar(this.controls.getStereo(), other.controls.getStereo(), 10.0))
            return false;
        if (!isSimilar(this.controls.getBalance(), other.controls.getBalance(), 10.0))
            return false;
        if (!isSimilar(this.controls.getVolume(), other.controls.getVolume(), 10.0))
            return false;
        return true;
    }
    
    private boolean isSimilar(Number n1, Number n2, double threshold) {
        if (n1 == null && n2 == null)
            return true;
        if (n1 == null || n2 == null)
            return false;
        if (Math.abs(n1.doubleValue() - n2.doubleValue()) <= threshold)
            return true;
        return false;
    }
}
