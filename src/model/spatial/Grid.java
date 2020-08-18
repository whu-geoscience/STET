package edu.whu.cgf.geoportal.util.gridsystem;

/**
 * @author lyx
 */
public class Grid {
    private int index;

    public Grid(int i) {
        index = i;
    }

    @Override
    public String toString() {
        return "Grid_" + index;
    }
}
