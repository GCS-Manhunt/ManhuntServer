package player;

import java.util.UUID;
import static java.lang.Math.*;

public class Player {
    public final UUID uuid;
    private double[] location;
    public final String uname;
    public boolean seeker;
    public boolean status;

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
        double deltalat = p.getLocation()[0] - this.location[0];
        double deltalon = p.getLocation()[1] - this.location[1];
        double a = pow(sin(deltalat/2),2) + (cos(this.location[0]) * cos(p.getLocation()[0]) * pow(sin(deltalon/2),2));
        double c = 2 * atan2(sqrt(a), sqrt(1-a));
        double d = 6371000 * c;
        return d + abs(this.location[2] - p.location[3]);
    }

    /*
     *  double heading(Player p)
     *  input: Player object
     *  output: heading in degrees
     *  Syntax: player.heading(player2)
     */

    public double heading(Player p){
        double x = (cos(this.location[0]) * sin(p.getLocation()[0])) -
                (sin(this.location[0]) * cos(p.getLocation()[0]) * cos(p.getLocation()[1]-p.location[0]));
        double y = sin(p.getLocation()[1]-this.location[1]) * cos(p.getLocation()[0]);
        double h = toDegrees(atan2(y, x));
        return h;
    }

    public double heading(double lat, double lon){
        double x = (cos(lat) * sin(lat)) -
                (sin(lat) * cos(lat) * cos(lon-lat));
        double y = sin(lon-this.location[1]) * cos(lat);
        double h = toDegrees(atan2(y, x));
        return h;
    }

}
