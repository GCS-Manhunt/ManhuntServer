package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

import java.util.UUID;

public class EventSeekerProximity extends PersonalEvent{

    public String username;
    public UUID uuid;
    public boolean proximity;

    public EventSeekerProximity(String username, UUID uuid, boolean proximity) {
        this.username = username;
        this.proximity = proximity;
        this.uuid = uuid;
    }


    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.username);
        NetworkUtils.writeString(b, this.uuid.toString());
        b.writeBoolean(this.proximity);
    }

    @Override
    public void read(ByteBuf b) {
        this.username = NetworkUtils.readString(b);
        this.uuid = UUID.fromString(NetworkUtils.readString(b));
        this.proximity = b.readBoolean();
    }

    @Override
    public void execute() {
    }
}
