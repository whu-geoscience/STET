package edu.whu.cgf.geoportal.util.gridsystem;

/**
 * max or min util
 *
 * @author lyx
 */
public class GetMax_Min {
    public static int getMax(double x[]) {
        int index = 0;
        for (int i = 1; i < 4; i++) {
            if (x[i] > x[index]) {
                index = i;
            }
        }
        return index;
    }

    public static int getMin(double x[]) {
        int index = 0;
        for (int i = 1; i < 4; i++) {
            if (x[i] < x[index]) {
                index = i;
            }
        }
        return index;
    }
}
