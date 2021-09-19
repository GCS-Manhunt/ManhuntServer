package Engine;

import Player.*;

public class GeoFence {

    public double[][] points;

    public GeoFence(double[][] points){
        this.points = points;
    }

    public boolean in(Player p){
        int i, j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i][1] > p.getLocation()[1]) != (points[j][1] > p.getLocation()[1]) &&
                    (p.getLocation()[0] < (points[j][0] - points[i][0]) * (p.getLocation()[1] - points[i][1]) / (points[j][1]-points[i][1]) + points[i][0])) {
                result = !result;
            }
        }
        return result;
    }


}
