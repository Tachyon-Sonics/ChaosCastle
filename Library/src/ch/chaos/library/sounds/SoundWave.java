package ch.chaos.library.sounds;

import java.util.Arrays;
import java.util.Objects;

/**
 * A sampled, monophonic sound wave, represented as floats between -1.0 and 1.0.
 * <p>
 * A {@link SoundWave} does not have a sample rate. Sample rate is controlled in a
 * {@link SoundVoice} by creating a {@link SoundControls}, specifying the rate using
 * {@link SoundControls#setRate()}, and then applying it on the <tt>SoundVoice</tt> using
 * {@link SoundVoice#enqueue(ISoundData)} or {@link SoundVoice#control(SoundControls)}.
 */
public class SoundWave implements ISoundData, IAudioWave {

    private float[] wave;
    private int length; // starting from 'offset'
    private int offset; // offset in 'wave'


    public SoundWave() {

    }

    public SoundWave(float[] wave) {
        this.wave = wave;
        this.length = wave.length;
        this.offset = 0;
    }

    public float[] getWave() {
        return wave;
    }

    public void setWave(float[] wave) {
        this.wave = wave;
    }

    @Override
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public float getValue(int index) {
        if (index < 0 || index >= length)
            return 0.0f;
        return wave[offset + index];
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(wave);
        result = prime * result + Objects.hash(length, offset);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SoundWave other = (SoundWave) obj;
        return length == other.length && offset == other.offset && Arrays.equals(wave, other.wave);
    }

}
