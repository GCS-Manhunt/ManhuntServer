package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

public class EventAcceptConnection extends PersonalEvent
{
    public String gameName;
    public String location;
    public String time;
    public String[] rules;
    public int code;

    public EventAcceptConnection()
    {

    }

    public EventAcceptConnection(String gameName, String location, String time, String[] rules, int code)
    {
        this.gameName = gameName;
        this.location = location;
        this.time = time;
        this.rules = rules;
        this.code = code;
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
        b.writeInt(code);
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
        this.code = b.readInt();
    }

    @Override
    public void execute()
    {

    }
}