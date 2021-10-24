package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

public class EventAcceptConnection extends PersonalEvent
{
    public String gameName;
    public String location;
    public String time;
    public String[] rules;

    public EventAcceptConnection()
    {

    }

    public EventAcceptConnection(String gameName, String location, String time, String[] rules)
    {
        this.gameName = gameName;
        this.location = location;
        this.time = time;
        this.rules = rules;
    }

    @Override
    public void write(ByteBuf b)
    {
        NetworkUtils.writeString(b, gameName);
        NetworkUtils.writeString(b, location);
        NetworkUtils.writeString(b, time);
        b.writeInt(rules.length);

        for (String s: rules)
            NetworkUtils.writeString(b, s);
    }

    @Override
    public void read(ByteBuf b)
    {
        gameName = NetworkUtils.readString(b);
        location = NetworkUtils.readString(b);
        time = NetworkUtils.readString(b);
        rules = new String[b.readInt()];

        for (int i = 0; i < rules.length; i++)
        {
            rules[i] = NetworkUtils.readString(b);
        }
    }

    @Override
    public void execute()
    {

    }
}
