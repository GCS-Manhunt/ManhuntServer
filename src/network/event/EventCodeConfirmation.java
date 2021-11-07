package network.event;

import io.netty.buffer.ByteBuf;
import network.NetworkUtils;

public class EventCodeConfirmation extends PersonalEvent{

    String confirmationString;

    public EventCodeConfirmation(String confirmationString) {
        this.confirmationString = confirmationString;
    }

    public EventCodeConfirmation() {

    }

    @Override
    public void write(ByteBuf b) {
        NetworkUtils.writeString(b, this.confirmationString);
    }

    @Override
    public void read(ByteBuf b) {
        NetworkUtils.writeString(b, this.confirmationString);
    }

    @Override
    public void execute() {
    }
}

