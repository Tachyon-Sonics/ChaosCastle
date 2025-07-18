package ch.chaos.library.sounds;


public record ControlledWave(long time, SoundWave wave, SoundControls controls) {
    
    public ControlledWave(SoundWave wave, SoundControls controls) {
        this(System.nanoTime(), wave, controls);
    }

    public boolean isSimilarTo(ControlledWave other) {
        if (!this.wave.equals(other.wave))
            return false;
        if (Math.abs(this.controls.getRate() - other.controls.getRate()) > 10.0)
            return false;
        if (Math.abs(this.controls.getStereo() - other.controls.getStereo()) > 10)
            return false;
        if (Math.abs(this.controls.getBalance() - other.controls.getBalance()) > 10)
            return false;
        if (Math.abs(this.controls.getVolume() - other.controls.getVolume()) > 10)
            return false;
        return true;
    }
}
