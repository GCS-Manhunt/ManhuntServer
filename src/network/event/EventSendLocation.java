package network.event;

import io.netty.buffer.ByteBuf;

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

    }
}
