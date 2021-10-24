package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;
import network.ServerHandler;

public class EventSendPlayerIdentity extends PersonalEvent implements IServerThreadEvent
{
    public String username;

    public EventSendPlayerIdentity(String username)
    {
        this.username = username;
    }

    public EventSendPlayerIdentity()
    {

    }

    @Override
    public void write(ByteBuf b)
    {
        NetworkUtils.writeString(b, username);
    }

    @Override
    public void read(ByteBuf b)
    {
        this.username = NetworkUtils.readString(b);
    }

    @Override
    public void execute()
    {

    }

    @Override
    public void execute(ServerHandler s)
    {
        s.sendEventAndClose(new EventKick("You are in! " + username));
    }
}
