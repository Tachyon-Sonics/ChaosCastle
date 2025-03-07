package ch.chaos.library.sounds;

/**
 * A simple monophonic audio wave, represented as floats between -1.0 and 1.0
 */
public interface IAudioWave {

    /**
     * Length, in samples, of this audio wave
     */
    public int getLength();

    /**
     * @return the sample value at the given index. This method must properly return <tt>0.0f</tt>
     * if the given index is negative or &gt;= the length of this audio wave
     */
    public float getValue(int index);

}
