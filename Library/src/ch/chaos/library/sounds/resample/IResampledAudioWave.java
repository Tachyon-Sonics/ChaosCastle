package ch.chaos.library.sounds.resample;

import ch.chaos.library.sounds.IAudioWave;

/**
 * A simple monophonic audio wave, represented as floats between -1.0 and 1.0, that is a resampled
 * version of an {@link IAudioWave}. The resample ratio can be modified on the fly.
 * <p>
 * There are two implementations: {@link LinearResampledWave} that uses fast linear reampling, and
 * {@link SincResampledWave} that uses high quality sinc resampling.
 */
public interface IResampledAudioWave {

    /**
     * The length of the resampled audio wave, in samples
     */
    public int getLength();

    /**
     * Change the resample ratio. This will also change the current position.
     * @param ratio target sample rate / source sample rate. &gt; 1 is upsampling
     */
    public void setResampleRatio(double ratio);

    /**
     * @return the current resample ratio
     */
    public double getResampleRatio();

    /**
     * Set the current postion (in the resampled version of the wave). The next call
     * to {@link #getValue()} will read from the given position.
     */
    public void setPosition(int position);

    /**
     * Get the current position. The returned value is the position at which the next
     * call to {@link #getValue()} will read.
     */
    public int getPosition();

    /**
     * Get the sample value at the current position, and move the current position to
     * the next sample value. Reading past the last sample will return <tt>0.0</tt> (silence).
     */
    public float getValue();

}
