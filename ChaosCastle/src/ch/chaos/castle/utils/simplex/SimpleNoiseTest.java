package ch.chaos.castle.utils.simplex;

public class SimpleNoiseTest {

    public static void main(String args[]) {
        SimplexNoise simplexNoise = new SimplexNoise(100, 0.5, 5000);

        double xStart = 0;
        double XEnd = 500;
        double yStart = 0;
        double yEnd = 500;

        int xResolution = 100;
        int yResolution = 60;
        
        int xCenter = xResolution / 2;
        int yCenter = yResolution / 2;
        int xRadius = xResolution / 2;
        int yRadius = yResolution / 2;

        double[][] result = new double[xResolution][yResolution];

        for (int i = 0; i < xResolution; i++) {
            for (int j = 0; j < yResolution; j++) {
                int x = (int) (xStart + i * ((XEnd - xStart) / xResolution));
                int y = (int) (yStart + j * ((yEnd - yStart) / yResolution));
                double noise = simplexNoise.getNoise(x, y);
                
                double dx = (double) Math.abs(i - xCenter) / (double) xRadius;
                double dy = (double) Math.abs(j - yCenter) / (double) yRadius;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double correction = distance * 2.0 - 1.0;
                System.out.println(correction);
                
                double value = noise + correction;
//                result[i][j] = 0.5 * (1 + simplexNoise.getNoise(x, y));
//                result[i][j] = 0.5 + simplexNoise.getNoise(x, y) * 3.0;
                result[i][j] = (value > 0.0 ? 1.0 : 0.0);
            }
        }

        ImageWriter.greyWriteImage(result);
    }
}
