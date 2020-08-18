package edu.whu.cgf.geoportal.util.gridsystem;

import src.config.Model1Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lyx
 */

public class SpatialEmbedding {
    private String userName;

    private double x_min;
    private double x_max;
    private double y_min;
    private double y_max;

    private int x_length;
    private int y_length;

    public static double gridSize;

    static {
        gridSize = Model1Constant.gridSize;
    }


    //positionString like x_min+";"+x_max+";"+y_min+";"+y_max;
    public SpatialEmbedding(String userName, String positionString) {
        String[] temp = positionString.split(";");
        this.userName = userName;
        this.x_min = Double.parseDouble(temp[0]);
        this.x_max = Double.parseDouble(temp[1]);
        this.y_min = Double.parseDouble(temp[2]);
        this.y_max = Double.parseDouble(temp[3]);

        x_length = (int) ((x_max - x_min) / gridSize) + 1;
        y_length = (int) ((y_max - y_min) / gridSize) + 1;
    }

    public SpatialEmbedding(String positionString) {
        String[] temp = positionString.split(";");
        this.x_min = Double.parseDouble(temp[0]);
        this.x_max = Double.parseDouble(temp[1]);
        this.y_min = Double.parseDouble(temp[2]);
        this.y_max = Double.parseDouble(temp[3]);

        x_length = (int) ((x_max - x_min) / gridSize) + 1;
        y_length = (int) ((y_max - y_min) / gridSize) + 1;
    }

    public SpatialEmbedding(String userName, double x_min, double x_max, double y_min, double y_max) {
        this.userName = userName;
        this.x_min = x_min;
        this.x_max = x_max;
        this.y_min = y_min;
        this.y_max = y_max;

        x_length = (int) ((x_max - x_min) / gridSize) + 1;
        y_length = (int) ((y_max - y_min) / gridSize) + 1;
    }

    public Grid getGridInedx(double longitude, double latitude) {
        int x = (int) Math.round((longitude - x_min) / gridSize);
        int y = (int) Math.round((latitude - y_min) / gridSize);
        return new Grid(x_length * y + x);
    }

    /**
     * Find the position in the grid system where the given image is located (the term used by STET)
     *
     * @param p_x_min
     * @param p_x_max
     * @param p_y_min
     * @param p_y_max
     * @return java.util.List<gridsystem.Grid>
     * @throws
     * @see
     */
    //x y轴投影 最大最小值,最小边框矩形法
    public List<Grid> getGridIndex(double p_x_min, double p_x_max, double p_y_min, double p_y_max) {
        List<Grid> gridIndex = new ArrayList<Grid>();

        if (p_x_max > x_max || p_x_min < x_min || p_y_min < y_min || p_y_max > y_max) {
            //System.err.println("gridsystem Out Of Bound! 位置不在格子系统内");
            gridIndex.add(new Grid(-1));
            return gridIndex;
        }

        int x_bias_min = (int) Math.round((p_x_min - x_min) / gridSize);
        int x_bias_max = (int) Math.round((p_x_max - x_min) / gridSize);
        int y_bias_min = (int) Math.round((p_y_min - y_min) / gridSize);
        int y_bias_max = (int) Math.round((p_y_max - y_min) / gridSize);

        int delta_x = x_bias_max - x_bias_min + 1;
        int delat_y = y_bias_max - y_bias_min + 1;

        for (int i = y_bias_min; i <= y_bias_max; i++) {
            for (int j = x_bias_min; j <= x_bias_max; j++) {
                gridIndex.add(new Grid(x_length * i + j));
            }
        }

        //check
        if (gridIndex.size() != delat_y * delta_x) {
            System.err.println("格子数量不等");
        }

        return gridIndex;
    }


    /**
     * Find the position in the grid system where the given image is located (the term used by STET)
     *
     * @param tlj
     * @param tlw
     * @param trj
     * @param trw
     * @param brj
     * @param brw
     * @param blj
     * @param blw
     * @return java.util.List<gridsystem.Grid>
     * @throws
     * @see
     */
    public List<Grid> getGridIndex(double tlj, double tlw, double trj, double trw,
                                   double brj, double brw, double blj, double blw) {
        List<Grid> gridIndex = new ArrayList<Grid>();

        if (trj > x_max || brj > x_max || tlj < x_min || blj < x_min ||
                trw > y_max || tlw > y_max || brw < y_min || blw < y_min) {
            //System.err.println("gridsystem Out Of Bound! 位置不在格子系统内");
            gridIndex.add(new Grid(-1));
            return gridIndex;
        }

        int[] tl_bias = {(int) Math.round((tlj - x_min) / gridSize), (int) Math.round((tlw - y_min) / gridSize)};
        int[] tr_bias = {(int) Math.round((trj - x_min) / gridSize), (int) Math.round((trw - y_min) / gridSize)};
        int[] bl_bias = {(int) Math.round((blj - x_min) / gridSize), (int) Math.round((blw - y_min) / gridSize)};
        int[] br_bias = {(int) Math.round((brj - x_min) / gridSize), (int) Math.round((brw - y_min) / gridSize)};

        double[] jindu = {tlj, trj, blj, brj};
        double[] weidu = {tlw, trw, blw, brw};

        int[] x = {tl_bias[0], tr_bias[0], bl_bias[0], br_bias[0]};
        int[] y = {tl_bias[1], tr_bias[1], bl_bias[1], br_bias[1]};

        int t_index = GetMax_Min.getMax(weidu);
        int b_index = GetMax_Min.getMin(weidu);
        int r_index = GetMax_Min.getMax(jindu);
        int l_index = GetMax_Min.getMin(jindu);

        //If the grid is divided into a regular rectangle, use the minimum border rectangle method
        if (y[l_index] == y[b_index] || y[r_index] == y[b_index] || y[t_index] == y[l_index] || y[r_index] == y[t_index]) {
            return getGridIndex(jindu[l_index], jindu[r_index], weidu[b_index], weidu[t_index]);
        }

        double b2l = (x[l_index] - x[b_index]) / (y[l_index] - y[b_index]);   //<0
        double b2r = (x[r_index] - x[b_index]) / (y[r_index] - y[b_index]);   //>0
        double l2t = (x[t_index] - x[l_index]) / (y[t_index] - y[l_index]);   //>0
        double r2t = (x[r_index] - x[t_index]) / (y[r_index] - y[t_index]);   //<0

        int delta = 0;

        for (int i = y[b_index]; i <= y[t_index]; i++) {
            int left_bounder, right_bounder;
            if (i < y[l_index]) {
                left_bounder = (int) Math.round(x[b_index] + b2l * (i - y[b_index]));
            } else {
                left_bounder = (int) Math.round(x[l_index] + l2t * (i - y[l_index]));
            }

            if (i < y[r_index]) {
                right_bounder = (int) Math.round(x[b_index] + b2r * (i - y[b_index]));
            } else {
                right_bounder = (int) Math.round(x[r_index] + r2t * (i - y[r_index]));
            }

            delta += right_bounder - left_bounder + 1;
            //System.out.println(left_bounder+"   "+right_bounder);
            for (int j = left_bounder; j <= right_bounder; j++) {
                gridIndex.add(new Grid(x_length * i + j));
            }
        }
        //check
        if (gridIndex.size() != delta) {
            System.err.println("Wrong number of grids");
        }
        return gridIndex;
    }

}
