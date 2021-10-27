package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

import java.util.UUID;

public class EventHiderProximity extends PersonalEvent{

    public UUID uuid;
    public boolean proximity;

    public EventHiderProximity(UUID uuid, boolean proximity) {
        this.proximity = proximity;
        this.uuid = uuid;
    }


    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.uuid.toString());
        b.writeBoolean(this.proximity);
    }

    @Override
    public void read(ByteBuf b) {
        this.uuid = UUID.fromString(NetworkUtils.readString(b));
        this.proximity = b.readBoolean();
    }

    @Override
    public void execute() {
    }
}
