package network.event;

import io.netty.buffer.ByteBuf;
import main.Main;
import player.Player;

import java.util.UUID;

public class EventSendLocation extends PersonalEvent{
    double longitude;
    double latitude;
    double altitude;
    public EventSendLocation() {

    }
    public EventSendLocation(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    @Override
    public void write(ByteBuf b) {
        b.writeDouble(this.longitude);
        b.writeDouble(this.latitude);
        b.writeDouble(this.altitude);
    }

    @Override
    public void read(ByteBuf b) {
        this.longitude = b.readDouble();
        this.latitude = b.readDouble();
        this.altitude = b.readDouble();
    }

    @Override
    public void execute() {
        UUID uuid = this.clientID;
        Player player = Main.engine.hiders.playerList.get(uuid);
        if(player == null) {
            player = Main.engine.seekers.playerList.get(uuid);
        }
        if(player == null) {
            return;
        }
        double[] location_double = new double[3];
        location_double[0] = this.longitude;
        location_double[1] = this.latitude;
        location_double[2] = this.altitude;
        player.setLocation(location_double);
    }
}
