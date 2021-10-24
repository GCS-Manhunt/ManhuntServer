package network.event;

import io.netty.buffer.ByteBuf;

public class EventSendHeading implements INetworkEvent
{

    public double heading;

    public EventSendHeading() {}

    public EventSendHeading(int heading){
        this.heading = heading;
    }

    @Override
    public void write(ByteBuf b)
    {
        b.writeDouble(this.heading);
    }

    @Override
    public void read(ByteBuf b) {}

    @Override
    public void execute() {}

}

