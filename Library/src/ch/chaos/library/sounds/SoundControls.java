package ch.chaos.library.sounds;

/**
 * Audio controls, such as volume, balance, rate, etc.
 * <p>
 * A {@link SoundControls} can either be enqueued to a {@link SoundVoice}
 * using {@link SoundVoice#enqueue(ISoundData)} (in which case it applies
 * to subsequently enqueued {@link SoundWave}s), or applied directly using
 * {@link SoundVoice#control(SoundControls)} (in which case it is applied
 * immediately to any playing {@link SoundWave}).
 * <p>
 * Any property of a {@link SoundControls} can be <tt>null</tt>, in which case
 * the value for that control should not be modified.
 */
public class SoundControls implements ISoundData, Cloneable {

    private Integer distance; // centimeters, SHIFT(data, 12) DIV 34300
    private Double rate; // frames / sec
    private Float volume; // 0..255 -> 0..1
    private Double fm; // std: 256 -> 1.0, rate *= fm / 256
    private Float am; // std: 128 -> 1.0, volume *= am / 128
    private Integer stereo; // -180 .. 180, 90 = right
    private Integer balance; // -90 .. 90
    private Boolean water;


    public void fillDefaults() {
        this.distance = 0;
        this.rate = 20000.0;
        this.volume = 0.5f;
        this.fm = 1.0;
        this.am = 1.0f;
        this.stereo = 0;
        this.balance = 0;
        this.water = false;
    }

    /**
     * Copy any non-null control from the given controls to this
     */
    public void updateFrom(SoundControls other) {
        this.distance = update(this.distance, other.distance);
        this.rate = update(this.rate, other.rate);
        this.volume = update(this.volume, other.volume);
        this.fm = update(this.fm, other.fm);
        this.am = update(this.am, other.am);
        this.stereo = update(this.stereo, other.stereo);
        this.balance = update(this.balance, other.balance);
        this.water = update(this.water, other.water);
    }

    private static <E> E update(E thisValue, E otherValue) {
        return otherValue == null ? thisValue : otherValue;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public Double getFm() {
        return fm;
    }

    public void setFm(Double fm) {
        this.fm = fm;
    }

    public Float getAm() {
        return am;
    }

    public void setAm(Float am) {
        this.am = am;
    }

    public Integer getStereo() {
        return stereo;
    }

    public void setStereo(Integer stereo) {
        this.stereo = stereo;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Boolean getWater() {
        return water;
    }

    public void setWater(Boolean water) {
        this.water = water;
    }

    /**
     * @return calculated rate based on {@link #getRate()} and {@link #getFm()}.
     */
    public double getModulatedRate() {
        return this.rate * this.fm;
    }

    @Override
    public SoundControls clone() {
        try {
            return (SoundControls) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
