package player;

import java.util.Arrays;
import java.util.UUID;
import java.lang.Math;

public class Player {
    public final UUID uuid;
    public double[] location;
    public final String uname;
    public boolean seeker;
    public boolean status;
    public int score;

    public Player(UUID uuid, String uname){
        this.uuid = uuid;
        this.uname = uname;
        this.location = new double[3];
        seeker = false;
        status = true;
    }

    public String toString(){
        return "Player<Name: "+this.uname+", UUID: "+this.uuid.toString()+">";
    }

    public double[] getLocation() {
        return new double[]{Math.toDegrees(location[0]),Math.toDegrees(location[1]),location[2]};
    }

    public void setLocation(double[] location) {
        this.location[0] = Math.toRadians(location[0]);
        this.location[1] = Math.toRadians(location[1]);
        this.location[2] = location[2];
    }

    /*
    *  double distance(Player p)
    *  input: Player object
    *  output: distance in metres
    *  Syntax: player.distance(player2)
    */

    public double distance(Player p)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        double lon1 = this.location[1];
        double lon2 = p.location[1];
        double lat1 = this.location[0];
        double lat2 = p.location[0];

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    /*
     *  double heading(Player p)
     *  input: Player object
     *  output: heading in degrees
     *  Syntax: player.heading(player2)
     */

    public double heading(Player p){
        double lat1 = this.location[0];
        double lat2 = p.location[0];
        double lon1 = this.location[1];
        double lon2 = p.location[1];
        return Math.toDegrees(Math.atan2(Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1),
                Math.sin(lon2-lon1)*Math.cos(lat2)) );

    }

    public double heading(double lat, double lon){
        double x = (Math.cos(lat) * Math.sin(lat)) -
                (Math.sin(lat) * Math.cos(lat) * Math.cos(lon-lat));
        double y = Math.sin(lon-this.location[1]) * Math.cos(lat);
        double h = Math.toDegrees(Math.atan2(y, x));
        return h;
    }

    public boolean equals(Object o){
        if(!(o instanceof Player)){
            return false;
        }
        return this.uuid == ((Player)o).uuid;
    }

}
