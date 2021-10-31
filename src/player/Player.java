package player;

import java.util.Arrays;
import java.util.UUID;
import static java.lang.Math.*;

public class Player {
    public final UUID uuid;
    private double[] location;
    public final String uname;
    public boolean seeker;
    public boolean status;
    public int code;

    public Player(UUID uuid, String uname){
        this.uuid = uuid;
        this.uname = uname;
        this.location = new double[3];
        seeker = false;
        status = true;
        code = -1;
    }

    public String toString(){
        return "Player<Name: "+this.uname+", UUID: "+this.uuid.toString()+">";
    }

    public double[] getLocation() {
        return new double[]{toDegrees(location[0]),toDegrees(location[1]),location[2]};
    }

    public void setLocation(double[] location) {
        this.location[0] = toRadians(location[0]);
        this.location[1] = toRadians(location[1]);
        this.location[2] = location[2];
    }

    /*
    *  double distance(Player p)
    *  input: Player object
    *  output: distance in metres
    *  Syntax: player.distance(player2)
    */

    public double distance(Player p){
        if(p == null || this.location == null || p.location == null){
            System.out.println("Distance Err!");
            return -3.1415926535;
        }
        double deltalat = p.getLocation()[0] - this.location[0];
        double deltalon = p.getLocation()[1] - this.location[1];
        double a = pow(sin(deltalat/2),2) + (cos(this.location[0]) * cos(p.getLocation()[0]) * pow(sin(deltalon/2),2));
        double c = 2 * atan2(sqrt(a), sqrt(1-a));
        double d = 6371000 * c;
        System.out.println(d + abs(this.location[2] - p.location[2]));
        return d + abs(this.location[2] - p.location[2]);
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
        return toDegrees(Math.atan2(Math.cos(lat1)*Math.sin(lat2)-Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1),
                Math.sin(lon2-lon1)*Math.cos(lat2)) );

    }

    public double heading(double lat, double lon){
        double x = (cos(lat) * sin(lat)) -
                (sin(lat) * cos(lat) * cos(lon-lat));
        double y = sin(lon-this.location[1]) * cos(lat);
        double h = toDegrees(atan2(y, x));
        return h;
    }

}
