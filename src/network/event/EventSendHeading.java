package network.event;

import io.netty.buffer.ByteBuf;
import main.Main;
import network.NetworkUtils;
import network.ServerHandler;
import player.Player;

import java.util.UUID;

public class EventSendHeading implements INetworkEvent
{

    public double heading;
    public UUID uuid;

    public EventSendHeading() {}

    public EventSendHeading(double heading, UUID uuid)
    {
        this.heading = heading;
        this.uuid = uuid;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.heading);
        NetworkUtils.writeString(b, uuid.toString());
    }

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void execute() {}

}

