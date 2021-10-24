package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

public class EventPlayersProximity extends PersonalEvent{

    //send username of hider
    //send boolean true if proximity

    public String username;
    public boolean proximity;

    public EventPlayersProximity(String username, boolean proximity) {
        this.username = username;
        this.proximity = proximity;
    }


    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.username);
        b.writeBoolean(this.proximity);
    }

    @Override
    public void read(ByteBuf b) {
        this.username = NetworkUtils.readString(b);
        this.proximity = b.readBoolean();
    }

    @Override
    public void execute() {
    }
}
