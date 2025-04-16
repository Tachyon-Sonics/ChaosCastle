package ch.chaos.library.sounds.resample;

import ch.chaos.library.sounds.IAudioWave;

public class SquareExpandedWave implements IAudioWave {

    private final IAudioWave source;
    private final int amount;


    public SquareExpandedWave(IAudioWave source, int amount) {
        this.source = source;
        this.amount = amount;
    }

    @Override
    public int getLength() {
        return source.getLength() * amount;
    }

    @Override
    public float getValue(int index) {
        if (index < 0)
            return 0.0f; // Ensure enclidean division
        return source.getValue(index / amount);
    }

}
