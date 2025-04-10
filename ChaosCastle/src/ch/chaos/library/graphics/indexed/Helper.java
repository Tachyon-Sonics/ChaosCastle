package ch.chaos.library.graphics.indexed;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

public class Helper {
    
    /**
     * Its not clear which one is optimal... After a few tests, it seems that {@link PixelInterleavedSampleModel} and
     * {@link MultiPixelPackedSampleModel} are equivalent, and {@link SinglePixelPackedSampleModel} is always slower.
     * <p>
     * We choose {@link PixelInterleavedSampleModel} because it seems to be the one used by default when creating
     * a {@link BufferedImage} with type {@link BufferedImage#TYPE_BYTE_INDEXED}.
     * @param width
     * @param height
     * @param nbBits
     * @return
     */
    public static SampleModel createSampleModel(int width, int height, int nbBits) {
        SampleModel sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, width,
                new int[] { 0 });
        
//        SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, width,
//                new int[] { (1 << nbBits) - 1 });
        
//        SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, nbBits);
        
        return sampleModel;
    }

}
