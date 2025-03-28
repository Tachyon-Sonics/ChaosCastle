package ch.chaos.castle.utils;

/**
 * Cubic interpolation
 */
public class CubicInterpolator {

    /**
     * Simple and fast cubic interpolation. Derivative is not continuous
     * @param mu relative x position between y1 and y2. 0 = at y1, 1 = at y2
     * @return y value at <code>mu</code>
     */
    public static double interpolateY(double y0, double y1, double y2, double y3, double mu) {
        double mu2 = mu * mu;
        double a0 = y3 - y2 - y0 + y1;
        double a1 = y0 - y1 - a0;
        double a2 = y2 - y0;
        double a3 = y1;
        return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
    }

    /**
     * Cubic spline interpolation. Derivative is continuous.
     * @param mu relative x position between y1 and y2. 0 = at y1, 1 = at y2
     * @return y value at <code>mu</code>
     */
    public static double splineInterpolateY(double y0, double y1, double y2, double y3, double mu) {
        double mu2 = mu * mu;
        double a0 = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;
        double a1 = y0 - 2.5 * y1 + 2 * y2 - 0.5 * y3;
        double a2 = -0.5 * y0 + 0.5 * y2;
        double a3 = y1;
        return (a0 * mu * mu2 + a1 * mu2 + a2 * mu + a3);
    }

}
